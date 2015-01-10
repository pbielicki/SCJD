package suncertify.db.dao.impl;

import static suncertify.db.dao.ComparisonOperator.EQUAL;
import static suncertify.db.dao.ComparisonOperator.GREATER;
import static suncertify.db.dao.ComparisonOperator.GREATER_OR_EQUAL;
import static suncertify.db.dao.ComparisonOperator.LESS;
import static suncertify.db.dao.ComparisonOperator.LESS_OR_EQUAL;
import static suncertify.db.dao.ComparisonOperator.CONTAINS;
import static suncertify.db.dao.ComparisonOperator.NOT_EQUAL;
import static suncertify.db.dao.ComparisonOperator.STARTS_WITH;
import static suncertify.db.domain.DBMetaInfo.getFieldType;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import suncertify.core.InitializationException;
import suncertify.db.DBPersistent;
import suncertify.db.Data;
import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.dao.ComparisonOperator;
import suncertify.db.dao.DaoDuplicateException;
import suncertify.db.dao.DaoException;
import suncertify.db.dao.DaoFinderException;
import suncertify.db.dao.DaoInitializationException;
import suncertify.db.dao.DaoStaleObjectException;
import suncertify.db.dao.IContractorDao;
import suncertify.db.dao.IDao;
import suncertify.db.dao.comparator.ComparatorFactory;
import suncertify.db.dao.comparator.IComparator;
import suncertify.db.domain.Contractor;

/**
 * <code>ContractorLocalDao</code> is a local (operates on file - indirectly) implementation of
 * {@link IContractorDao} interface.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see suncertify.db.dao.IContractorDao
 */
public class ContractorLocalDao implements IContractorDao {
	/**
	 * Database operator object.
	 */
	private DBPersistent<Contractor> db;

	/**
	 * Constructs <code>ContractorLocalDao</code> instance basing on given database filename.
	 * 
	 * @throws DaoInitializationException -
	 *             see {@link Data#Data(String)}.
	 */
	public ContractorLocalDao(String dbFilename) throws DaoInitializationException {
		super();
		try {
			db = new Data(dbFilename);
		} catch (InitializationException e) {
			throw new DaoInitializationException(e.getMessage(), e);
		}
	}

	/**
	 * @see IDao#findById(int)
	 */
	public Contractor findById(int id) throws DaoFinderException, RemoteException {
		try {
			return new Contractor(db.read(id));
		} catch (RecordNotFoundException e) {
			throw new DaoFinderException("Record with given id (" + id + ") was not found in the persistent layer.", e);
		}
	}

	/**
	 * @see IDao#load()
	 */
	public List<Contractor> load() throws RemoteException {
		return db.readAll();
	}

	/**
	 * @see IDao#load(String[])
	 */
	public List<Contractor> load(final String[] filter) throws DaoFinderException, DaoException, RemoteException {
		ComparisonOperator[] operators = new ComparisonOperator[filter.length];
		Arrays.fill(operators, STARTS_WITH);

		return load(filter, operators);
	}

	/**
	 * @see IDao#load(String[], ComparisonOperator[])
	 */
	public List<Contractor> load(final String[] filter, final ComparisonOperator[] operators)
			throws DaoFinderException, DaoException, RemoteException {

		String[] tmp = new String[filter.length];
		for (int i = 0; i < filter.length; ++i) {
			if (operators[i] == STARTS_WITH) {
				tmp[i] = filter[i];
			} else {
				tmp[i] = null;
			}
		}

		// search for record's ids
		int[] ids;
		try {
			ids = db.find(tmp);
		} catch (RecordNotFoundException e) {
			throw new DaoFinderException("No records matching given criteria found.", e);
		}

		List<Contractor> list = new ArrayList<Contractor>();
		// load found records from database
		try {
			for (int id : ids) {
				Contractor record = new Contractor(db.read(id));
				record.setId(id);
				if (matches(record, filter, operators)) {
					list.add(record);
				}
			}
		} catch (RecordNotFoundException e) {
			throw new DaoException("Could not load record(s) from the persistent layer.");
		}

		if (list.size() == 0) {
			throw new DaoFinderException("No records matching given criteria found.");
		}

		return list;
	}

