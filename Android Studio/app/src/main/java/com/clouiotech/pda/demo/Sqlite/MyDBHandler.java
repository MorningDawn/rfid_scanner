package com.clouiotech.pda.demo.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.format.DateFormat;

import com.clouiotech.pda.demo.Activity.MainActivity.Callbacks;
import com.clouiotech.pda.demo.Fragment.StockScanFragment.DatabaseResponseCallback;
import com.clouiotech.pda.demo.BaseObject.EpcObject;
import com.clouiotech.pda.demo.BaseObject.Item;
import com.clouiotech.pda.demo.Fragment.StockScanFragment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "rfid.db";
    private static final String TABLE_REPORT = "report";
    private static final String TABLE_ORDER = "data_order";
    private static final String TABLE_TEMP = "temp_code";
    private static final String TABLE_DOWNLOAD = "setting";
    private static final String TABLE_ASLI = "data_asli";

    public static final String REPORT_RECID = "rec_id";
    public static final String REPORT_KODE = "kode_barang";
    public static final String REPORT_JUMLAHDATA = "jumlah_data";
    public static final String REPORT_JUMLAHCEK = "jumlah_cek";
    public static final String REPORT_WAREHOUSE = "warehouse";
    public static final String REPORT_PERIOD = "period";
    public static final String REPORT_GROUP = "grup";
    public static final String REPORT_TGL = "tgl_cek";
    public static final String REPORT_DESKRIPSI = "deskripsi";
    public static final String REPORT_CATATAN = "catatan";
    public static final String REPORT_SELISIH = "selisih";
    public static final String REPORT_ID = "id_report";
    public static final String REPORT_TANGGAL_CATAT = "tgl_catat";

    public static final String ORDER_RECID = "rec_id";
    public static final String ORDER_KODE = "kode_barang";
    public static final String ORDER_JUMLAHDATA = "jumlah_data";
    public static final String ORDER_JUMLAHCEK = "jumlah_cek";
    public static final String ORDER_WAREHOUSE = "warehouse";
    public static final String ORDER_PERIOD = "period";
    public static final String ORDER_GROUP = "grup";
    public static final String ORDER_TGL = "tgl_cek";
    public static final String ORDER_DESKRIPSI = "deskripsi";
    public static final String ORDER_CATATAN = "catatan";
    public static final String ORDER_SELISIH = "selisih";

    public static final String ASLI_RECID = "rec_id";
    public static final String ASLI_KODE = "kode_barang";
    public static final String ASLI_JUMLAHDATA = "jumlah_data";
    public static final String ASLI_JUMLAHCEK = "jumlah_cek";
    public static final String ASLI_WAREHOUSE = "warehouse";
    public static final String ASLI_PERIOD = "period";
    public static final String ASLI_GROUP = "grup";
    public static final String ASLI_TGL = "tgl_cek";
    public static final String ASLI_DESKRIPSI = "deskripsi";
    public static final String ASLI_CATATAN = "catatan";
    public static final String ASLI_SELISIH = "selisih";

    public static final String TEMP_RECID = "rec_id";
    public static final String TEMP_KODE = "kode_barang";

    public static final String DOWNLOAD_RECID = "rec_id";
    public static final String DOWNLOAD_SERVER = "server";
    public static final String DOWNLOAD_DATABASE = "database";
    public static final String DOWNLOAD_USER = "user";
    public static final String DOWNLOAD_PASS = "password";
    public static final String DOWNLOAD_FILTER = "filter";
    public static final String DOWNLOAD_QUERY = "query";
    public static final String DOWNLOAD_TABEL = "nama_tabel";

    private Context context;

    private Callbacks mCallbacks = null;

	public MyDBHandler(Context context, String name,
	            SQLiteDatabase.CursorFactory factory, int version) {
			super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
        this.context = context;
		// TODO Auto-generated method stub
		String CREATE_REPORT_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_REPORT + "("
                        + REPORT_RECID + " INTEGER PRIMARY KEY,"
                        + REPORT_KODE + " TEXT,"
                        + REPORT_JUMLAHDATA + " TEXT,"
                        + REPORT_JUMLAHCEK + " TEXT,"
                        + REPORT_WAREHOUSE + " TEXT,"
                        + REPORT_PERIOD + " TEXT,"
                        + REPORT_GROUP + " TEXT,"
                        + REPORT_TGL + " TEXT,"
                        + REPORT_DESKRIPSI + " TEXT,"
                        + REPORT_CATATAN + " TEXT,"
                        + REPORT_SELISIH + " TEXT,"
                        + REPORT_ID + " TEXT,"
                        + REPORT_TANGGAL_CATAT + " TEXT " + ")";
		String CREATE_ORDER_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_ORDER + "("
                        + ORDER_RECID + " INTEGER PRIMARY KEY,"
                        + ORDER_KODE + " TEXT,"
                        + ORDER_JUMLAHDATA + " TEXT,"
                        + ORDER_JUMLAHCEK + " TEXT,"
                        + ORDER_WAREHOUSE + " TEXT,"
                        + ORDER_PERIOD + " TEXT,"
                        + ORDER_GROUP + " TEXT,"
                        + ORDER_TGL + " TEXT,"
                        + ORDER_DESKRIPSI + " TEXT,"
                        + ORDER_CATATAN + " TEXT,"
                        + ORDER_SELISIH + " TEXT " + ")";
        String CREATE_ASLI_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_ASLI+ "("
                        + ASLI_RECID + " INTEGER PRIMARY KEY,"
                        + ASLI_KODE + " TEXT,"
                        + ASLI_JUMLAHDATA + " TEXT,"
                        + ASLI_JUMLAHCEK + " TEXT,"
                        + ASLI_WAREHOUSE + " TEXT,"
                        + ASLI_PERIOD + " TEXT,"
                        + ASLI_GROUP + " TEXT,"
                        + ASLI_TGL + " TEXT,"
                        + ASLI_DESKRIPSI + " TEXT,"
                        + ASLI_CATATAN + " TEXT,"
                        + ASLI_SELISIH + " TEXT " + ")";
		String CREATE_TEMP_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_TEMP + "("
                        + TEMP_RECID + " INTEGER PRIMARY KEY,"
                        + TEMP_KODE + " TEXT " + ")";
        String CREATE_DOWNLOAD_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_DOWNLOAD + "("
                        + DOWNLOAD_RECID + " INTEGER PRIMARY KEY,"
                        + DOWNLOAD_SERVER + " TEXT,"
                        + DOWNLOAD_DATABASE + " TEXT,"
                        + DOWNLOAD_USER + " TEXT,"
                        + DOWNLOAD_FILTER + " TEXT,"
                        + DOWNLOAD_PASS + " TEXT,"
                        + DOWNLOAD_QUERY + " TEXT,"
                        + DOWNLOAD_TABEL + " TEXT " + ")";
		db.execSQL(CREATE_REPORT_TABLE);
        db.execSQL(CREATE_ORDER_TABLE);
        db.execSQL(CREATE_TEMP_TABLE);
        db.execSQL(CREATE_DOWNLOAD_TABLE);
        db.execSQL(CREATE_ASLI_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public void deleteAll(String tbl){
        SQLiteDatabase db = this.getWritableDatabase(); //get database
        db.execSQL("DELETE FROM "+ tbl); //delete all rows in a table
        db.close();
    }

    public void insertAsliToOrder(){
        SQLiteDatabase db = this.getWritableDatabase(); //get database
        db.execSQL("INSERT INTO "+TABLE_ORDER+" SELECT * FROM "+TABLE_ASLI);
//        db.execSQL("INSERT INTO "+TABLE_ORDER+"("+ORDER_KODE+","+ORDER_JUMLAHDATA+","+ORDER_JUMLAHCEK+","+
//                ORDER_WAREHOUSE+","+ORDER_PERIOD+","+ORDER_GROUP+","+ORDER_TGL+","+ORDER_DESKRIPSI+","+
//                ORDER_CATATAN+","+ORDER_SELISIH+") SELECT "+ORDER_KODE+","+ORDER_JUMLAHDATA+","+ORDER_JUMLAHCEK+","+
//                ORDER_WAREHOUSE+","+ORDER_PERIOD+","+ORDER_GROUP+","+ORDER_TGL+","+ORDER_DESKRIPSI+","+
//                ORDER_CATATAN+","+ORDER_SELISIH+" FROM "+TABLE_ASLI);
        db.close();
//        insert into table2 (name, address)
//        select name, address
//        from table1
    }

    public void insertOrderToReport(){
        SQLiteDatabase db = this.getWritableDatabase(); //get database
//        db.execSQL("INSERT INTO "+TABLE_REPORT+" SELECT * FROM "+TABLE_ASLI);
        String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
        Cursor mCount;
        mCount = db.rawQuery("SELECT DISTINCT "+REPORT_ID+" FROM "+TABLE_REPORT+" ORDER BY CAST("+REPORT_ID+" AS INTEGER) DESC", null);
        mCount.moveToFirst();
        int kode = 0;
        if(mCount.getCount()>0){
            kode= mCount.getInt(0)+1;
        }else{
            kode = 0;
        }
        mCount.close();
        db.execSQL("INSERT INTO "+TABLE_REPORT+"("
                +REPORT_KODE+","
                +REPORT_JUMLAHDATA+","
                +REPORT_JUMLAHCEK+","
                +REPORT_WAREHOUSE+","
                +REPORT_PERIOD+","
                +REPORT_GROUP+","
                +REPORT_TGL+","
                +REPORT_DESKRIPSI+","
                +REPORT_CATATAN+","
                +REPORT_SELISIH+","
                +REPORT_ID+","
                +REPORT_TANGGAL_CATAT+") SELECT "
                +REPORT_KODE+","
                +REPORT_JUMLAHDATA+","
                +REPORT_JUMLAHCEK+","
                +REPORT_WAREHOUSE+","
                +REPORT_PERIOD+","
                +REPORT_GROUP+","
                +REPORT_TGL+","
                +REPORT_DESKRIPSI+","
                +REPORT_CATATAN+","
                +REPORT_SELISIH+","
                + String.valueOf(kode)+
                ",'"+date.toString()+"' FROM "+TABLE_ORDER);
        db.close();
    }

    public void deleteSetting(String recid){
        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DELETE FROM "+ TABLE_DOWNLOAD + " WHERE " + DOWNLOAD_RECID + " = " + recid);
        db.delete(TABLE_DOWNLOAD, DOWNLOAD_RECID + "=" + recid, null);
        db.close();
    }

	//Handler Report
    public void addDataReport(DataReport listDataReport) {
        ContentValues values = new ContentValues();
        values.put(REPORT_ID,listDataReport.getID());
        values.put(REPORT_KODE,listDataReport.getKODE());
        values.put(REPORT_JUMLAHDATA,listDataReport.getJUMLAHDATA());
        values.put(REPORT_JUMLAHCEK,listDataReport.getJUMLAHCEK());
        values.put(REPORT_WAREHOUSE,listDataReport.getWAREHOUSE());
        values.put(REPORT_PERIOD,listDataReport.getPERIOD());
        values.put(REPORT_GROUP,listDataReport.getGROUP());
        values.put(REPORT_TGL,listDataReport.getTGL());
        values.put(REPORT_DESKRIPSI,listDataReport.getDESKRIPSI());
        values.put(REPORT_CATATAN,listDataReport.getCATATAN());
        values.put(REPORT_SELISIH,listDataReport.getSELISIH());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_REPORT, null, values);
        db.close();
    }

    //Handler Data Asli
    public void addDataAsli(DataAsli listDataOrder) {
        ContentValues values = new ContentValues();
        values.put(ASLI_KODE,listDataOrder.getKODE());
        values.put(ASLI_JUMLAHDATA,listDataOrder.getJUMLAHDATA());
        values.put(ASLI_JUMLAHCEK,listDataOrder.getJUMLAHCEK());
        values.put(ASLI_WAREHOUSE,listDataOrder.getWAREHOUSE());
        values.put(ASLI_PERIOD,listDataOrder.getPERIOD());
        values.put(ASLI_GROUP,listDataOrder.getGROUP());
        values.put(ASLI_TGL,listDataOrder.getTGL());
        values.put(ASLI_DESKRIPSI,listDataOrder.getDESKRIPSI());
        values.put(ASLI_CATATAN,listDataOrder.getCATATAN());
        values.put(ASLI_SELISIH,listDataOrder.getSELISIH());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_ASLI, null, values);
        db.close();
    }

   //Handler Order
   public void addDataOrder(DataOrder listDataOrder) {
        ContentValues values = new ContentValues();
        values.put(ORDER_KODE,listDataOrder.getKODE());
        values.put(ORDER_JUMLAHDATA,listDataOrder.getJUMLAHDATA());
        values.put(ORDER_JUMLAHCEK,listDataOrder.getJUMLAHCEK());
        values.put(ORDER_WAREHOUSE,listDataOrder.getWAREHOUSE());
        values.put(ORDER_PERIOD,listDataOrder.getPERIOD());
        values.put(ORDER_GROUP,listDataOrder.getGROUP());
        values.put(ORDER_TGL,listDataOrder.getTGL());
        values.put(ORDER_DESKRIPSI,listDataOrder.getDESKRIPSI());
        values.put(ORDER_CATATAN,listDataOrder.getCATATAN());
        values.put(ORDER_SELISIH,listDataOrder.getSELISIH());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_ORDER, null, values);
        db.close();
    }

    public void addDataOrderAndAsli(List<Item> listItem, Callbacks callbacks) {
        // DELETE THE DATA FIRST BEFORE INSERTING
        deleteAll(TABLE_ASLI);
        deleteAll(TABLE_ORDER);

        this.mCallbacks = callbacks;
        Item item = null;

        ContentValues values = new ContentValues();

        SQLiteDatabase db = this.getWritableDatabase();
        DatabaseUtils.InsertHelper insertOrderHelper = new DatabaseUtils.InsertHelper(db, TABLE_ORDER);
        DatabaseUtils.InsertHelper insertAsliHelper = new DatabaseUtils.InsertHelper(db, TABLE_ASLI);
        db.beginTransaction();
        try {
            for(int i = 0; i < listItem.size(); i++) {
                item = listItem.get(i);
                values.put(ORDER_KODE,item.getItemCode());
                values.put(ORDER_JUMLAHDATA,String.valueOf(item.getQuantityAwal()));
                values.put(ORDER_JUMLAHCEK,"0");
                values.put(ORDER_WAREHOUSE,item.getWarehouseId());
                values.put(ORDER_PERIOD,item.getPeriodName());
                values.put(ORDER_GROUP,item.getGroupId());
                values.put(ORDER_TGL,"");
                values.put(ORDER_DESKRIPSI,item.getItemDescription());
                values.put(ORDER_CATATAN,"");
                values.put(ORDER_SELISIH, String.valueOf(getItemDifference(item.getQuantityAwal())));
                //db.insert(TABLE_ORDER, null, values);
                //db.insert(TABLE_ASLI, null, values);
                insertAsliHelper.insert(values);
                insertOrderHelper.insert(values);
                values.clear();
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        mCallbacks.onSuccess();
    }

    private int getItemDifference(int itemCount) {
        if (itemCount <= 0) return 0;
        else if (itemCount > 0) return (0-itemCount);

        return 0;
    }

    // TODO DISINI
    public void getDataAsli(DatabaseResponseCallback callback) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ORDER, null, null, null, null, null, null);

        // Parse the cursor
        if (!cursor.moveToFirst()) return;

        List<EpcObject> listEpc= new ArrayList<EpcObject>();
        while(cursor.moveToNext()) {
            int orderCodeIndex = cursor.getColumnIndex(ORDER_KODE);
            int orderJumlahCekIndex = cursor.getColumnIndex(ORDER_JUMLAHCEK);
            int orderWarehouseIndex = cursor.getColumnIndex(ORDER_WAREHOUSE);
            int ordedJumlahDataIndex = cursor.getColumnIndex(ORDER_JUMLAHDATA);
            int orderPeriodIndex = cursor.getColumnIndex(ORDER_PERIOD);
            int orderGroupIndex = cursor.getColumnIndex(ORDER_GROUP);
            int orderTanggalIndex = cursor.getColumnIndex(ORDER_TGL);
            int orderDeskripsiIndex = cursor.getColumnIndex(ORDER_DESKRIPSI);
            int orderCatatanIndex = cursor.getColumnIndex(ORDER_CATATAN);
            int orderSelisihIndex = cursor.getColumnIndex(ORDER_SELISIH);

            String orderCode= cursor.getString(orderCodeIndex);
            String orderJumlahCek = cursor.getString(orderJumlahCekIndex);
            String orderWarehouse = cursor.getString(orderWarehouseIndex);
            String orderJumlahData = cursor.getString(ordedJumlahDataIndex);
            String orderPeriod = cursor.getString(orderPeriodIndex);
            String orderGroup = cursor.getString(orderGroupIndex);
            String orderTanggal = cursor.getString(orderTanggalIndex);
            String orderDeskripsi = cursor.getString(orderDeskripsiIndex);
            String orderCatatan = cursor.getString(orderCatatanIndex);
            String orderSelisih= cursor.getString(orderSelisihIndex);

            //Item item = new Item(orderCode, Integer.parseInt(orderJumlahData), orderWarehouse, orderPeriod,
//                    orderGroup, orderDeskripsi);
            EpcObject item = new EpcObject(orderCode, orderDeskripsi, Integer.parseInt(orderJumlahData),
                    0);
            listEpc.add(item);
            item = null;
        }

        callback.onData(listEpc);
    }

    public void addDataSetting(DataSetting listDataSetting) {
        ContentValues values = new ContentValues();
        values.put(DOWNLOAD_SERVER,listDataSetting.getSERVER());
        values.put(DOWNLOAD_DATABASE,listDataSetting.getDATABASE());
        values.put(DOWNLOAD_USER,listDataSetting.getUSER());
        values.put(DOWNLOAD_PASS,listDataSetting.getPASSWORD());
        values.put(DOWNLOAD_FILTER,listDataSetting.getFILTER());
        values.put(DOWNLOAD_QUERY,listDataSetting.getQUERY());
        values.put(DOWNLOAD_TABEL,listDataSetting.getTABLE());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_DOWNLOAD, null, values);
        db.close();
    }

    public void updateDataSetting(DataSetting listDataSetting, String recid) {
        ContentValues values = new ContentValues();
        values.put(DOWNLOAD_SERVER,listDataSetting.getSERVER());
        values.put(DOWNLOAD_DATABASE,listDataSetting.getDATABASE());
        values.put(DOWNLOAD_USER,listDataSetting.getUSER());
        values.put(DOWNLOAD_PASS,listDataSetting.getPASSWORD());
        values.put(DOWNLOAD_FILTER,listDataSetting.getFILTER());
        values.put(DOWNLOAD_QUERY,listDataSetting.getQUERY());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_DOWNLOAD, values, DOWNLOAD_RECID + " = " + recid, null);
        db.close();
    }

   //Handler Temp
   public void addDataTemp(DataTemp listDataTemp) {
        ContentValues values = new ContentValues();
        values.put(TEMP_KODE,listDataTemp.getKODE());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_TEMP, null, values);
        db.close();
    }
   
   public ArrayList<String> getAllData(String tblName, String columnName)
   {
       ArrayList<String> array_list = new ArrayList<String>();
       SQLiteDatabase db = this.getReadableDatabase();
       Cursor res =  db.rawQuery("select * from " + tblName, null);
       res.moveToFirst();
       while(res.isAfterLast() == false){
           String data = res.getString(res.getColumnIndex(columnName));
           array_list.add(data);
           res.moveToNext();
       }
       return array_list;
   }

    public ArrayList<String> getAllHistory(int kode)
    {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select DISTINCT "+REPORT_ID+","+REPORT_TANGGAL_CATAT+" from "+TABLE_REPORT+" ORDER BY CAST("+REPORT_ID+" AS INTEGER) DESC", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            String data = res.getString(kode);
            array_list.add(data);
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllSetting(String columnName)
    {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_DOWNLOAD+" order by "+DOWNLOAD_FILTER+" ASC", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            String data = res.getString(res.getColumnIndex(columnName));
            array_list.add(data);
            res.moveToNext();
        }
        return array_list;
    }

    public void updateDataOrder(DataOrder listDataOrder, String recid, int jnsUpdate) {
        ContentValues values = new ContentValues();
        values.put(ORDER_KODE,listDataOrder.getKODE());
        values.put(ORDER_JUMLAHDATA,listDataOrder.getJUMLAHDATA());
        values.put(ORDER_JUMLAHCEK,listDataOrder.getJUMLAHCEK());
        values.put(ORDER_WAREHOUSE,listDataOrder.getWAREHOUSE());
        values.put(ORDER_PERIOD,listDataOrder.getPERIOD());
        values.put(ORDER_GROUP,listDataOrder.getGROUP());
        values.put(ORDER_TGL,listDataOrder.getTGL());
        values.put(ORDER_DESKRIPSI,listDataOrder.getDESKRIPSI());
        values.put(ORDER_CATATAN,listDataOrder.getCATATAN());
        values.put(ORDER_SELISIH, listDataOrder.getSELISIH());
        SQLiteDatabase db = this.getWritableDatabase();
        if(jnsUpdate==0){
            //Update Catatan/Deskripsi
            db.update(TABLE_ORDER,values, ORDER_RECID + " = " + recid,null);
            db.close();
        }else if(jnsUpdate==1){
            //Update Jumlah
            db.update(TABLE_ORDER,values, ORDER_KODE + " = '" + recid +"'",null);
            db.close();
        }else{
            db.close();
        }
        db.close();
    }

    public DataOrder findDataOrder(String _id) {
        String query = "Select * FROM " + TABLE_ORDER  + " WHERE " + ORDER_RECID + " =  \"" + _id + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        DataOrder listOrder = new DataOrder();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            listOrder.setKODE(cursor.getString(1));
            listOrder.setJUMLAHDATA(cursor.getString(2));
            listOrder.setJUMLAHCEK(cursor.getString(3));
            listOrder.setWAREHOUSE(cursor.getString(4));
            listOrder.setPERIOD(cursor.getString(5));
            listOrder.setGROUP(cursor.getString(6));
            listOrder.setTGL(cursor.getString(7));
            listOrder.setDESKRIPSI(cursor.getString(8));
            listOrder.setCATATAN(cursor.getString(9));
            listOrder.setSELISIH(cursor.getString(10));
            cursor.close();
        } else {
            listOrder = null;
        }
        db.close();
        return listOrder;
    }

    public DataOrder findKode(String _id) {
//        String query = "Select * FROM " + TABLE_ORDER  + " WHERE " + ORDER_KODE + " =  '" + _id + "'";
        String query = "Select * FROM " + TABLE_ORDER  + " WHERE " + ORDER_KODE + " =  \"" + _id + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        DataOrder listOrder = new DataOrder();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            listOrder.setKODE(cursor.getString(1));
            listOrder.setJUMLAHDATA(cursor.getString(2));
            listOrder.setJUMLAHCEK(cursor.getString(3));
            listOrder.setWAREHOUSE(cursor.getString(4));
            listOrder.setPERIOD(cursor.getString(5));
            listOrder.setGROUP(cursor.getString(6));
            listOrder.setTGL(cursor.getString(7));
            listOrder.setDESKRIPSI(cursor.getString(8));
            listOrder.setCATATAN(cursor.getString(9));
            listOrder.setSELISIH(cursor.getString(10));
            cursor.close();
        } else {
            listOrder = null;
        }
        db.close();
        return listOrder;
    }

    public DataTemp findKodeTemp(String _id) {
        String query = "Select * FROM " + TABLE_TEMP  + " WHERE " + ORDER_KODE + " =  \"" + _id + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        DataTemp listOrder = new DataTemp();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            listOrder.setKODE(cursor.getString(1));
            cursor.close();
        } else {
            listOrder = null;
        }
        db.close();
        return listOrder;
    }

    public Cursor selectNameSetting(String namaSetting) throws SQLException
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String command = "SELECT * FROM "+TABLE_DOWNLOAD+" WHERE "+DOWNLOAD_FILTER +" = '" + namaSetting+"'";
        return db.rawQuery(command, null);
    }

    public ArrayList<String> getAllOrder(String columnName, String sort, String cari)
    {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        if(!cari.equals("")){
            res =  db.rawQuery(
                    "select * from "+TABLE_ORDER+ " where "+
                            ORDER_KODE + " like '%"+cari+"%' or "+
                            ORDER_JUMLAHCEK + " like '%"+cari+"%' or "+
                            ORDER_JUMLAHDATA + " like '%"+cari+"%' or "+
                            ORDER_DESKRIPSI + " like '%"+cari+"%' or "+
                            ORDER_CATATAN + " like '%"+cari+"%' or "+
                            ORDER_SELISIH + " like '%"+cari+"%'"+
                            sort, null );
        }else{
            res =  db.rawQuery( "select * from "+TABLE_ORDER+ sort, null );
        }
        res.moveToFirst();
        while(res.isAfterLast() == false){
            String data = res.getString(res.getColumnIndex(columnName));
            array_list.add(data);
            res.moveToNext();
        }
        res.close();
        db.close();
        return array_list;
    }

    public ArrayList<String> getAllReportDetail(String columnName, String sort, String cari,String kode)
    {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        if(!cari.equals("")){
            res =  db.rawQuery(
                    "select * from "+TABLE_REPORT+ " where ("+
                            REPORT_KODE + " like '%"+cari+"%' or "+
                            REPORT_JUMLAHCEK + " like '%"+cari+"%' or "+
                            REPORT_JUMLAHDATA + " like '%"+cari+"%' or "+
                            REPORT_SELISIH + " like '%"+cari+"%') AND "+REPORT_ID+" = "+kode+" "+
                            sort, null );
//                        res =  db.rawQuery(
//                    "select * from report where id_report = 1", null );
        }else{
            res =  db.rawQuery( "select * from "+TABLE_REPORT+ " where "+ REPORT_ID + " = "+ kode +" " +sort, null );
        }
        res.moveToFirst();
        while(res.isAfterLast() == false){
            String data = res.getString(res.getColumnIndex(columnName));
            array_list.add(data);
            res.moveToNext();
        }
        res.close();
        db.close();
        return array_list;
    }

    public String informasiUpdate(){
        String result = "0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCount;
        mCount = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ASLI+ " WHERE "+ASLI_JUMLAHCEK+" >0", null);
