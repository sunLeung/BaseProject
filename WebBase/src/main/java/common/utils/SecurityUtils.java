package common.utils;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class SecurityUtils {

	private final static int max_auto_id = 9000000;

	private static final AtomicInteger[] _uuidCount = new AtomicInteger[10];
	
	static {
		for (int i = 0; i < _uuidCount.length; i++)
			_uuidCount[i] = new AtomicInteger(1);
	}

	private static long gen_uuid(int c) {
		long uuid = 0;
		long time = System.currentTimeMillis();
		int count = _uuidCount[c].incrementAndGet();
		time = time / 60000L;// 只取分钟
		uuid = time * 100000000000L + 999888 * 100000000L + c * 10000000L + count;
		if (count > max_auto_id)
			_uuidCount[c].set(0);
		return uuid;
	}

	/**
	 * 创建唯一字符串UUID
	 * @return
	 */
	public static String createUUIDString(){
		return java.util.UUID.randomUUID().toString().replace("-", "");
	}
	
	/**
	 * 字符串加密
	 * @param src
	 * @param key
	 * @return
	 */
	public static String encryptString(String src, String key) {
		if (StringUtils.isBlank(src)||StringUtils.isBlank(key))
			return null;
		try {
			SecureRandom sr = new SecureRandom();
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
			byte[] srcbyte=cipher.doFinal(src.getBytes());
			
			StringBuilder hs = new StringBuilder();
			String stmp = "";
			for (int n = 0; n < srcbyte.length; n++) {
				stmp = (Integer.toHexString(srcbyte[n] & 0XFF));
				if (stmp.length() == 1)
					hs = hs.append("0").append(stmp);
				else
					hs = hs.append(stmp);
			}
			return hs.toString().toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 字符串解密
	 * @param src
	 * @param key
	 * @return
	 */
	public static String decryptString(String src, String key) {
		if (StringUtils.isBlank(src)||StringUtils.isBlank(key))
			return null;
		try {
			SecureRandom sr = new SecureRandom();
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
			
			if ((src.getBytes().length % 2) != 0)
				throw new IllegalArgumentException("The length is not even.");
			byte[] b2 = new byte[src.getBytes().length / 2];
			for (int n = 0; n < src.getBytes().length; n += 2) {
				String item = new String(src.getBytes(), n, 2);
				b2[n / 2] = (byte) Integer.parseInt(item, 16);
			}
			byte[] srcbyte=cipher.doFinal(b2);
			return new String(srcbyte);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 登陆密码加密
	 * @param password
	 * @return
	 */
	public static String encryptPassword(String password){
		return encryptString(password, Def.PasswordSecret);
	}
	
	/**
	 * 登陆密码解密
	 * @param encryptPassword
	 * @return
	 */
	public static String decryptPassword(String encryptPassword){
		return decryptString(encryptPassword, Def.PasswordSecret);
	}
}
