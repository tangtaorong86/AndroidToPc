package com.xlingmao.vncdemo.utils;

import android.os.SystemClock;
import android.view.MotionEvent;
import com.xlingmao.vncdemo.EventInput;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by tangyiping on 2017/1/20.
 */

public class SocketFunction {
    public void back(String...args){
        ShellUtil.execCommand("input keyevent 4",false);
    }

    public void home(String...args){
        ShellUtil.execCommand("input keyevent 3",false);
    }

    public void screenShot(String...args){
        File file = new File("/mnt/sdcard/screenshot");
        if (!file.exists()){
            file.mkdir();
        }
        ShellUtil.execCommand("/system/bin/screencap -p /sdcard/screenshot/"+SystemClock.uptimeMillis()+".png",false);
    }

    public void power(String...args){
        ShellUtil.execCommand("input keyevent 26", false);
    }

    public void reboot(String...args){
        ShellUtil.execCommand("reboot", true);
    }

    public void openWechat(String...args){
        String WXPackage = "com.tencent.mm";
        String LanucherUI = "com.tencent.mm.ui.LauncherUI";
        ShellUtil.execCommand("am start -n "+WXPackage+"/"+LanucherUI, true);
//
    }

    public void openQQ(String...args){
        String WXPackage = "com.tencent.mobileqq";
        String LanucherUI = "com.tencent.mobileqq.activity.SplashActivity";
        ShellUtil.execCommand("am start -n "+WXPackage+"/"+LanucherUI, true);
//

    }
    public void speedUp(String...args){
        ShellUtil.execCommand("am start -n com.xlingmao.powerclient/.MainActivity",true);
    }

    public void scrollUpIndex(String...args){
        EventInput eventInput = EventInput.getInstance();
        eventInput.motionScroll(args[2],Float.parseFloat(args[0]),Float.parseFloat(args[1]));
    }

    public void scrollDownIndex(String...args){
        EventInput eventInput = EventInput.getInstance();
        eventInput.motionScroll(args[2],Float.parseFloat(args[0]),Float.parseFloat(args[1]));
    }

    public void mouseUp(String...args) throws Exception {
        EventInput eventInput = new EventInput();
        eventInput.injectMotionEvent(4098, MotionEvent.ACTION_UP, SystemClock.uptimeMillis(), Float.parseFloat(args[0]), Float.parseFloat(args[1]), 1.0f);

    }

    public void mouseDown(String...args) throws Exception {
        EventInput eventInput = new EventInput();
        eventInput.injectMotionEvent(4098, MotionEvent.ACTION_DOWN, SystemClock.uptimeMillis(), Float.parseFloat(args[0]), Float.parseFloat(args[1]), 1.0f);

    }

    public void  scrollUp(String...args){
        EventInput eventInput = EventInput.getInstance();
        eventInput.scrollMotionEvent(SystemClock.uptimeMillis(),Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[0]),3);
    }

    public void scrollDown(String...args){
        EventInput eventInput = EventInput.getInstance();
        eventInput.scrollMotionEvent(SystemClock.uptimeMillis(),Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[0]),-3);
    }

    public void  move(String...args) throws InvocationTargetException, IllegalAccessException {
        EventInput eventInput = EventInput.getInstance();
        eventInput.injectMotionEvent(4098, MotionEvent.ACTION_MOVE, SystemClock.uptimeMillis(), Float.parseFloat(args[0]), Float.parseFloat(args[1]), 1.0f);
    }

    public void powerOn(String...args){
        CommandResult commandResult = ShellUtil.execCommand("dumpsys window policy|grep mScreenOnFully",false);
        //开屏
        System.out.println(commandResult.toString());
        if (commandResult.successMsg.contains("false")){
            ShellUtil.execCommand("input keyevent 26",false);
        }
    }

    public void powerOff(String...args){
        CommandResult commandResult = ShellUtil.execCommand("dumpsys window policy|grep mScreenOnFully",false);
        //关屏
        System.out.println(commandResult.toString());
        if (commandResult.successMsg.contains("true")){
            ShellUtil.execCommand("input keyevent 26",false);
        }
    }

}
