package com.wzq.jz_app.utils;

import java.security.MessageDigest;

/**
 * md5加密
 * @author: zmm
 * @time: 2020/12/3 20:13
 */
public class MD5Util {
	//盐，用于混交md5
	private static final String slat = "aaf#=uh#%%**)hsam!";
	public static String encrypt(String dataStr) {
		try {
			dataStr = dataStr + slat;
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(dataStr.getBytes("UTF8"));
			byte s[] = m.digest();
			String result = "";
			for (int i = 0; i < s.length; i++) {
				result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
//			logger.error("md5加密失败", e);
		}
		return "";
	}

	public static String toHexString(byte[] var0) {
		if (var0 == null) {
			return null;
		} else {
			StringBuffer var1 = new StringBuffer(2 * var0.length);

			for(int var3 = 0; var3 < var0.length; ++var3) {
				int var2 = var0[var3] & 255;
				if (var2 < 16) {
					var1.append('0');
				}

				var1.append(Integer.toString(var2, 16));
			}

			return var1.toString();
		}
	}

}