//        mCount = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ASLI+ " WHERE "+ASLI_JUMLAHDATA+" >0", null);
        mCount.moveToFirst();
        int J1= mCount.getInt(0);
        mCount = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ASLI, null);
        mCount.moveToFirst();
        int J2= mCount.getInt(0);
        mCount = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ASLI+ " WHERE "+ASLI_JUMLAHDATA+" <=0", null);
        mCount.moveToFirst();
        int J3= mCount.getInt(0);
//        mCount = db.rawQuery("SELECT SUM("+ORDER_JUMLAHCEK+") FROM "+TABLE_ORDER+ " WHERE "+ORDER_JUMLAHDATA+" >0", null);
        mCount = db.rawQuery("SELECT SUM("+ORDER_JUMLAHCEK+") FROM "+TABLE_ORDER+ " WHERE "+ORDER_JUMLAHDATA+" != '-' "+
                "AND "+ORDER_JUMLAHDATA+" != '+'", null);
        mCount.moveToFirst();
        int I1= mCount.getInt(0);
//        mCount = db.rawQuery("SELECT SUM("+ORDER_JUMLAHCEK+") FROM "+TABLE_ORDER+ " WHERE "+ORDER_JUMLAHDATA+" >0 AND "
//                +ORDER_JUMLAHCEK+">"+ORDER_JUMLAHDATA, null);
        mCount = db.rawQuery("SELECT SUM("+ORDER_JUMLAHCEK+" - "+ORDER_JUMLAHDATA+") FROM "+TABLE_ORDER+ " WHERE "+ORDER_JUMLAHDATA+" != '-' AND "+
                ORDER_JUMLAHCEK+" > "+ORDER_JUMLAHDATA+" AND "+ORDER_JUMLAHDATA+" != '+' AND "+
                ORDER_JUMLAHDATA +" >= 0", null);
        mCount.moveToFirst();
        int I3= mCount.getInt(0);
        mCount = db.rawQuery("SELECT SUM("+ORDER_JUMLAHCEK+" - 0"+") FROM "+TABLE_ORDER+ " WHERE "+ORDER_JUMLAHDATA+" != '-' AND "+
                ORDER_JUMLAHCEK+" > "+ORDER_JUMLAHDATA+" AND "+ORDER_JUMLAHDATA+" != '+' AND "+
                ORDER_JUMLAHDATA +" < 0", null);
        mCount.moveToFirst();
        I3 = I3 + mCount.getInt(0);
        mCount = db.rawQuery("SELECT SUM("+ORDER_JUMLAHDATA+") FROM "+TABLE_ASLI +" WHERE "+ORDER_JUMLAHDATA+" >=0", null);
        mCount.moveToFirst();
        int I2= mCount.getInt(0);
