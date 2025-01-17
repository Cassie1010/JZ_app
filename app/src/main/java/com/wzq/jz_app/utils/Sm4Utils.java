//package com.wzq.jz_app.utils;
//
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
//
//import javax.crypto.Cipher;
//import javax.crypto.KeyGenerator;
//import javax.crypto.spec.SecretKeySpec;
//import java.security.Key;
//import java.security.SecureRandom;
//import java.security.Security;
//import java.util.Arrays;
//
///**
// * sm4加密算法工具类
// *
// * @author: zmm
// * @time: 2021/8/3
// */
//public class Sm4Utils {
//
//    static {
//        Security.addProvider(new BouncyCastleProvider());
//    }
//
//    private static final String ENCODING = "UTF-8";
//    public static final String ALGORITHM_NAME = "SM4";
//    // 加密算法/分组加密模式/分组填充方式
//    // PKCS5Padding-以8个字节为一组进行分组加密
//    // 定义分组加密模式使用：PKCS5Padding
//    public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";
//    // 128-32位16进制；256-64位16进制
//    public static final int DEFAULT_KEY_SIZE = 128;
//
//    //16进制密钥（忽略大小写）
//    private static final String hexKey = "ab653878bb32b6c5b8ce92fccd12096zm";
//
//    /**
//     * 生成ECB暗号
//     *
//     * @param algorithmName 算法名称
//     * @param mode          模式
//     * @param key
//     * @return
//     * @throws Exception
//     * @explain ECB模式（电子密码本模式：Electronic codebook）
//     */
//    private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key) throws Exception {
//        Cipher cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME);
//        Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
//        cipher.init(mode, sm4Key);
//        return cipher;
//    }
//
//    /**
//     * sm4加密
//     * @explain 加密模式：ECB
//     *          密文长度不固定，会随着被加密字符串长度的变化而变化
//     * @param paramStr
//     *            待加密字符串
//     * @return 返回16进制的加密字符串
//     * @throws Exception
//     */
//    public static String encryptEcb(String paramStr) throws Exception {
//        String cipherText = "";
//        // 16进制字符串-->byte[]
//        byte[] keyData = ByteUtils.fromHexString(hexKey);
//        // String-->byte[]
//        byte[] srcData = paramStr.getBytes(ENCODING);
//        // 加密后的数组
//        byte[] cipherArray = encrypt_Ecb_Padding(keyData, srcData);
//        // byte[]-->hexString
//        cipherText = ByteUtils.toHexString(cipherArray);
//        return cipherText;
//    }
//
//    /**
//     * 加密模式之Ecb
//     * @explain
//     * @param key
//     * @param data
//     * @return
//     * @throws Exception
//     */
//    public static byte[] encrypt_Ecb_Padding(byte[] key, byte[] data) throws Exception {
//        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.ENCRYPT_MODE, key);
//        return cipher.doFinal(data);
//    }
//
//    /**
//     * sm4解密
//     * @explain 解密模式：采用ECB
//     * @param cipherText
//     *            16进制的加密字符串（忽略大小写）
//     * @return 解密后的字符串
//     * @throws Exception
//     */
//    public static String decryptEcb(String cipherText) throws Exception {
//        // 用于接收解密后的字符串
//        String decryptStr = "";
//        // hexString-->byte[]
//        byte[] keyData = ByteUtils.fromHexString(hexKey);
//        // hexString-->byte[]
//        byte[] cipherData = ByteUtils.fromHexString(cipherText);
//        // 解密
//        byte[] srcData = decrypt_Ecb_Padding(keyData, cipherData);
//        // byte[]-->String
//        decryptStr = new String(srcData, ENCODING);
//        return decryptStr;
//    }
//
//    /**
//     * 解密
//     * @explain
//     * @param key
//     * @param cipherText
//     * @return
//     * @throws Exception
//     */
//    public static byte[] decrypt_Ecb_Padding(byte[] key, byte[] cipherText) throws Exception {
//        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.DECRYPT_MODE, key);
//        return cipher.doFinal(cipherText);
//    }
//
//    /**
//     * 校验加密前后的字符串是否为同一数据
//     * @explain
//     * @param cipherText
//     *            16进制加密后的字符串
//     * @param paramStr
//     *            加密前的字符串
//     * @return 是否为同一数据
//     * @throws Exception
//     */
//    public static boolean verifyEcb(String cipherText, String paramStr) throws Exception {
//        // 用于接收校验结果
//        boolean flag = false;
//        // hexString-->byte[]
//        byte[] keyData = ByteUtils.fromHexString(hexKey);
//        // 将16进制字符串转换成数组
//        byte[] cipherData = ByteUtils.fromHexString(cipherText);
//        // 解密
//        byte[] decryptData = decrypt_Ecb_Padding(keyData, cipherData);
//        // 将原字符串转换成byte[]
//        byte[] srcData = paramStr.getBytes(ENCODING);
//        // 判断2个数组是否一致
//        flag = Arrays.equals(decryptData, srcData);
//        return flag;
//    }
//
//    /**
//     * 自动生成密钥
//     * @explain
//     * @return
//     */
//    public static byte[] generateKey() throws Exception {
//        return generateKey(DEFAULT_KEY_SIZE);
//    }
//
//    /**
//     * @explain
//     * @param keySize
//     * @return
//     * @throws Exception
//     */
//    public static byte[] generateKey(int keySize) throws Exception {
//        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME);
//        kg.init(keySize, new SecureRandom());
//        return kg.generateKey().getEncoded();
//    }
//
//    public static void main(String[] args) {
//        try {
////            System.out.println(ByteUtils.toHexString(generateKey()));
//
//            String json = "{\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\"}";
//            String cipher = Sm4Utils.encryptEcb(json);
//            System.out.println(cipher);//05a087dc798bb0b3e80553e6a2e73c4ccc7651035ea056e43bea9d125806bf41c45b4263109c8770c48c5da3c6f32df444f88698c5c9fdb5b0055b8d042e3ac9d4e3f7cc67525139b64952a3508a7619
//            String cipher1 = Sm4Utils.encryptEcb(cipher);
//            System.out.println(cipher1);//05a087dc798bb0b3e80553e6a2e73c4ccc7651035ea056e43bea9d125806bf41c45b4263109c8770c48c5da3c6f32df444f88698c5c9fdb5b0055b8d042e3ac9d4e3f7cc67525139b64952a3508a7619
//            System.out.println(Sm4Utils.verifyEcb(cipher, json));// true
//            json = Sm4Utils.decryptEcb(cipher);
//            System.out.println(json);
//            json = Sm4Utils.decryptEcb(cipher1);
//            System.out.println(json);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
