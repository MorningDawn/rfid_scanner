package com.clouiotech.pda.demo.Sqlite;

public class DataSetting {
	private String SERVER;
	private String DATABASE;
	private String USER;
	private String PASSWORD;
	private String FILTER;
	private String QUERY;
	private String TABLE;


	public DataSetting(){

	}

	public DataSetting(String _SERVER, String _DATABASE, String _USER, String _PASSWORD, String _FILTER, String _QUERY, String _TABLE){
		this.SERVER = _SERVER;
		this.DATABASE = _DATABASE;
		this.USER = _USER;
		this.PASSWORD = _PASSWORD;
		this.FILTER = _FILTER;
		this.QUERY= _QUERY;
		this.TABLE= _TABLE;
	}

	public void setSERVER(String _SERVER){this.SERVER = _SERVER;}
	public void setDATABASE(String _DATABASE){this.DATABASE = _DATABASE;}
	public void setUSER(String _USER){this.USER = _USER;}
	public void setPASSWORD(String _PASSWORD){this.PASSWORD = _PASSWORD;}
	public void setFILTER(String _FILTER){this.FILTER = _FILTER;}
	public void setQUERY(String _QUERY){this.QUERY = _QUERY;}
	public void setTABLE(String _TABLE){this.TABLE = _TABLE;}

	public String getSERVER(){return this.SERVER;}
	public String getDATABASE(){return this.DATABASE;}
	public String getUSER(){return this.USER;}
	public String getPASSWORD(){return this.PASSWORD;}
	public String getFILTER(){return this.FILTER;}
	public String getQUERY(){return this.QUERY;}
	public String getTABLE(){return this.TABLE;}
}
