package suncertify.db;

/**
 * <code>DBMain</code> consists of basic low-level CRUD (Create, Read, Update,
 * Delete) operations allowing users to perform operations on database - it also
 * allows user to lock/unlock concrete record in the database.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public interface DBMain {
	/**
	 * Reads a record with given no from the database.
	 * 
	 * @param recNo
	 *            int - the record no to be read.
	 * @return String[] - the array where each element is a record value.
	 * @throws RecordNotFoundException
	 *             If record with given no does not exist in database.
	 */
	public String[] read(final int recNo) throws RecordNotFoundException;

	/**
	 * Modifies the fields of a record with given no. The new value for field n
	 * appears in data[n].
	 * 
	 * @param recNo
	 *            int - the record no to be changed.
	 * @param data
	 *            String[] - the updated data of the record to be stored into
	 *            database.
	 * @throws RecordNotFoundException
	 *             If record with given no does not exist in database or record
	 *             with given name and location already exists in database or
	 *             system was unable to perform IO operation on database file.
	 */
	public void update(final int recNo, final String[] data)
			throws RecordNotFoundException;

	/**
	 * Deletes a record, making the record number and associated disk storage
	 * available for reuse.
	 * 
	 * @param recNo
	 *            int - no of the record to be deleted.
	 * @throws RecordNotFoundException
	 *             If record with given no does not exist in database or system
	 *             was unable to perform IO operation on database file.
	 */
	public void delete(final int recNo) throws RecordNotFoundException;

	/**
	 * Returns an array of record numbers that match the specified criteria.
	 * 
	 * @param criteria
	 *            String[] - the field n in the database file is described by
	 *            criteria[n]. A null value in criteria[n] matches any field
	 *            value. A non-null value in criteria[n] matches any field value
	 *            that begins with criteria[n]. (For example, "Fred" matches
	 *            "Fred" or "Freddy".)
	 * 
	 * @return int[] - the array of record numbers that match the specified
	 *         criteria
	 * @throws RecordNotFoundException
	 *             If no record matches specified criteria.
	 */
	public int[] find(final String[] criteria) throws RecordNotFoundException;

	/**
	 * Creates a new record in the database (possibly reusing a deleted entry).
	 * Inserts the given data, and returns the record number of the new record.
	 * 
	 * @param data
	 *            String[] - the data of the new record to be inserted into
	 *            database.
	 * @return int - the record number of the new record.
	 * @throws DuplicateKeyException
	 *             If record with given name and location already exists in
	 *             database or system was unable to perform IO operation on
	 *             database file.
	 */
	public int create(final String[] data) throws DuplicateKeyException;

	/**
	 * Locks a record with given no so that it can only be updated or deleted by
	 * this client. If the specified record is already locked, the current
	 * thread gives up the CPU and consumes no CPU cycles until the record is
	 * unlocked.
	 * 
	 * @param recNo
	 *            int - the no of the record to be locked.
	 * @throws RecordNotFoundException
	 *             If record with given no does not exist in database.
	 */
	public void lock(final int recNo) throws RecordNotFoundException;

	/**
	 * Releases the lock on a record with given no.
	 * 
	 * @param recNo
	 *            int - the no of the record to be unlocked.
	 * @throws RecordNotFoundException
	 *             If record with given no does not exist in database.
	 */
	public void unlock(final int recNo) throws RecordNotFoundException;

	/**
	 * Determines if a record with given no is currenly locked.
	 * 
	 * @param recNo
	 *            int - the no of the record to be checked.
	 * @return boolean - <code>true</code> if the record is locked,
	 *         <code>false</code> otherwise.
	 * @throws RecordNotFoundException
	 *             If record with given no does not exist in database.
	 */
	public boolean isLocked(final int recNo) throws RecordNotFoundException;
}
