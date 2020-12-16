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
@Table(name="tb_stand2")
@NamedQuery(name="TbStand2.findAll", query="SELECT t FROM TbStand2 t")
@NamedQuery(name="TbStand2.getStand", query="SELECT t.stand FROM TbStand2 t")
public class TbStand2  {

	@Id
	private String stand;

	@Column(name="temp_d")
	private float tempD;

	@Column(name="temp_w")
	private float tempW;

	@Temporal(TemporalType.TIMESTAMP)
	private Date udate;

	private float xrang;

	public TbStand2() {
	}

	public String getStand() {
		return this.stand;
	}

	public void setStandNo(String stand) {
		this.stand = stand;
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