//        mCount = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ASLI +" WHERE "+ORDER_JUMLAHDATA+" <0", null);
//        mCount.moveToFirst();
//        I2 = I2 + mCount.getInt(0);
        mCount = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ORDER+" WHERE "+ORDER_JUMLAHDATA+" = '-' ", null);
        mCount.moveToFirst();
        int X= mCount.getInt(0);
        mCount = db.rawQuery("SELECT SUM("+ORDER_JUMLAHCEK+") FROM "+TABLE_ORDER+" WHERE "+ORDER_JUMLAHDATA+" = '-' "+
                "AND "+ORDER_JUMLAHDATA+" != '+'", null);
        mCount.moveToFirst();
        int Y= mCount.getInt(0);
        mCount = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ORDER+" WHERE "+ORDER_JUMLAHDATA+" = '+'", null);
        mCount.moveToFirst();
        int Z= mCount.getInt(0);
        mCount.close();
        result = String.valueOf(J1)+" ( "+ String.valueOf(J2)+" | "+ String.valueOf(J3)+" ) / "+ String.valueOf(I1)+" | "+ String.valueOf(I3)+
                " ( "+ String.valueOf(I2)+" ) / "+ String.valueOf(X)+" ; "+ String.valueOf(Y)+" ; "+ String.valueOf(Z);
        return result;
    }

    public String informasiUpdateDetail(){
        String result = "0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCount;
        mCount = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ASLI+ " WHERE "+ASLI_JUMLAHCEK+" >0", null);
