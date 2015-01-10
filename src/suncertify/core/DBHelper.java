package suncertify.core;

import static suncertify.db.domain.DBMetaInfo.FIELDS_COUNT;
import static suncertify.db.domain.DBMetaInfo.FIELD_LENGTHS;
import static suncertify.db.domain.DBMetaInfo.FIELD_NAMES;
import static suncertify.db.domain.DBMetaInfo.FIRST_RECORD_NO;
import static suncertify.db.domain.DBMetaInfo.FLAG_LENGTH;
import static suncertify.db.domain.DBMetaInfo.MAGIC_COOKIE;
import static suncertify.db.domain.DBMetaInfo.RECORD_LENGTH;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import suncertify.db.domain.Contractor;
import suncertify.db.domain.RecordFlagEnum;

/**
 * <code>DBHelper</code> is a low-level operations database helper. This class
 * provides many useful mathods that operate on databse file (loading, storage),
 * which operate on raw bytes rather than objects (that's why they are all
 * low-level).
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public final class DBHelper {
	/**
	 * DB file.
	 */
	private RandomAccessFile dbFile;

	/**
	 * DB file channel.
	 */
	private FileChannel dbFileChannel;

	/**
	 * DB file lock.
	 */
	private FileLock dbFileLock;

	/**
	 * Offset of the first record in DB file.
	 */
	private final long firstRecordOffset;

	/**
	 * Constructor - its task is to read meta info and schema from DB file - if
	 * database file does not exists this constructor throws initialization
	 * exception - i.e. {@link InitializationException}.
	 * 
	 * @param dbFileName
	 *            String - DB file name.
	 * 
	 * @throws InitializationException
	 *             If any {@link Exception} occurs during helper initialization.
	 */
	public DBHelper(final String dbFileName) throws InitializationException {
		try {
			File file = new File(dbFileName);
			if (!file.exists()) {
				throw new InitializationException(
						"Database file does not exist");
			}
			dbFile = new RandomAccessFile(file, "rws");
			// Get a file channel for the file
			dbFileChannel = dbFile.getChannel();
			// Try acquiring the lock without blocking.
			dbFileLock = dbFileChannel.tryLock();
			dbFile.seek(0);

			// Check if file is valid
			if (dbFile.readInt() != MAGIC_COOKIE) {
				throw new IOException("File '" + dbFileName
						+ "' is not valid database file.");
			}
			// Check file meta info
			if (dbFile.readInt() != RECORD_LENGTH
					|| dbFile.readShort() != FIELDS_COUNT) {

				throw new IOException("File '" + dbFileName
						+ "' contains invalid meta info.");
			}

			/*
			 * Read db schema
			 */
			String[] fieldNames = new String[FIELDS_COUNT];
			short[] fieldLengths = new short[FIELDS_COUNT];

			for (int i = 0; i < FIELDS_COUNT; ++i) {
				short fieldLength = dbFile.readShort();
				byte[] bytes = new byte[fieldLength];

				dbFile.read(bytes);
				fieldNames[i] = new String(bytes);
				fieldLengths[i] = dbFile.readShort();
			}

			// Check schema meta info
			if (!Arrays.equals(fieldNames, FIELD_NAMES)
					|| !Arrays.equals(fieldLengths, FIELD_LENGTHS)) {

				throw new IOException("File '" + dbFileName
						+ "' contains invalid data meta info.");
			}

			firstRecordOffset = dbFile.getFilePointer();

		} catch (Exception e) {
			throw new InitializationException(e.getMessage() + "; "
					+ dbFileName, e);
		}
	}

	/**
	 * Closes database file.
	 */
	public void close() {
		try {
			// Release the lock
			dbFileLock.release();
			// Close the file
			dbFileChannel.close();
			dbFile.close();
		} catch (IOException e) {
			// ignore
		}
	}

	/**
	 * @see java.lang.Object#finalize()
	 */
	public void finalize() throws Throwable {
		close();
		super.finalize();
	}

	/**
	 * Loads all records from database file.
	 * 
	 * @return Map&lt;Integer, Contractor&gt; - map of all records loaded from
	 *         database file (key: record no; value: record).
	 * @throws IOException
	 *             If read operation fails.
	 */
	public Map<Integer, Contractor> loadAllRecords() throws IOException {
		seekToRecord(FIRST_RECORD_NO);
		Map<Integer, Contractor> map = new HashMap<Integer, Contractor>();

		try {
			int recIdx = FIRST_RECORD_NO;
			while (true) {
				byte flag = dbFile.readByte();

				// read record's data
				String[] values = new String[FIELDS_COUNT];
				for (int i = 0; i < FIELDS_COUNT; ++i) {
					byte[] bytes = new byte[FIELD_LENGTHS[i]];
					dbFile.read(bytes);

					values[i] = new String(bytes);
				}

				Contractor record = new Contractor(values);
				record.setId(recIdx++);
				record.setFlag(RecordFlagEnum.values()[flag]);
				map.put(record.getId(), record);
			}

		} catch (EOFException e) {
			// XXX: ignore it - this exception exits from infinite loop
		}
		return map;
	}

	/**
	 * Reads record with given record no.
	 * 
	 * @param recordNo
	 *            int - the number of record to read.
	 * @return Contractor - {@link Contractor} object representing record in DB.
	 * @throws IOException
	 *             If method encounters IO exception while processing DB file.
	 * @throws IllegalArgumentException
	 *             If given record no is less than
	 *             {@link suncertify.db.domain.DBMetaInfo#FIRST_RECORD_NO}
	 */
	public Contractor loadRecord(final int recordNo) throws IOException,
			IllegalArgumentException {

		if (recordNo < FIRST_RECORD_NO) {
			throw new IllegalArgumentException(
					"Record number must be greater or equal to "
							+ FIRST_RECORD_NO + " - passed value: " + recordNo);
		}

		seekToRecord(recordNo);

		byte flag = dbFile.readByte();

		// read record's data
		String[] values = new String[FIELDS_COUNT];
		for (int i = 0; i < FIELDS_COUNT; ++i) {
			byte[] bytes = new byte[FIELD_LENGTHS[i]];
			dbFile.read(bytes);

			values[i] = new String(bytes);
		}

		Contractor record = new Contractor(values);
		record.setId(recordNo);
		record.setFlag(RecordFlagEnum.values()[flag]);
		return record;
	}

	/**
	 * Moves given file pointer to given record number. After execution of this
	 * method file pointer is set on the first byte of the record with given no.
	 * 
	 * @param recordNo
	 *            int - the record no.
	 * @throws IOException
	 *             If method encounters IO exception while processing DB file.
	 */
	private void seekToRecord(final int recordNo) throws IOException {
		// (recordNo - FIRST_RECORD_NO) - counting starts from FIRST_RECORD_NO
		dbFile
				.seek(firstRecordOffset
						+ ((recordNo - FIRST_RECORD_NO) * (RECORD_LENGTH + FLAG_LENGTH)));
	}

	/**
	 * Stores given record to database file without moving file pointer (stores
	 * record in the current file position).
	 * 
	 * @param record
	 *            Contractor - record to be stored to database file.
	 * @throws IOException
	 *             If storage operation fails.
	 */
	private void storeNext(final Contractor record) throws IOException {
		dbFile.writeByte(record.getFlag().ordinal());
		for (String datum : record.toStrings()) {
			dbFile.write(datum.getBytes());
		}
	}

	/**
	 * Stores given record to database file.
	 * 
	 * @param record
	 *            Contractor - record to be stored to database file.
	 * @throws IOException
	 *             If storage operation fails.
	 */
	public void storeRecord(final Contractor record) throws IOException {
		seekToRecord(record.getId());
		storeNext(record);
	}
}
