package sj.posco.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the tb_stand database table.
 * 
 */
@Entity
@Table(name="tb_stand")
@NamedQuery(name="TbStand.findAll", query="SELECT t FROM TbStand t")
@NamedQuery(name="TbStand.getStand", query="SELECT concat(t.standNo,'T') FROM TbStand t")
public class TbStand  {

	@Id
	private int standNo;

	@Column(name="temp_d")
	private float tempD;

	@Column(name="temp_w")
	private float tempW;

	@Temporal(TemporalType.TIMESTAMP)
	private Date udate;

	private float xrang;

	public TbStand() {
	}

	public int getStandNo() {
		return this.standNo;
	}

	public void setStandNo(int standNo) {
		this.standNo = standNo;
	}

	public float getTempD() {
		return this.tempD;
	}

	public void setTempD(float tempD) {
		this.tempD = tempD;
	}

	public float getTempW() {
		return this.tempW;
	}

	public void setTempW(float tempW) {
		this.tempW = tempW;
	}

	public Date getUdate() {
		return this.udate;
	}

	public void setUdate(Date udate) {
		this.udate = udate;
	}

	public float getXrang() {
		return this.xrang;
	}

	public void setXrang(float xrang) {
		this.xrang = xrang;
	}

}