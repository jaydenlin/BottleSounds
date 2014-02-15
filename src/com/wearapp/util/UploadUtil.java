package com.wearapp.util;

import java.io.File;

import com.wearapp.exception.UploadUtil.UploadFileNotAssign;

public class UploadUtil {

	private static File uploadFile;
	
	public static void setUploadFile(File uploadFile){
		UploadUtil.uploadFile=uploadFile;
	}
	
	public static File getUploadFile() throws UploadFileNotAssign{
		if(UploadUtil.uploadFile==null){
			throw new UploadFileNotAssign("UploadFile is not assigned");
		}
		return UploadUtil.uploadFile;
	}
	
	public static byte[] getBytesofUploadFile() {
		
		try {
			return ByteUtils.FileToByte(getUploadFile());
		} catch (UploadFileNotAssign e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}
}
