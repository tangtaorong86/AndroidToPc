package com.xlingmao.vncdemo.utils;

import com.xlingmao.vncdemo.EventConfig;
import com.xlingmao.vncdemo.EventInput;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by tangyiping on 2017/1/20.
 */

public enum Config {

    MOUSE_SCROLL_INDEX_UP(EventConfig.MOUSE_MOTION_TYPE,EventConfig.MOUSE_SCROLL_INDEX_UP,"scrollUpIndex"),

    MOUSE_SCROLL_INDEX_DOWN(EventConfig.MOUSE_MOTION_TYPE,EventConfig.MOUSE_SCROLL_INDEX_DOWN,"scrollDownIndex"),

    MOUSE_MOTION_UP(EventConfig.MOUSE_MOTION_TYPE,EventConfig.MOUSE_MOTION_UP,"mouseUp") ,

    MOUSE_MOTION_DOWN(EventConfig.MOUSE_MOTION_TYPE,EventConfig.MOUSE_MOTION_DOWN,"mouseDown"),

    MOUSE_SCROLL_UP(EventConfig.MOUSE_MOTION_TYPE,EventConfig.MOUSE_SCROLL_UP,"scrollUp"),

    MOUSE_SCROLL_DOWN(EventConfig.MOUSE_MOTION_TYPE,EventConfig.MOUSE_SCROLL_DOWN,"scrollDown"),

    MOUSE_MOTION_MOVE(EventConfig.MOUSE_MOTION_TYPE,EventConfig.MOUSE_MOTION_MOVE,"move"),

    BACK_HOME(EventConfig.MOUSE_MOTION_TYPE_SECOND,EventConfig.BACK_HOME,"home"),

    BACK_BEFORE(EventConfig.MOUSE_MOTION_TYPE_SECOND,EventConfig.BACK_BEFORE,"back"),

    SCREEN_SHOT(EventConfig.MOUSE_MOTION_TYPE_SECOND,EventConfig.SCREEN_SHOT,"screenShot"),

    POWER(EventConfig.MOUSE_MOTION_TYPE_SECOND,EventConfig.POWER,"power"),

    REBOOT(EventConfig.MOUSE_MOTION_TYPE_SECOND,EventConfig.REBOOT,"reboot"),

    OPEN_WECHAT(EventConfig.MOUSE_MOTION_TYPE_SECOND,EventConfig.OPEN_WECHAT,"openWechat"),

    OPEN_QQ(EventConfig.MOUSE_MOTION_TYPE_SECOND,EventConfig.OPEN_QQ,"openQQ"),

    SPEED_UP(EventConfig.MOUSE_MOTION_TYPE_SECOND,EventConfig.SPEED_UP,"speedUp"),

    POWER_OFF(EventConfig.MOUSE_MOTION_TYPE_SECOND,EventConfig.POWER_OFF,"powerOff"),

    POWER_ON (EventConfig.MOUSE_MOTION_TYPE_SECOND,EventConfig.POWER_ON,"powerOn");

    private String kind;
    private String type;
    private String function;

    Config(String kind, String type, String function) {
        this.kind = kind;
        this.type = type;
        this.function = function;
    }

    public String getType() {
        return type;
    }
    public String getFunction() {
        return function;
    }
    public String getKind() {
        return kind;
    }

    public static Config filtrate(String kind,String type){
        for (Config config : Config.values()) {
            if(config.getKind().equals(kind)&&config.getType().equals(type)) {
                return config;
            }
        }
        return null;
    }
    public static void main(String args[]) {
        Config config = filtrate(EventConfig.MOUSE_MOTION_TYPE, EventConfig.MOUSE_MOTION_UP);

        Method[] methods = SocketFunction.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(config.getFunction()))
                System.out.println(method.getName());
        }


    }

}
