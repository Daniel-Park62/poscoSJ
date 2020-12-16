package sj.posco.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Entity implementation class for Entity: LasTime
 *
 */
@ReadOnly
@Entity
@NamedQuery(name="LasTime.findAll", query="SELECT l FROM LasTime l")
public class LasTime  {

	private static LasTime instance ;
	public static LasTime getInstance() {
		if ( instance == null )
			instance = new LasTime();
		return instance;
	}
	
	@Id
	private int pkey;
	private Timestamp lastm;

	private LasTime() {	}   
	public int getPkey() {
		return this.pkey;
	}

	public void setPkey(int pkey) {
		this.pkey = pkey;
	}   
	public Timestamp getLastm() {
		return this.lastm;
	}

	public void setLastm(Timestamp lastm) {
		this.lastm = lastm;
	}
   
}