//        mCount = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ASLI+ " WHERE "+ASLI_JUMLAHDATA+" >0", null);
        mCount.moveToFirst();
        int J1= mCount.getInt(0);
        mCount = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ASLI, null);
        mCount = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ASLI, null);
        mCount.moveToFirst();
        int J2= mCount.getInt(0);
        mCount = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ASLI+ " WHERE "+ASLI_JUMLAHDATA+" <=0", null);
        mCount.moveToFirst();
        int J3= mCount.getInt(0);
//        mCount = db.rawQuery("SELECT SUM("+ORDER_JUMLAHCEK+") FROM "+TABLE_ORDER+ " WHERE "+ORDER_JUMLAHDATA+" >0", null);
        mCount = db.rawQuery("SELECT SUM("+ORDER_JUMLAHCEK+") FROM "+TABLE_ORDER+ " WHERE "+ORDER_JUMLAHDATA+" != '-' "+
                "AND "+ORDER_JUMLAHDATA+" != '+'", null);
        mCount.moveToFirst();
        int I1= mCount.getInt(0);
//        mCount = db.rawQuery("SELECT SUM("+ORDER_JUMLAHCEK+") FROM "+TABLE_ORDER+ " WHERE "+ORDER_JUMLAHDATA+" >0 AND "
//                +ORDER_JUMLAHCEK+">"+ORDER_JUMLAHDATA, null);
        mCount = db.rawQuery("SELECT SUM("+ORDER_JUMLAHCEK+" - "+ORDER_JUMLAHDATA+") FROM "+TABLE_ORDER+ " WHERE "+ORDER_JUMLAHDATA+" != '-' AND "+
                ORDER_JUMLAHCEK+" > "+ORDER_JUMLAHDATA+" AND "+ORDER_JUMLAHDATA+" != '+' AND "+
                ORDER_JUMLAHDATA +" >= 0", null);
        mCount.moveToFirst();
        int I3= mCount.getInt(0);
        mCount = db.rawQuery("SELECT SUM("+ORDER_JUMLAHCEK+" - 0"+") FROM "+TABLE_ORDER+ " WHERE "+ORDER_JUMLAHDATA+" != '-' AND "+
                ORDER_JUMLAHCEK+" > "+ORDER_JUMLAHDATA+" AND "+ORDER_JUMLAHDATA+" != '+' AND "+
                ORDER_JUMLAHDATA +" < 0", null);
        mCount.moveToFirst();
        I3 = I3 + mCount.getInt(0);
        mCount = db.rawQuery("SELECT SUM("+ORDER_JUMLAHDATA+") FROM "+TABLE_ASLI +" WHERE "+ORDER_JUMLAHDATA+" >=0", null);
        mCount.moveToFirst();
        int I2= mCount.getInt(0);
