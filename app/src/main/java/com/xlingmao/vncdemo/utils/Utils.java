package com.xlingmao.vncdemo.utils;

import android.graphics.Bitmap;
import android.os.Build;

import com.xlingmao.vncdemo.ScreenConfig;

import java.lang.reflect.Method;
import java.security.MessageDigest;

/**
 * Created by tangyiping on 2017/1/7.
 */

public class Utils {
    /**
     * md5 转换
     * @param data
     * @return
     */
    public static String MD5(byte[] data){
        String sReturnCode = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            byte b[] = md.digest();
            int i;
            StringBuilder sb = new StringBuilder("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(i));
            }

            sReturnCode = sb.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sReturnCode;
    }

    /**
     * 截图
     * @return
     * @throws Exception
     */


//    private static int width = 540;
//    private static int height = 960;
   // private static int width = 480;
    //private static int height = 854;

    public static Bitmap screenshot() throws Exception {
        String surfaceClassName;
        int x = ScreenConfig.COOLPAD[0];
        int y = ScreenConfig.COOLPAD[1];
        if (Build.VERSION.SDK_INT <= 17) {
            surfaceClassName = "android.view.Surface";
        } else {
            surfaceClassName = "android.view.SurfaceControl";
        }
        Class clz = Class.forName(surfaceClassName);
        Method method = clz.getDeclaredMethod("screenshot",int.class,int.class);
        Bitmap b = (Bitmap) method.invoke(null,x,y);

        if (b==null) {
            System.out.println("bitmap 为null");
        }
        return Bitmap.createBitmap(b, 0, 0, x,y);
    }
}
