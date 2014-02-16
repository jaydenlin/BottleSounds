package com.wearapp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteUtils {
	private static ByteBuffer buffer = ByteBuffer.allocate(8);

	public static byte[] longToBytes(long x) {
		buffer.putLong(0, x);
		return buffer.array();
	}

	public static long bytesToLong(byte[] bytes) {
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();// need flip
		return buffer.getLong();
	}

	public static byte[] int2byte(int integer) {
		final BigInteger bi = BigInteger.valueOf(integer);
		final byte[] bytes = bi.toByteArray();

		// System.out.println(integer +" int2byte " + Arrays.toString(bytes));

		return bytes;
	}

	public static int bytes2int(byte[] bytes) {
		final int i = new BigInteger(bytes).intValue();
		// System.out.println("byte2int"+i);

		return i;
	}

	public static byte[] FileToByte(File file) {

		FileInputStream fileInputStream = null;
		byte[] byteFile = new byte[(int) file.length()];
		// convert file into array of bytes
		try {
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(byteFile);
			fileInputStream.close();
			
			return byteFile;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return byteFile;
		}

	}

}