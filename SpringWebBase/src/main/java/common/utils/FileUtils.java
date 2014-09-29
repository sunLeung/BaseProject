package common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {
	private static String defaultCharset = "utf-8";

	/**
	 * 过滤文本注释
	 * 
	 * @param path
	 * @return
	 */
	public static String readFileToJSONString(String path) {
		String src = readFileToString(path, defaultCharset);
		src = src.replaceAll("/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/", "");
		return src;
	}

	public static String readFileToString(String path) {
		return readFileToString(path, defaultCharset);
	}

	public static String readFileToString(String path, String charset) {
		FileInputStream fis = null;
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			fis = new FileInputStream(path);
			br = new BufferedReader(new InputStreamReader(fis, charset));
			String temp = "";
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fis != null)
					fis.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static void writeStringToFile(String path, String content) {
		writeStringToFile(path, content, defaultCharset);
	}

	public static void writeStringToFile(String path, String content,
			String charset) {
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		try {
			fos = new FileOutputStream(path);
			bw = new BufferedWriter(new OutputStreamWriter(fos, charset));
			bw.write(content);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fos != null)
					fos.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	protected static MessageDigest messagedigest = null;
	static {
		try {
			messagedigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static String getFileMD5String(File file) throws IOException {
		InputStream fis;
		fis = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int numRead = 0;
		while ((numRead = fis.read(buffer)) > 0) {
			messagedigest.update(buffer, 0, numRead);
		}
		fis.close();
		return bufferToHex(messagedigest.digest());
	}

	public static String getFileMD5StringEncode(File file, String key)
			throws IOException {
		InputStream fis;
		fis = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int numRead = 0;
		byte[] keys = key.getBytes();
		while ((numRead = fis.read(buffer)) > 0) {
			messagedigest.update(doMix(buffer, keys), 0, numRead);
		}
		fis.close();
		return bufferToHex(messagedigest.digest());
	}

	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4];
		char c1 = hexDigits[bt & 0xf];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

	/**
	 * 加/解密文件
	 * 
	 * @param src
	 * @param key
	 * @return
	 */
	public static byte[] doMix(byte[] src, byte[] key) {
		for (int i = 0; i < src.length; i++) {
			src[i] ^= key[i % 32];
		}
		return src;
	}
}
