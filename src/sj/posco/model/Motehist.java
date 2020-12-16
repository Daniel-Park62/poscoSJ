package sj.posco.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the motehist database table.
 * 
 */
@ReadOnly
@Entity
@NamedQuery(name="Motehist.findAll", query="SELECT m FROM Motehist m")
public class Motehist  {

	@Id
	private String pkey;

	private short act;

	private int bno;

	private int seq;

	private int batt;

	private String stand;

	private float temp;

	@Temporal(TemporalType.TIMESTAMP)
	private Date tm;

	@Column(updatable=false, insertable=false)
	private int standNo;

	@ManyToOne
	@JoinColumn(name="stand", updatable=false, insertable=false)
	private TbStand2 tbstand2 ;
	public TbStand2 getTbstand2() {
		return tbstand2 ;
	}
	
	public Motehist() {
	}

	
	public String getPkey() {
		return this.pkey;
	}

	public void setPkey(String pkey) {
		this.pkey = pkey;
	}

	public short getAct() {
		return this.act;
	}

	public void setAct(short act) {
		this.act = act;
	}

	public int getBatt() {
		return batt;
	}

	public int getBno() {
		return this.bno;
	}

	public void setBno(int bno) {
		this.bno = bno;
	}

	public int getSeq() {
		return this.seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getStand() {
		return this.stand;
	}

	public void setStand(String stand) {
		this.stand = stand;
	}

	public int getStandNo() {
		return this.standNo;
	}

	public float getTemp() {
		return this.temp;
	}

	public void setTemp(float temp) {
		this.temp = temp;
	}

	public Date getTm() {
		return this.tm;
	}

	public void setTm(Date tm) {
		this.tm = tm;
	}
	
	public int getStatus() {
		int sts = 0 ;
		if ( temp > tbstand2.getTempD() ) 
			sts = 2 ;
		else if ( temp > tbstand2.getTempW() )
			sts = 1 ;
		return sts ;
	}

}