package com.wearapp.asyncTask;

import java.io.File;

import com.wearapp.util.UploadUtil;

import android.os.AsyncTask;
import android.util.Log;

public class UploadAsyncTask extends AsyncTask<File, Void, Void>{
	
	public static final String requestURL="http://jadyenlin.tw/newre/savetest.php";

	@Override
	protected Void doInBackground(File... params) {
		// TODO Auto-generated method stub
		UploadUtil uploadUtil = new UploadUtil();
    	uploadUtil.executeMultiPartRequest(requestURL, params[0], params[0].getName(), "File Upload") ;
    	Log.w("UploadAsyncTask","uploading");
		return null;
	}
	

}
