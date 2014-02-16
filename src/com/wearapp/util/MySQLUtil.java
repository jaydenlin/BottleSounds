package com.wearapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.AsyncTask;
import android.util.Log;
import com.facebook.model.GraphPlace;
import com.wearapp.exception.UploadUtil.UploadFileNotAssign;

public class MySQLUtil {

	private static final String host = "54.251.252.71";
	private static final String port = "3306";
	private static final String dbName = "heare";
	private static final String dbUser = "heare";
	private static final String dbPassword = "hearerock";
	private static String uploadFileName;

	public static void insertVoice(String message, String tag, GraphPlace location) throws UploadFileNotAssign {
	
		if(uploadFileName==null){
			throw new UploadFileNotAssign("You have to call prepareInsertVoiceQuery() in advance to assign upload file");
		}
		
		new InsertVoiceAsyncTask(message, tag, location).execute();
	}

	public static void prepareInsertVoiceQuery(String uploadFileName) {
		MySQLUtil.uploadFileName = uploadFileName;
	}

	private static String getDatetime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// 時間格式轉化
		Date date = new Date();
		String strTime = dateFormat.format(date);

		return strTime;
	}

	static class InsertVoiceAsyncTask extends AsyncTask<Void, Void, Void> {

		private String message;
		private String tag;
		private GraphPlace location;

		public InsertVoiceAsyncTask(String message, String tag, GraphPlace location) {
			this.message = message;
			this.tag = tag;
			this.location = location;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + dbName+"?useUnicode=true&characterEncoding=utf-8", dbUser, dbPassword);

				Statement stmt = conn.createStatement();
				stmt.executeUpdate("insert into voice(fb_uid_recorder,latitude,longitude,path,datetime,place_name,tag,message) values (" + "1,"
						+ location.getLocation().getLatitude() + "," + location.getLocation().getLongitude() + ",'/var/www/heare/audio/" + uploadFileName + "','" + getDatetime()
						+ "','" + location.getName() + "','" + tag + "','" + message + "')");

				conn.close();
				uploadFileName = null;

			} catch (ClassNotFoundException e) {
				Log.w(MySQLUtil.class.getSimpleName(), "ClassNotFoundException: MySQL Not Connected");
			} catch (SQLException e) {
				Log.w(MySQLUtil.class.getSimpleName(), "SQLException: MySQL Not Connected  " + e);
			}
			return null;
		}

	}

}
