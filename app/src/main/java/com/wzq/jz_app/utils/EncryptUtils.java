//package com.wzq.jz_app.utils;
//
//import org.apache.commons.codec.binary.Base64;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//
//import javax.crypto.Cipher;
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//
///**
// * 加密/解密
// * @author mengmeng_zuo
// * 2019年3月15日
// */
//public class EncryptUtils {
//
//	/**
//	 * 定义加密算法,可用，DES,DESede,Blowfish
// 	 */
//	private static final String ALGORITHM = "AES/GCM/NoPadding";
//	public static final int XOR_CONST = 0x80;
//	//原始密钥字符串
//	public static final String key = "2cRDP5kNgHYRBdlhV17myIqMbcYMDFWk";
//
//	/**
//	 * 3des解码
//	 *
//	 * @param value
//	 *            待解密字符串
//	 * @return
//	 * @throws Exception
//	 */
//	public static String decrypt3DES(String value) throws Exception {
//		byte[] b = decryptMode(getKeyBytes(key), new BASE64Decoder()(value));
//		return new String(b);
//	}
//
//	/**
//	 * 3des加密
//	 *
//	 * @param value
//	 *            待加密字符串
//	 * @param key
//	 *            原始密钥字符串
//	 * @return
//	 * @throws Exception
//	 */
//	public static String encrypt3DES(String value, String key) throws Exception {
//		return byte2Base64(encryptMode(getKeyBytes(key), value.getBytes()));
//	}
//
//	/**
//	 * 计算24位长的密码byte值,首先对原始密钥做MD5算hash值，再用前8位数据对应补全后8位
//	 *
//	 * @param strKey
//	 *            密钥
//	 * @return
//	 * @throws Exception
//	 */
//	public static byte[] getKeyBytes(String strKey) throws Exception {
//		if (null == strKey || strKey.length() < 1) {
//			throw new Exception("key is null or empty!");
//		}
//		MessageDigest alg = MessageDigest.getInstance("MD5");
//		alg.update(strKey.getBytes());
//		byte[] bkey = alg.digest();
//		int start = bkey.length;
//		byte[] bkey24 = new byte[24];
//		for (int i = 0; i < start; i++) {
//			bkey24[i] = bkey[i];
//		}
//		for (int i = start; i < 24; i++) {// 为了与.net16位key兼容
//			bkey24[i] = bkey[i - start];
//		}
//		return bkey24;
//	}
//
//	/**
//	 * 加密
//	 *
//	 * @param keybyte 为加密密钥
//	 *            ，长度为24字节
//	 * @param src
//	 *            为被加密的数据缓冲区（源）
//	 * @return
//	 */
//	public static byte[] encryptMode(byte[] keybyte, byte[] src) {
//		try {
//			// 生成密钥
//			SecretKey deskey = new SecretKeySpec(keybyte, ALGORITHM); // 加密
//			Cipher c1 = Cipher.getInstance(ALGORITHM);
//			c1.init(Cipher.ENCRYPT_MODE, deskey);
//			return c1.doFinal(src);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return new byte[0];
//	}
//
//	/**
//	 * 解密
//	 *
//	 * @param keybyte
//	 *            解密密钥，长度为24字节
//	 * @param src
//	 *            解密后的缓冲区
//	 * @return
//	 */
//	public static byte[] decryptMode(byte[] keybyte, byte[] src) {
//		try {
//			// 生成密钥
//			SecretKey deskey = new SecretKeySpec(keybyte, ALGORITHM);
//			// 解密
//			Cipher c1 = Cipher.getInstance(ALGORITHM);
//			c1.init(Cipher.DECRYPT_MODE, deskey);
//			return c1.doFinal(src);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return new byte[0];
//	}
//
//	/**
//	 * 转换成base64编码
//	 */
//	public static String byte2Base64(byte[] b) {
//		byte[] b1 = Base64.encodeBase64(b);
//		return new String(b1, StandardCharsets.UTF_8);
//	}
//
//	/**
//	 * 转换成十六进制字符串
//	 */
//	public static String byte2hex(byte[] b) {
//		StringBuilder hs = new StringBuilder();
//		String stmp = "";
//		for (int n = 0; n < b.length; n++) {
//			stmp = (Integer.toHexString(b[n] & 0XFF));
//			if (stmp.length() == 1) {
//				hs.append("0");
//			}
//			hs.append(stmp);
//			if (n < b.length - 1) {
//				hs.append(":");
//			}
//		}
//		return hs.toString().toUpperCase();
//	}
//
//	/**
//     * 异或的一个特点： a^b = c c^b = a
//     * 所以加解密都用一个函数
//     * @param src
//     * @param dest
//     * @throws Exception
//     **/
//	public static void xorEn(File src, File dest) {
//		try (FileInputStream fis = new FileInputStream(src);
//			 FileOutputStream fos = new FileOutputStream(dest)
//		){
//			byte[] bs = new byte[1024];
//			int len = 0;
//			while ((len = fis.read(bs)) != -1) {
//				for (int i = 0; i < len; i++) {
//					bs[i] ^= XOR_CONST;
//				}
//				fos.write(bs, 0, len);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 3DEC 加密解密测试
//	 */
//	public static void main(String[] args) throws Exception {
//		/*String key = RandomUtils.randomKey(32);
//		System.out.println(key);
//		System.out.println(key.length());
//
//		String password = "5b6c03ece4b0796b85933188_5b6c03ece4b0796b85933188_"+System.currentTimeMillis();
////		String password = "121242";
//		String result = EncryptUtils.encrypt3DES(password, key);
//		System.out.println("调用原始密钥算加密结果:" + result);
//		System.out.println("调用原始密钥算加密结果:" + result.length());
//		System.out.println("调用原始密钥算解密结果:" + EncryptUtils.decrypt3DES(result, key));*/
//	}
//}
