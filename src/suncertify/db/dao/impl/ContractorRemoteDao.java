package suncertify.db.dao.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import suncertify.core.InitializationException;
import suncertify.db.dao.ComparisonOperator;
import suncertify.db.dao.DaoDuplicateException;
import suncertify.db.dao.DaoException;
import suncertify.db.dao.DaoFinderException;
import suncertify.db.dao.DaoInitializationException;
import suncertify.db.dao.DaoStaleObjectException;
import suncertify.db.dao.IContractorDao;
import suncertify.db.dao.IDao;
import suncertify.db.domain.Contractor;

/**
 * <code>ContractorRemoteDao</code> is a remote (RMI) implementation of {@link IContractorDao}
 * interface. Because it is a remote object it extends {@link UnicastRemoteObject}.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see java.rmi.server.UnicastRemoteObject
 * @see suncertify.db.dao.IContractorDao
 */
public class ContractorRemoteDao extends UnicastRemoteObject implements IContractorDao {

    private static final long serialVersionUID = 1L;

	/**
	 * Local DAO delegate.
	 */
	private IContractorDao dao;

	/**
	 * Constructs <code>ContractorRemoteDao</code> instance basing on given database filename.
	 * 
	 * @throws InitializationException -
	 *             see {@link ContractorLocalDao#ContractorLocalDao(String)}.
	 * 
	 * @throws RemoteException -
	 *             see {@link UnicastRemoteObject#UnicastRemoteObject()}.
	 */
	public ContractorRemoteDao(String dbFilename) throws DaoInitializationException, RemoteException {
		super();
		dao = new ContractorLocalDao(dbFilename);
	}

	/**
	 * @see IDao#findById(int)
	 */
	public Contractor findById(int id) throws DaoFinderException, RemoteException {
		return dao.findById(id);
	}

	/**
	 * @see IDao#load()
	 */
	public List<Contractor> load() throws RemoteException {
		return dao.load();
	}

	/**
	 * @see IDao#load(String[], ComparisonOperator[])
	 */
	public List<Contractor> load(final String[] filter, final ComparisonOperator[] operators)
			throws DaoFinderException, DaoException, RemoteException {

		return dao.load(filter, operators);
	}

	/**
	 * @see IDao#load(String[])
	 */
	public List<Contractor> load(final String[] filter) throws DaoFinderException, DaoException, RemoteException {
		return dao.load(filter);
	}

	/*
	 * @see IDao#refresh(T)
	 */
	public Contractor refresh(Contractor persistent) throws DaoStaleObjectException, RemoteException {
		return dao.refresh(persistent);
	}

	/*
	 * @see IDao#remove(T)
	 */
	public void remove(Contractor persistent) throws DaoStaleObjectException, DaoException, RemoteException {
		dao.remove(persistent);
	}

	/*
	 * @see IDao#store(T)
	 */
	public int store(Contractor persistent) throws DaoDuplicateException, DaoException, RemoteException {
		return dao.store(persistent);
	}

	/*
	 * @see IDao#update(T)
	 */
	public void update(Contractor persistent) throws DaoStaleObjectException, DaoException, RemoteException {
		dao.update(persistent);
	}
}
