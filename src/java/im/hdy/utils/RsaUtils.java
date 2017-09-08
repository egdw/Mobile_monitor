package im.hdy.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by hdy on 2017/9/8.
 */
public class RsaUtils {

    /**
     * RSA加密
     *
     * @param data      加密前的数据
     * @param publicKey 公钥
     * @return
     */
    public static String encryptData(String data, String publicKey) {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(Base64.decodeBase64(publicKey.getBytes()));
            KeyFactory keyf = KeyFactory.getInstance("RSA", "BC");
            PublicKey pubKey = keyf.generatePublic(pubX509);
//            Cipher cipher = Cipher.getInstance("RSA");
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] dataToEncrypt = data.getBytes("utf-8");
            byte[] encryptedData = cipher.doFinal(dataToEncrypt);
            String encryptString = Base64.encodeBase64String(encryptedData);
            return encryptString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * RSA解密
     *
     * @param data       加密后的数据
     * @param privateKey 密钥
     * @return
     */
    public static String decryptData(String data, String privateKey) {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            privateKey = privateKey.replace(" ", "");
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey.getBytes()));
//            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(new BigInteger(mod, 16), new BigInteger(exp,16)Base64.decodeBase64(privateKey.getBytes()));
            KeyFactory keyf = KeyFactory.getInstance("RSA", "BC");
            PrivateKey privKey = keyf.generatePrivate(priPKCS8);
//            Cipher cipher = Cipher.getInstance("RSA");
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privKey);
            byte[] descryptData = Base64.decodeBase64(data);
            byte[] descryptedData = cipher.doFinal(descryptData);

//            byte[] descryptedData = cipher.update(descryptData);
            String srcData = new String(descryptedData, "utf-8");
            return srcData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回的是String数组
     * String[0]代表公钥的Base64编码
     * String[1]代表私钥Base64编码
     *
     * @return
     */
    public static String[] createKeyPairs() {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
            generator.initialize(512, new SecureRandom());
            KeyPair pair = generator.generateKeyPair();
            PublicKey pubKey = pair.getPublic();
            PrivateKey privKey = pair.getPrivate();
            byte[] pk = pubKey.getEncoded();
            byte[] privk = privKey.getEncoded();
            String strpk = new String(Base64.encodeBase64(pk));
            String strprivk = new String(Base64.encodeBase64(privk));
            String[] strings = {strpk, strprivk};
            return strings;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
