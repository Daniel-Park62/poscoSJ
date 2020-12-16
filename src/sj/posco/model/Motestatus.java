package sj.posco.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;


/**
 * The persistent class for the motestatus database table.
 * 
 */
@Entity
@NamedQuery(name = "Motestatus.sensorList", query = "select a from Motestatus a where a.gubun = 'S' and a.spare = 'N' ")
@NamedQuery(name="Motestatus.findAll", query="SELECT m FROM Motestatus m")
public class Motestatus  {

	@Id
	private int seq;

	@Column(updatable=false, insertable=false)
	private short act;

	@Column(updatable=false, insertable=false)
	private short status;

	@Column(updatable=false, insertable=false)
	private int batt;

	@Column(name="batt_dt")
	private String battDt;

	private int bno;

	private String descript;

	private String gubun = "S"; // 센서/리피터구분 S, R;

	@Column(updatable=false, insertable=false)
	private String mac = "";

	private String spare = "N"; // 예비품 Y,N;

	private String stand;
	 
	@Column(updatable=false, insertable=false)
	private int standNo;

	@Column(updatable=false, insertable=false)
	private String tb;

//	@ManyToOne
//	@JoinColumn(name = "standNo") 
//	private TbStand tbstand ;
	
	@ManyToOne
	@JoinColumn(name="stand" ,updatable=false, insertable=false)
	private TbStand2 tbstand2 ;
	public TbStand2 gettbStand2() {
		return tbstand2 ;
	}

	public Motestatus() {
	}

	public int getSeq() {
		return this.seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public short getAct() {
		return this.act;
	}

	public void setAct(short act) {
		this.act = act;
	}

	public short getStatus() {
		return status;
	}

	public int getBatt() {
		return this.batt;
	}

	public void setBatt(int batt) {
		this.batt = batt;
	}

	public String getBattDt() {
		return this.battDt == null ? "" : this.battDt ;
	}

	public void setBattDt(String battDt) {
		this.battDt = battDt;
	}

	public int getBno() {
		return this.bno;
	}

	public void setBno(int bno) {
		this.bno = bno;
	}

	public String getDescript() {
		return this.descript == null ? "" : this.descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

	public String getGubun() {
		return this.gubun;
	}

	public void setGubun(String gubun) {
		this.gubun = gubun;
	}

	public String getMac() {
		return this.mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getSpare() {
		return this.spare;
	}

	public void setSpare(String spare) {
		this.spare = spare;
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

	public String getTb() {
		return this.tb;
	}

}