//        mCount = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ASLI +" WHERE "+ORDER_JUMLAHDATA+" <0", null);
//        mCount.moveToFirst();
//        I2 = I2 + mCount.getInt(0);
        mCount = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ORDER+" WHERE "+ORDER_JUMLAHDATA+" = '-' ", null);
        mCount.moveToFirst();
        int X= mCount.getInt(0);
        mCount = db.rawQuery("SELECT SUM("+ORDER_JUMLAHCEK+") FROM "+TABLE_ORDER+" WHERE "+ORDER_JUMLAHDATA+" = '-' "+
                "AND "+ORDER_JUMLAHDATA+" != '+'", null);
        mCount.moveToFirst();
        int Y= mCount.getInt(0);
        mCount = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ORDER+" WHERE "+ORDER_JUMLAHDATA+" = '+'", null);
        mCount.moveToFirst();
        int Z= mCount.getInt(0);
        mCount.close();
        result = String.valueOf(J1)+" ( "+ String.valueOf(J2)+" | "+ String.valueOf(J3)+" ) / "+ String.valueOf(I1)+" | "+ String.valueOf(I3)+
                " ( "+ String.valueOf(I2)+" ) / "+ String.valueOf(X)+" ; "+ String.valueOf(Y)+" ; "+ String.valueOf(Z);
        return result;
    }

    public boolean exportToCsv(String fileName,String kodeReport){
        boolean result = false;
        SQLiteDatabase sqldb = this.getReadableDatabase();
        Cursor c = null;
        try {
            if(kodeReport.equals("")){
                c = sqldb.rawQuery("select * from "+TABLE_REPORT, null);
            }else{
                c = sqldb.rawQuery("select * from "+TABLE_REPORT+" where "+REPORT_ID+" = "+kodeReport, null);
            }
            int rowcount = 0;
            int colcount = 0;
//            File sdCardDir = Environment.getExternalStorageDirectory();
            File sdCardDir = new File(Environment.getExternalStorageDirectory() + "/Report Scanner");
            if (!sdCardDir.exists()) {
                sdCardDir.mkdir();
            }
            String filename = fileName+".csv";
            // the name of the file to export with
            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);
            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if (rowcount > 0) {
                c.moveToFirst();
                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {
                        bw.write(c.getColumnName(i) + ",");
                    } else {
                        bw.write(c.getColumnName(i));
                    }
                }
                bw.newLine();
                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);
                    for (int j = 0; j < colcount; j++) {
                        if (j != colcount - 1)
                            bw.write(c.getString(j) + ",");
                        else
                            bw.write(c.getString(j));
                    }
                    bw.newLine();
                }
                bw.flush();
                result = true;
            }
        } catch (Exception ex) {
            if (sqldb.isOpen()) {
                sqldb.close();
                result = false;
            }
        } finally {

        }
        return result;
    }
	
}
