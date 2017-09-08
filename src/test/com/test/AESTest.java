package com.test;

import im.hdy.utils.MD5Utils;
import org.junit.Test;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by hdy on 2017/9/8.
 */
public class AESTest {

    @Test
    public void test() {
        //纯英文短信160个字母，但是换成汉字就只有70个汉字合140字节。因为有20字节要用来标记这是中文短信。
        String content = "纯英文短信160个字母，但是换成汉字就只有70个汉字合140字节。因为有20字节要用来标记这是中文短信。纯英文短信160个字母，但是换成汉字就只有70个汉字合140字节。因为有20字节要用来标记这是中文短信。"+"纯英文短信160个字母，但是换成汉字就只有70个汉字合140字节。因为有20字节要用来标记这是中文短信。纯英文短信160个字母，但是换成汉字就只有70个汉字合140字节。因为有20字节要用来标记这是中文短信。纯英文短信160个字母，但是换成汉字就只有70个汉字合140字节。因为有20字节要用来标记这是中文短信。";
        String password = MD5Utils.MD5("hzkjzyjsxy");
        // 加密
        System.out.println("加密前：" + content);
        String s = encrypt(content, password);
        System.out.println("加密后："+s);
        // 解密

        String s1 = decrypt(s, password);
        System.out.println("解密后：" +s1);

    }
    public static String encrypt(String bef_aes, String password) {
        byte[] byteContent = null;
        try {
            byteContent = bef_aes.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encrypt(byteContent,password);
    }
    public static String encrypt(byte[] content, String password) {
        try {
            SecretKey secretKey = getKey(password);
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            String aft_aes = parseByte2HexStr(result);
            return aft_aes; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String decrypt(String aft_aes, String password) {
        try {
            byte[] content = parseHexStr2Byte(aft_aes);
            SecretKey secretKey = getKey(password);
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            try {
                cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
            byte[] result = cipher.doFinal(content);
            String bef_aes = new String(result);
            return bef_aes; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int value = Integer.parseInt(hexStr.substring(i*2, i*2+2), 16);
            result[i] = (byte)value;
        }
        return result;
    }
    public static SecretKey getKey(String strKey) {
        try {
            KeyGenerator _generator = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(strKey.getBytes());
            _generator.init(128,secureRandom);
            return _generator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("初始化密钥出现异常");
        }
    }

}
