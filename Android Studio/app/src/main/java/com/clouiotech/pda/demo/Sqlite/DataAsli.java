package com.clouiotech.pda.demo.Sqlite;

public class DataAsli {
	private String KODE;
	private String JUMLAHDATA;
	private String JUMLAHCEK;
	private String WAREHOUSE;
	private String PERIOD;
	private String GROUP;
	private String TGL;
	private String DESKRIPSI;
	private String CATATAN;
	private String SELISIH;

	public DataAsli(){

	}

	public DataAsli(String _KODE,
					String _JUMLAHDATA,
					String _JUMLAHCEK,
					String _WAREHOUSE,
					String _PERIOD,
					String _GROUP,
					String _TGL,
					String _DESKRIPSI,
					String _CATATAN,
					String _SELISIH){
		this.KODE = _KODE;
		this.JUMLAHDATA = _JUMLAHDATA;
		this.JUMLAHCEK = _JUMLAHCEK;
		this.WAREHOUSE = _WAREHOUSE;
		this.PERIOD = _PERIOD;
		this.GROUP = _GROUP;
		this.TGL = _TGL;
		this.DESKRIPSI = _DESKRIPSI;
		this.SELISIH = _SELISIH;
		this.CATATAN = _CATATAN;
	}
	
	public void setKODE(String _KODE){
		this.KODE = _KODE;
	}
	public void setJUMLAHDATA(String _JUMLAHDATA){
		this.JUMLAHDATA = _JUMLAHDATA;
	}
	public void setJUMLAHCEK(String _JUMLAHCEK){
		this.JUMLAHCEK = _JUMLAHCEK;
	}
	public void setWAREHOUSE(String _WAREHOUSE){
		this.WAREHOUSE = _WAREHOUSE;
	}
	public void setPERIOD(String _PERIOD){
		this.PERIOD = _PERIOD;
	}
	public void setGROUP(String _GROUP){
		this.GROUP = _GROUP;
	}
	public void setTGL(String _TGL){
		this.TGL = _TGL;
	}
	public void setDESKRIPSI(String _DESKRIPSI){
		this.DESKRIPSI = _DESKRIPSI;
	}
	public void setSELISIH(String _SELISIH){
		this.SELISIH = _SELISIH;
	}
	public void setCATATAN(String _CATATAN){
		this.CATATAN = _CATATAN;
	}

	public String getKODE(){
		return this.KODE;
	}
	public String getJUMLAHDATA(){
		return this.JUMLAHDATA;
	}
	public String getJUMLAHCEK(){
		return this.JUMLAHCEK;
	}
	public String getWAREHOUSE(){
		return this.WAREHOUSE;
	}
	public String getPERIOD(){
		return this.PERIOD;
	}
	public String getGROUP(){
		return this.GROUP;
	}
	public String getTGL(){
		return this.TGL;
	}
	public String getDESKRIPSI(){
		return this.DESKRIPSI;
	}
	public String getSELISIH(){
		return this.SELISIH;
	}
	public String getCATATAN(){
		return this.CATATAN;
	}
}
