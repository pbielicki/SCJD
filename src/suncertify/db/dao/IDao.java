package suncertify.db.dao;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import suncertify.db.domain.Persistent;

/**
 * <code>IDao</code> interface consists of common Data Access Object methods for CRUD (Create,
 * Read, Update, Delete). This interface can be parametrized by the classes of persistent object -
 * i.e. all classes that extends {@link Persistent} class.<br>
 * <code>IDao</code> interface extends {@link java.rmi.Remote} interface as it can be used locally
 * as well as remotely - see {@link java.rmi.Remote} for details.<br>
 * <br>
 * This interface utilizes <i>Data Access Object</i> design pattern.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public interface IDao<T extends Persistent> extends Remote {
	/**
	 * Finds persistent object with given id in the persistent layer.
	 * 
	 * @param id
	 *            int - id of object to find.
	 * @return T - found persistent object.
	 * @throws DaoFinderException
	 *             If persistent object with given id cannot be found.
	 * @throws RemoteException
	 *             If remote method cannot be invoked - see javadoc for {@link RemoteException} for
	 *             details.
	 */
	public T findById(final int id) throws DaoFinderException, RemoteException;

	/**
	 * Loads the list of persistent objects from the persistent layer.
	 * 
	 * @return List&lt;T&gt; - the list of persistent objects.
	 * @throws RemoteException
	 *             If remote method cannot be invoked - see javadoc for {@link RemoteException} for
	 *             details.
	 */
	public List<T> load() throws RemoteException;

	/**
	 * Loads persistent objects from the persistent layer basing on given filter(s) and relevant
	 * comparison operators.
	 * 
	 * @param filter
	 *            String[] - finder filter.
	 * @param operators
	 *            ComparisonOperator[] - comparison operators for each filter field.
	 * @return List&lt;T&gt; - the list of found persistent objects.
	 * @throws DaoFinderException
	 *             If no object was found on given filter.
	 * @throws DaoException
	 *             If general database failure occurs.
	 * @throws RemoteException
	 *             If remote method cannot be invoked - see javadoc for {@link RemoteException} for
	 *             details.
	 * 
	 * @see suncertify.db.DBMain#find(String[])
	 */
	public List<T> load(final String[] filter, final ComparisonOperator[] operators) throws DaoFinderException,
			DaoException, RemoteException;

	/**
	 * Loads persistent objects from the persistent layer basing on given filter(s) and default
	 * comparison operators.
	 * 
	 * @param filter
	 *            String[] - finder filter.
	 * @return List&lt;T&gt; - the list of found persistent objects.
	 * @throws DaoFinderException
	 *             If no object was found on given filter.
	 * @throws DaoException
	 *             If general database failure occurs.
	 * @throws RemoteException
	 *             If remote method cannot be invoked - see javadoc for {@link RemoteException} for
	 *             details.
	 * 
	 * @see suncertify.db.DBMain#find(String[])
	 */
	public List<T> load(final String[] filter) throws DaoFinderException, DaoException, RemoteException;

	/**
	 * Refreshes given object's data from the persistent layer.
	 * 
	 * @param persistent
	 *            T - object to be refreshed.
	 * @return T - refreshed object from the persistent layer.
	 * @throws DaoStaleObjectException
	 *             If given object longer exists in the persistent layer (thus cannot be refreshed).
	 * @throws RemoteException
	 *             If remote method cannot be invoked - see javadoc for {@link RemoteException} for
	 *             details.
	 */
	public T refresh(final T persistent) throws DaoStaleObjectException, RemoteException;

	/**
	 * Removes given object from the persistent layer.
	 * 
	 * @param persistent
	 *            T - object to be removed from the persistent layer.
	 * @throws DaoStaleObjectException
	 *             If given object is outdated or no longer exists in the persistent layer.
	 * @throws DaoException
	 *             If general database failure occurs.
	 * @throws RemoteException
	 *             If remote method cannot be invoked - see javadoc for {@link RemoteException} for
	 *             details.
	 */
	public void remove(final T persistent) throws DaoStaleObjectException, DaoException, RemoteException;

	/**
	 * Stores given object into persistent layer.
	 * 
	 * @param persistent
	 *            T - object to be stored into the persistent layer.
	 * @return int - id of the newly created record in the persistent layer.
	 * @throws DaoDuplicateException
	 *             If given object to store already exists in the persistent layer.
	 * @throws DaoException
	 *             If general database failure occurs.
	 * @throws RemoteException
	 *             If remote method cannot be invoked - see javadoc for {@link RemoteException} for
	 *             details.
	 */
	public int store(final T persistent) throws DaoDuplicateException, DaoException, RemoteException;

	/**
	 * Updates given object in the persistent layer.
	 * 
	 * @param persistent
	 *            T - object to be updated.
	 * @throws DaoStaleObjectException
	 *             If given object is outdated or no longer exists in the persistent layer.
	 * @throws DaoDuplicateException
	 *             If given object to update already exists in the persistent layer.
	 * @throws DaoException
	 *             If general database failure occurs.
	 * @throws RemoteException
	 *             If remote method cannot be invoked - see javadoc for {@link RemoteException} for
	 *             details.
	 */
	public void update(final T persistent) throws DaoStaleObjectException, DaoDuplicateException, DaoException,
			RemoteException;
}
