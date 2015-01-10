package suncertify.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import suncertify.core.DBHelper;
import suncertify.core.InitializationException;
import suncertify.db.domain.Contractor;
import suncertify.db.domain.DBMetaInfo;
import suncertify.db.domain.RecordFlagEnum;

/**
 * <code>Data</code> is an implementation of {@link DBMain} class.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see suncertify.db.DBMain
 * @see suncertify.db.DBPersistent
 */
public class Data implements DBMain, DBPersistent<Contractor> {
	/**
	 * Flag enum for db CUD (Create, Update, Delete) operation type.
	 * 
	 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
	 */
	private enum DBOperationEnum {
		CREATE, UPDATE, DELETE;
	}

	/**
	 * All records (valid and invalid) map.
	 */
	private Map<Integer, Contractor> recordMap;

	/**
	 * Invalid records map.
	 */
	private Map<Integer, Contractor> invalidRecordMap;

	/**
	 * Locked record's keys set.
	 */
	private Set<Integer> lockedRecords;

	/**
	 * Database helper.
	 */
	private DBHelper dbHelper;

	/**
	 * Creates instance of this class with given database file. This is an instance of class that
	 * implements {@link DBMain} class.
	 * 
	 * @param dbFilename
	 *            String - DB file name - see {@link DBHelper}.
	 * @throws InitializationException
	 *             see {@link DBHelper#DBHelper(String)}
	 * @see DBMain
	 */
	public Data(final String dbFilename) throws InitializationException {
		this.dbHelper = new DBHelper(dbFilename);
		invalidRecordMap = Collections.synchronizedMap(new HashMap<Integer, Contractor>());
		lockedRecords = Collections.synchronizedSet(new HashSet<Integer>());

		try {
			recordMap = dbHelper.loadAllRecords();

			for (Contractor record : recordMap.values()) {
				if (record.getFlag() == RecordFlagEnum.INVALID) {
					invalidRecordMap.put(record.getId(), record);
				}
			}
		} catch (IOException e) {
			recordMap = Collections.synchronizedMap(new HashMap<Integer, Contractor>());
		}
	}

	/**
	 * Returns record from db and checks if this record is valid.
	 * 
	 * @param recNo
	 *            int - no of record to read.
	 * @return Contractor - record read from db.
	 * @throws RecordNotFoundException
	 *             If record with given no was not found in database.
	 */
	private Contractor readRecord(final int recNo) throws RecordNotFoundException {
		Contractor record = null;

		synchronized (recordMap) {
			record = recordMap.get(recNo);
		}

		if (record == null) {
			throw new RecordNotFoundException("Record with no >" + recNo + "< cannot be found.");
		}

		if (record.getFlag() == RecordFlagEnum.INVALID) {
			throw new RecordNotFoundException("Record with no >" + record.getId() + "< is not a valid record.");
		}

		return record;
	}

	/**
	 * @see suncertify.db.DBMain#read(int)
	 */
	public String[] read(final int recNo) throws RecordNotFoundException {
		return readRecord(recNo).toStrings();
	}

	/**
	 * @see suncertify.db.DBPersistent#readAll()
	 */
	public synchronized List<Contractor> readAll() {
		List<Contractor> list = new ArrayList<Contractor>(recordMap.values());
		list.removeAll(invalidRecordMap.values());
		return list;
	}

	/**
	 * @see suncertify.db.DBMain#update(int, java.lang.String[])
	 */
	public void update(final int recNo, final String[] data) throws RecordNotFoundException {
		try {
			lock(recNo);
			Contractor record = readRecord(recNo);
			record = new Contractor(data);
			record.setId(recNo);

			execute(DBOperationEnum.UPDATE, record);
		} catch (DBException e) {
			throw new RecordNotFoundException(e.getMessage(), e);

		} finally {
			unlock(recNo);
		}
	}

	/**
	 * @see suncertify.db.DBMain#delete(int)
	 */
	public void delete(final int recNo) throws RecordNotFoundException {
		try {
			lock(recNo);
			Contractor record = readRecord(recNo);
			record.setFlag(RecordFlagEnum.INVALID);

			execute(DBOperationEnum.DELETE, record);
		} catch (DBException e) {
			throw new RecordNotFoundException(e.getMessage(), e);

		} finally {
			unlock(recNo);
		}
	}

