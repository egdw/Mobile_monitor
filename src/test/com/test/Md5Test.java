package com.test;

import im.hdy.utils.Constants;
import im.hdy.utils.MD5Utils;
import org.junit.Test;

/**
 * Created by hdy on 2017/9/9.
 */
public class Md5Test {

    //E7F006B93CC6790C17CA68CD4AE505AA
    //ADF5B68EF4F142E410DD54E443020B51
    @Test
    public void test(){
        String s = MD5Utils.MD5("17194110228" + Constants.SAILT + "hzkjzyjsxy");
        System.out.println(s);
    }
}
