package com.wearapp.util;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB{

	private static final String DATABASE_NAME = "heare.db";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_TABLE_1 = "history";
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_RECORD = "record";
	public static final String KEY_CREATED = "created";
	public static final String KEY_GPS = "gps";
	public static final String KEY_RECEIVER = "receiver";
	
	public final static String  TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
	
	
	private Context mContext = null;
	private DataBaseHelper dbHelper;
	private SQLiteDatabase db;
	
	
	public DB (Context context){
		
		mContext = context;
	}
			
	/*To prepare a database*/
	public DB open() throws SQLException {
		
		dbHelper = new DataBaseHelper(mContext);
		db = dbHelper.getWritableDatabase();
		
		return this;
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public Cursor getAll(){
		
		//return db.rawQuery("SELECT * FROM "+DATABASE_TABLE_1+" ORDER BY "+KEY_CREATED+" DESC", null);
		return db.query(DATABASE_TABLE_1, new String[]{KEY_ROWID,KEY_RECORD,KEY_GPS,KEY_RECEIVER,KEY_CREATED}, 
				null, null, null, null, KEY_CREATED);
	}
	
	public long create(String record){
		SimpleDateFormat df = new SimpleDateFormat(TIME_FORMAT,Locale.ENGLISH);
		Date date = new Date();
		ContentValues args = new ContentValues();
		args.put(KEY_RECORD, record);
		args.put(KEY_GPS, "text_gps");
		args.put(KEY_RECEIVER, "null_receiver");
		args.put(KEY_CREATED, df.format(date));
		
		return db.insert(DATABASE_TABLE_1, null, args);
	}
	
	public boolean delete (long rowId){
		//TODO add deleteFunction in JistoryActivity 
		return ( db.delete(DATABASE_TABLE_1, KEY_ROWID + "="+ rowId, null) > 0 );
	}
	
	
	
	public static class DataBaseHelper extends SQLiteOpenHelper {
		
		
		
		private static final String DATABASE_CREATE = "CREATE TABLE "+DATABASE_TABLE_1+"("
													+ KEY_ROWID+" INTEGER PRIMARY KEY,"
													+ KEY_RECORD+" TEXT NOT NULL,"
													+ KEY_GPS+" TEXT NOT NULL,"
													+ KEY_RECEIVER+" TEXT NOT NULL,"
													+ KEY_CREATED+" TIMESTAMP"
													+");";
		
			

		public DataBaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			//TODO
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			
			db.execSQL(DATABASE_CREATE);
			// Names table
			// Virtual table for full text search
			/*
			StringBuilder builder = new StringBuilder();
			builder.setLength(0);
			builder.append("CREATE VIRTUAL TABLE NAMES USING FTS3");
			builder.append("(");
			builder.append("name TEXT) ");		
			db.execSQL(builder.toString());
			builder=new StringBuilder();

			//dummy  data
			InsertData(db);
			 */
		}

		 void InsertData(SQLiteDatabase db)
		 {
			 ContentValues cv=new ContentValues();
				cv.put("NAME","USA");
				db.insert("Countries", "NAME", cv);
				cv.put("NAME","UK");
				db.insert("Countries", "NAME", cv);
				cv.put("NAME","Spain");
				db.insert("Countries", "NAME", cv);
				cv.put("NAME","ITALY");
				db.insert("Countries", "NAME", cv);
				cv.put("NAME","Germany");
				db.insert("Countries", "NAME", cv);

				 cv=new ContentValues();
					cv.put("name","John");
					db.insert("NAMES", "name", cv);
					cv.put("name","Jack");
					db.insert("NAMES", "name", cv);
					cv.put("name","Ann");
					db.insert("NAMES", "name", cv);
					cv.put("name","Adam");
					db.insert("NAMES", "name", cv);
					cv.put("name","Sarah");
					db.insert("NAMES", "name", cv);

		 }

		 /**
		  notice that the Names table is a VIRTUAL table. we created it as virtual to make use of Full Text Search (FTS3) feature in SQLite. 
		  this feature makes queries faster than that in regular tables.
		  then we add two functions to retrieve all rows from both tables:
		  * */
		 @Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				// TODO Auto-generated method stub
			 db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_1);
			 onCreate(db );
			}
		
		 /**
			 * Return all countries
			 * @return
			 */
			public ArrayList ArrayListgetCountries(){
				ArrayList countries=new ArrayList();
				SQLiteDatabase db=this.getReadableDatabase();
				Cursor c=db.rawQuery("select * from Countries", null);
				while(c.moveToNext()){
					String country=c.getString(1);
					countries.add(country);
				}
				c.close();
				return countries;
			}
		/**
			 * Return all names
		 * @return 
			 * @return
			 */

			public ArrayList ArrayListgetNames(){
				ArrayList names=new ArrayList();
				Cursor c=this.getReadableDatabase().rawQuery("select * FROM Names", null);
				while(c.moveToNext()){
					String name=c.getString(0);
					names.add(name);
				}
				c.close();
				return names;
			}
			
		/**	
		and another two functions to retrieve data based on a search string:
		 */
			
		/**
			 * Return all countries based on a search string
		 * @return 
			 * @return
			 */
			public ArrayList ArrayListgetCountriesSearch(String query){
				ArrayList countries=new ArrayList();
				SQLiteDatabase db=this.getReadableDatabase();
				Cursor c=db.rawQuery("select * from Countries where NAME LIKE '%"+query+"%'", null);
				while(c.moveToNext()){
					String country=c.getString(1);
					countries.add(country);
				}
				c.close();
				return countries;
			}
		/**
			 * Return all names based on a search string
			 * we use the MATCH keyword to make use of the full text search
		 * @return 
			 * @return
			 */
			public ArrayList ArrayListgetNamesSearch(String query){
				ArrayList names=new ArrayList();
				Cursor c=this.getReadableDatabase().rawQuery("select * FROM Names WHERE name MATCH '"+query+"'", null);
				while(c.moveToNext()){
					String name=c.getString(0);
					names.add(name);
				}
				c.close();
				return names;
			}

	}//DBHelper
	
	
	
	
}//DB()








	
