package com.wearapp.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class UploadUtil {

	private HttpURLConnection httpURLConnection;
	private FileInputStream fileInputStream;
	private DataOutputStream dataOutputStream;
	private String uplaodServerCGI = "http://jadyenlin.tw/heare/savetest.php";
	private File uploadedFile;
	private String parameterNameToServer;

	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary = "*****";

	int bytesRead, bytesAvailable, bufferSize;
	byte[] buffer;
	int maxBufferSize = 1 * 1024 * 1024;

	public UploadUtil(File uploadFile, String parameterNameToServer) {
		this.uploadedFile = uploadFile;
		this.parameterNameToServer = parameterNameToServer;
	}

	public void upload() {
		setFileInputStream(this.uploadedFile);
		setSimpleHttpURLConnection(this.uplaodServerCGI);
		setSimpleDataOutputStream(this.httpURLConnection);
		writeDataOutputStrean();
		insertToMySQL();
		
	}
	
	private void insertToMySQL() {
		
		//MySQLUtil.insertVoice(uploadedFile.getName());
		MySQLUtil.prepareInsertVoiceQuery(this.uploadedFile.getName());
//		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//時間格式轉化
//		Date date = new Date();
//		String strTime = dateFormat.format(date);
//		
//		Log.w(this.getClass().getSimpleName(),"insert into voice(fb_uid_recorder,latitude,longitude,path,datetime,place_name,tag,message) values (" +
//				"1,25.23456,123.45666,'/var/www/heare/audio/"+this.uploadedFile.getName()+"','"+strTime+"','我家','test','我在我家')");
//		
//			try {
//				Class.forName("com.mysql.jdbc.Driver");
//				Connection conn = DriverManager.getConnection(
//						"jdbc:mysql://54.251.252.71:3306/heare", "heare", "hearerock");
//				
//				Statement stmt = conn.createStatement();
//				stmt.executeUpdate("insert into voice(fb_uid_recorder,latitude,longitude,path,datetime,place_name,tag,message) values (" +
//						"1,25.23456,123.45666,'/var/www/heare/audio/"+this.uploadedFile.getName()+"','"+strTime+"','我家','test','我在我家')");
//				
//				conn.close();
//				
//			} catch (ClassNotFoundException e) {
//				Log.w(this.getClass().getSimpleName(),"ClassNotFoundException: MySQL Not Connected");
//			} catch (SQLException e) {
//				Log.w(this.getClass().getSimpleName(),"SQLException: MySQL Not Connected  "+e);
//				//tv.setText("Connection ERROR");
//			}
	}
	

	private void setSimpleHttpURLConnection(String uplaodServerCGI) {
		try {
			httpURLConnection = (HttpURLConnection) new URL(uplaodServerCGI).openConnection();
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
			httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			httpURLConnection.setRequestProperty(parameterNameToServer, uploadedFile.getAbsolutePath());

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private void setSimpleDataOutputStream(HttpURLConnection settledHttpURLConnection) {
		try {
			dataOutputStream = new DataOutputStream(settledHttpURLConnection.getOutputStream());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeDataOutputStrean() {
		try {
			// /////////////////
			// /write data start
			// /////////////////
			dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
			dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterNameToServer + "\";filename=\"" + uploadedFile.getAbsolutePath() + "\"" + lineEnd);
			dataOutputStream.writeBytes(lineEnd);

			// read file and write it into form...
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {

				dataOutputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			}

			// send multipart form data necesssary after file data...
			dataOutputStream.writeBytes(lineEnd);
			dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
			// /////////////////
			// /write data end
			// /////////////////

			// Responses from the server (code and message)
			int serverResponseCode = httpURLConnection.getResponseCode();
			String serverResponseMessage = httpURLConnection.getResponseMessage();

			Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

			// close the streams //
			fileInputStream.close();
			dataOutputStream.flush();
			dataOutputStream.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void setFileInputStream(File uploadedFile) {
		try {
			fileInputStream = new FileInputStream(uploadedFile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

}