	/**
	 * Checks if given arguments meet given comparison conditions.
	 * 
	 * @param arg1
	 *            String - first argument of comparison (left side of equation).
	 * @param arg2
	 *            String - second argument of comparison (right side of equation).
	 * @param operator
	 *            ComparisonOperator - comparison operator.
	 * @return boolean - <code>true</code> if given arguments meet given comparison operator and
	 *         <code>false</code> otherwise.
	 *         
	 * @see IComparator
	 * @see ComparisonOperator
	 */
	private boolean matches(IComparator comparator, String arg1, String arg2, ComparisonOperator operator) {
		// <
		if (LESS == operator) {
			return comparator.compare(arg1, arg2) < 0;

		// <=
		} else if (LESS_OR_EQUAL == operator) {
			return comparator.compare(arg1, arg2) <= 0;

		// =
		} else if (EQUAL == operator) {
			return comparator.compare(arg1, arg2) == 0;

		// >=
		} else if (GREATER_OR_EQUAL == operator) {
			return comparator.compare(arg1, arg2) >= 0;

		// >
		} else if (GREATER == operator) {
			return comparator.compare(arg1, arg2) > 0;

		// !=
		} else if (NOT_EQUAL == operator) {
			return comparator.compare(arg1, arg2) != 0;
			
		// contains
		} else if (CONTAINS == operator) {
			String[] strings1 = arg1.split(",");
			String[] strings2 = arg2.split(",");
			
			Set<String> set1 = new HashSet<String>();
			Set<String> set2 = new HashSet<String>();
	
			for (int i = 0; i < strings1.length; ++i) {
				if (strings1[i].trim().length() > 0) {
					set1.add(strings1[i].trim().toUpperCase());
				}
			}
			
			for (int i = 0; i < strings2.length; ++i) {
				if (strings2[i].trim().length() > 0) {
					set2.add(strings2[i].trim().toUpperCase());
				}
			}
			
			set2.removeAll(set1);
			return set2.size() == 0;
		}
		return false;
	}

	/**
	 * Checks if given contractor's data match given filter.
	 * 
	 * @param record
	 *            Contractor - contractor to be checked against filter.
	 * @param filter
	 *            String[] - data filter.
	 * @param operators
	 *            ComparisonOperator[] - comparison operators for each field.
	 * @return boolean - <code>true</code> if given contractor's numeric data match given filter
	 *         and <code>false</code> otherwise.
	 *         
	 * @see IComparator
	 * @see ComparisonOperator
	 */
	private boolean matches(Contractor record, String[] filter, ComparisonOperator[] operators) {
		for (int i = 0; i < filter.length; ++i) {
			if (operators[i] != STARTS_WITH && filter[i] != null) {
				IComparator comparator = ComparatorFactory.getComparator(getFieldType(i));
				
				String[] strings = record.toStrings();
				if (!matches(comparator, strings[i], filter[i], operators[i])) {
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * @see IDao#refresh(T)
	 */
	public Contractor refresh(Contractor persistent) throws DaoStaleObjectException, RemoteException {
		try {
			Contractor record = new Contractor(db.read(persistent.getId()));
			record.setId(persistent.getId());
			return record;
		} catch (RecordNotFoundException e) {
			throw new DaoStaleObjectException("Record no longer exists in the persistent layer.");
		}
	}

	/*
	 * @see IDao#remove(T)
	 */
	public void remove(Contractor persistent) throws DaoStaleObjectException, DaoException, RemoteException {
		try {
			db.delete(persistent.getId());
		} catch (RecordNotFoundException e) {
			Throwable cause = e.getCause();
			if (cause == null || cause instanceof RecordNotFoundException) {
				throw new DaoStaleObjectException("Could not find record to remove in the persistent layer.");
			}

			throw new DaoException("Could not remove record from the persistent layer.");
		}
	}

	/*
	 * @see IDao#store(T)
	 */
	public int store(Contractor persistent) throws DaoDuplicateException, DaoException, RemoteException {
		try {
			return db.create(persistent.toStrings());
		} catch (DuplicateKeyException e) {
			Throwable cause = e.getCause();
			if (cause == null || cause instanceof DuplicateKeyException) {
				throw new DaoDuplicateException("Could not store duplicate object in the persistent layer.", e);
			}

			throw new DaoException("Could not store object in the persistent layer.", e);
		}
	}

	/*
	 * @see IDao#update(T)
	 */
	public void update(Contractor persistent) throws DaoStaleObjectException, DaoException, RemoteException {
		try {
			db.update(persistent.getId(), persistent.toStrings());
		} catch (RecordNotFoundException e) {
			Throwable cause = e.getCause();
			if (cause == null || cause instanceof RecordNotFoundException) {
				throw new DaoStaleObjectException("Could not find record to update in the persistent layer.");
			}

			if (cause instanceof DuplicateKeyException) {
				throw new DaoDuplicateException("Could not update duplicate object in the persistent layer.", e);
			}

			throw new DaoException("Could not update record in the persistent layer.");
		}
	}
}
