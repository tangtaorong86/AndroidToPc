package com.xlingmao.vncdemo;

import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by tangyiping on 2017/1/7.
 */

public class EventInput {
    static EventInput eventInput;
    Method injectInputEventMethod;
    InputManager im;

    public EventInput()  {

    }
    public static EventInput getInstance(){

           if (eventInput == null) {
               try {
                   eventInput = new EventInput();
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
           return eventInput;
    }


    private Method getMotionEvent(){
        if(this.injectInputEventMethod != null){
            return this.injectInputEventMethod;
        }

        synchronized (this){
            try{
                //Get the instance of InputManager class using reflection
                String methodName = "getInstance";
                Object[] objArr = new Object[0];
                im = (InputManager) InputManager.class.getDeclaredMethod(methodName, new Class[0])
                        .invoke(null, objArr);

                //Make MotionEvent.obtain() method accessible
                methodName = "obtain";
                MotionEvent.class.getDeclaredMethod(methodName, new Class[0]).setAccessible(true);

                //Get the reference to injectInputEvent method
                methodName = "injectInputEvent";
                injectInputEventMethod = InputManager.class.getMethod(
                        methodName, new Class[]{InputEvent.class, Integer.TYPE});
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        return this.injectInputEventMethod;

    }


    public void injectMotionEvent(int inputSource, int action, long when, float x, float y,
                                  float pressure) throws InvocationTargetException, IllegalAccessException {
        MotionEvent event = MotionEvent.obtain(when, when, action, x, y, pressure, 1.0f, 0, 1.0f, 1.0f, 0, 0);
        event.setSource(inputSource);
        getMotionEvent().invoke(im, new Object[]{event, Integer.valueOf(0)});
    }
    public void scrollMotionEvent(long when,int startX,int startY,float endX,float endY){
        MotionEvent.PointerProperties[] arrayOfPointerProperties = new MotionEvent.PointerProperties[1];
        arrayOfPointerProperties[0] = new MotionEvent.PointerProperties();
        arrayOfPointerProperties[0].clear();
        arrayOfPointerProperties[0].id = 0;
        MotionEvent.PointerCoords[] arrayOfPointerCoords = new MotionEvent.PointerCoords[1];
        arrayOfPointerCoords[0] = new MotionEvent.PointerCoords();
        arrayOfPointerCoords[0].clear();
        arrayOfPointerCoords[0].x = startX;
        arrayOfPointerCoords[0].y = startY;
        arrayOfPointerCoords[0].pressure = 1.0F;
        arrayOfPointerCoords[0].size = 1.0F;
        arrayOfPointerCoords[0].setAxisValue(MotionEvent.AXIS_Y, endX);
        arrayOfPointerCoords[0].setAxisValue(9, endY);
        MotionEvent localMotionEvent = MotionEvent.obtain(when, when+500, 8, 1, arrayOfPointerProperties, arrayOfPointerCoords, 0, 0, 1.0F, 1.0F, 0, 0, 4098, 0);
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = localMotionEvent;
        arrayOfObject[1] = Integer.valueOf(0);
        try {
            getMotionEvent().invoke(im, arrayOfObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void injectKeyEvent(KeyEvent event)
            throws InvocationTargetException, IllegalAccessException {
        getMotionEvent().invoke(im, new Object[]{event, Integer.valueOf(0)});
    }

    public void simulate(int action, long startTime, long endTime,float mLastMotionX ) {
        // specify the property for the two touch points
        MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[1];
        MotionEvent.PointerProperties pp = new MotionEvent.PointerProperties();
        pp.id = 0;
        pp.toolType = MotionEvent.TOOL_TYPE_FINGER;

        properties[0] = pp;

        // specify the coordinations of the two touch points
        // NOTE: you MUST set the pressure and size value, or it doesn't work
        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[1];
        MotionEvent.PointerCoords pc = new MotionEvent.PointerCoords();
        pc.x = mLastMotionX;
        pc.pressure = 1;
        pc.size = 1;
        pointerCoords[0] = pc;
        final MotionEvent ev = MotionEvent.obtain(
                startTime, endTime, action, 1, properties,
                pointerCoords, 0,  0, 1, 1, 0, 0, 0, 0);
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = ev;
        arrayOfObject[1] = Integer.valueOf(0);
        try {
            getMotionEvent().invoke(im, arrayOfObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    public  void motionScroll(String type,float x,float y) {
        try {
            injectMotionEvent(4098, MotionEvent.ACTION_DOWN, SystemClock.uptimeMillis(), x, y, 1.0f);
            System.out.println("do up");
            long time = SystemClock.uptimeMillis();
            System.out.println("do move");
            for (int i = 1;i<6;i++) {
                if (EventConfig.MOUSE_SCROLL_DOWN.equals(type)) {
                    injectMotionEvent(4098, MotionEvent.ACTION_MOVE, time, x , y - i * 10, 1.0f);
                }else if (EventConfig.MOUSE_SCROLL_UP.equals(type)){
                    injectMotionEvent(4098, MotionEvent.ACTION_MOVE, time, x , y + i * 10, 1.0f);
                }else if (EventConfig.MOUSE_SCROLL_INDEX_DOWN.equals(type)) {
                    injectMotionEvent(4098, MotionEvent.ACTION_MOVE, SystemClock.uptimeMillis(), x - i * 20, y, 1.0f);
                }else if (EventConfig.MOUSE_SCROLL_INDEX_UP.equals(type)){
                    injectMotionEvent(4098, MotionEvent.ACTION_MOVE, SystemClock.uptimeMillis(), x + i * 20, y, 1.0f);
                }
            }
            if (EventConfig.MOUSE_SCROLL_UP.equals(type)) {
                injectMotionEvent(4098, MotionEvent.ACTION_UP, time, x, y + 60, 1.0f);
            }else if (EventConfig.MOUSE_SCROLL_DOWN.equals(type)){
                injectMotionEvent(4098, MotionEvent.ACTION_UP, time, x , y - 60, 1.0f);
            }else if (EventConfig.MOUSE_SCROLL_INDEX_UP.equals(type)) {
                injectMotionEvent(4098, MotionEvent.ACTION_UP, SystemClock.uptimeMillis(), x + 120, y, 1.0f);
            }else if (EventConfig.MOUSE_SCROLL_INDEX_DOWN.equals(type)){
                injectMotionEvent(4098, MotionEvent.ACTION_UP, SystemClock.uptimeMillis(), x - 120, y, 1.0f);
            }
            System.out.println("type = "+type +", x = "+x+",y = "+y);

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
