package com.wearapp.asyncTask;

import java.io.File;
import com.wearapp.util.UploadUtil;
import android.os.AsyncTask;
import android.util.Log;

public class UploadAsyncTask extends AsyncTask<File, Void, Void> {

	public static final String upLoadServerUri = "http://jadyenlin.tw/newre/savetest.php";
	public File sourceFile;

	@Override
	protected Void doInBackground(File... params) {
		this.sourceFile = params[0];
		UploadUtil uploadUtil = new UploadUtil(sourceFile);
		uploadUtil.upload();
		Log.w("UploadAsyncTask", "uploading:" + this.sourceFile.getAbsolutePath());
		return null;
	}

}