	/**
	 * @see suncertify.db.DBMain#find(java.lang.String[])
	 */
	public int[] find(final String[] criteria) throws RecordNotFoundException {
		// check params validity
		if (criteria.length != DBMetaInfo.FIELDS_COUNT) {
			throw new IllegalArgumentException("Size of criteria array (" + criteria.length + ") must equal "
					+ DBMetaInfo.FIELDS_COUNT + ".");
		}

		List<Contractor> recordList = readAll();
		List<Integer> list = new ArrayList<Integer>();
		// search for matching objects
		for (Contractor record : recordList) {
			boolean matches = true;

			for (int k = 0; k < record.toStrings().length; ++k) {
				if (criteria[k] != null) {
					// one unfullfilled criteria in a single record excludes
					// this record from the result list
					String datum = record.toStrings()[k].trim().toUpperCase();
					if (!datum.startsWith(criteria[k].trim().toUpperCase())) {
						matches = false;
						break;
					}
				}
			}
			// add record to the list if it matches criteria
			if (matches) {
				list.add(record.getId());
			}
		}

		// no records found
		if (list.size() <= 0) {
			throw new RecordNotFoundException("No records matching " + "given criteria found.");
		}

		// converting list into int array
		int[] returnArray = new int[list.size()];
		for (int i = 0; i < returnArray.length; ++i) {
			returnArray[i] = list.get(i);
		}

		return returnArray;
	}

	/**
	 * @see suncertify.db.DBMain#create(java.lang.String[])
	 */
	public synchronized int create(final String[] data) throws DuplicateKeyException {
		int recNo = getNextRecordNo();

		Contractor record = new Contractor(data);
		record.setId(recNo);

		try {
			execute(DBOperationEnum.CREATE, record);
		} catch (DBException e) {
			throw new DuplicateKeyException(e.getMessage(), e);
		}

		return recNo;
	}

	/**
	 * Checks data integrity i.e. checks if given record does not violate db constraints
	 * (uniqueness).
	 * 
	 * @param record
	 *            Contractor - method checks if this given record violates db constraints.
	 * @throws DuplicateKeyException
	 *             If given record violates uniqueness constraints.
	 */
	private synchronized void checkDataIntegrity(final Contractor record) throws DuplicateKeyException {
		if (recordMap.containsValue(record) && !invalidRecordMap.containsValue(record)) {

			if (!record.equals(recordMap.get(record.getId()))) {
				throw new DuplicateKeyException("Record: >" + record + "< already exists in database.");
			}
		}
	}

	/**
	 * Returns first free to use record no (id) - it can be id of invalid record or completely new
	 * one (if no invalid record exists in database).
	 * 
	 * @return int - free record no that can be used to assign it to a newly created record.
	 */
	private int getNextRecordNo() {
		if (invalidRecordMap.values().size() > 0) {
			return invalidRecordMap.keySet().iterator().next();
		}

		int max = DBMetaInfo.FIRST_RECORD_NO;
		for (int i : recordMap.keySet()) {
			if (i >= max) {
				max = i + 1;
			}
		}

		return max;
	}

	/**
	 * Tries to find given record in given synchronized map. If given record was not found this
	 * method throws RecordNotFoundException.
	 * 
	 * @param map
	 *            Map&lt;Integer, Contractor&gt; - synchronized map in which given record will be
	 *            looked up.
	 * @param recNo
	 *            int - no of record to be looked up in given map.
	 * @throws RecordNotFoundException
	 *             If given record was not found in given map.
	 */
	private void findRecordInMapByKey(final Map<Integer, Contractor> map, final int recNo)
			throws RecordNotFoundException {

		synchronized (map) {
			if (!map.containsKey(recNo)) {
				throw new RecordNotFoundException("Record with given no >" + recNo + "< does not exist.");
			}
		}
	}

	/**
	 * @see suncertify.db.DBMain#lock(int)
	 */
	public void lock(final int recNo) throws RecordNotFoundException {
		synchronized (lockedRecords) {
			while (isLocked(recNo)) {
				try {
					lockedRecords.wait();
				} catch (InterruptedException e) {
					lockedRecords.notify();
				}
			}
			lockedRecords.add(recNo);
		}
	}

	/**
	 * @see suncertify.db.DBMain#unlock(int)
	 */
	public void unlock(final int recNo) throws RecordNotFoundException {
		findRecordInMapByKey(recordMap, recNo);

		synchronized (lockedRecords) {
			lockedRecords.remove(recNo);
			lockedRecords.notify();
		}
	}

	/**
	 * @see suncertify.db.DBMain#isLocked(int)
	 */
	public boolean isLocked(final int recNo) throws RecordNotFoundException {
		findRecordInMapByKey(recordMap, recNo);

		synchronized (lockedRecords) {
			return lockedRecords.contains(recNo);
		}
	}

	/**
	 * Executes chosen operation on given record and stores the result (if needed) into database.
	 * 
	 * @throws DuplicateKeyException
	 *             If operation violated data integrity in database.
	 * @throws DBException
	 *             If system was unable to perform IO operation on database file.
	 */
	private void execute(DBOperationEnum operation, Contractor record) throws DuplicateKeyException, DBException {
		try {
			if (operation != DBOperationEnum.DELETE) {
				checkDataIntegrity(record);
			}

			// update recordMap
			recordMap.put(record.getId(), record);

			if (operation == DBOperationEnum.DELETE) {
				invalidRecordMap.put(record.getId(), record);
			} else {
				invalidRecordMap.remove(record.getId());
			}

			dbHelper.storeRecord(record);

		} catch (IOException e) {
			throw new DBException("Unable to commit IO operation on database file.", e);
		}
	}
}