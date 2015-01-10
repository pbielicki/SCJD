package suncertify.db.domain;

import java.io.Serializable;

/**
 * <code>Persistent</code> is the abstract class that is to be extended by all
 * persistent objects in the domain model - it also implements common methods
 * for all domain objects.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see suncertify.db.domain.IDomain
 */
public abstract class Persistent implements IDomain, Serializable {

    private static final long serialVersionUID = 1L;

    /**
	 * Flag of this record.
	 */
	private RecordFlagEnum flag;

	/**
	 * Id of this record.
	 */
	private int id = DBMetaInfo.UNSAVED_RECORD_NO;

	/**
	 * Constructor.
	 */
	Persistent() {
		flag = RecordFlagEnum.VALID;
	}

	/**
	 * @see IDomain#getFlag()
	 */
	public RecordFlagEnum getFlag() {
		return flag;
	}

	/**
	 * @see IDomain#getId()
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns <code>true</code> if this object is tored in the persistent
	 * layer and <code>false</code> otherwise.
	 * 
	 * @return boolean - <code>true</code> if this object is tored in the
	 *         persistent layer and <code>false</code> otherwise.
	 */
	public boolean isStored() {
		return id > DBMetaInfo.UNSAVED_RECORD_NO;
	}

	/**
	 * @see IDomain#setFlag(RecordFlagEnum)
	 */
	public void setFlag(final RecordFlagEnum flag) {
		this.flag = flag;
	}

	/**
	 * @see IDomain#setId(int)
	 */
	public void setId(final int id) {
		this.id = id;
	}
}
