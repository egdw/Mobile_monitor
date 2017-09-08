package com.test;

import im.hdy.utils.RsaUtils;
import org.junit.Test;

/**
 * Created by hdy on 2017/9/8.
 */
public class RsaTest {
    @Test
    public void rsaTest() {
        String[] pairs = RsaUtils.createKeyPairs();
        System.out.println(pairs[0]);
        System.out.println(pairs[1]);
        String str = "X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(Base64.decodeBase64(publicKey.getBytes()));";
        String data = RsaUtils.encryptData(str, pairs[0]);
        System.out.println(RsaUtils.encryptData(str, pairs[0]));
        System.out.println(RsaUtils.decryptData(data, pairs[1]));
    }


}
