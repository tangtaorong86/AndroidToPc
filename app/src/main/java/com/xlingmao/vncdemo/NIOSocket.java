package com.xlingmao.vncdemo;

import android.graphics.Bitmap;
import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * Created by dell on 2016/12/5.// TODO: 2016/12/6
 */
public class NIOSocket {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private int port;

    public NIOSocket(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        try {
           new NIOSocket(Integer.parseInt(args[0])).startServer();
        } catch (Exception e) {

        }
    }

    private void startServer() throws IOException {


        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress("localhost", port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true){
            int isHasSelector = selector.select();
            if(isHasSelector > 0){
                Iterator<SelectionKey> selectorKeys = selector.selectedKeys().iterator();
                while (selectorKeys.hasNext()) {
                    SelectionKey key = selectorKeys.next();
                    selectorKeys.remove();
                    if (key.isAcceptable()) {
                        SocketChannel clientSocket = serverSocketChannel.accept();
                        clientSocket.configureBlocking(false);
                        clientSocket.register(selector, SelectionKey.OP_WRITE);//初始为accept ==> 设置为读状态
                    }else if(key.isWritable()){
                        SocketChannel clientSocket = (SocketChannel) key.channel();
                        try {

                            Bitmap bitmap = screenshot();
                            ByteArrayOutputStream bout = new ByteArrayOutputStream();
                            byte[] bytes ;
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, bout);
                            bytes = bout.toByteArray();
                            int ch = bytes.length;
                            byte[] bt2 = new byte[4];
                            writeInt(bt2,ch);
                            clientSocket.write(ByteBuffer.wrap(bt2));
                            clientSocket.write(ByteBuffer.wrap(bytes));
                            Thread.sleep(300);
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            }
        }
    }
    public static Bitmap screenshot() {
        String surfaceClassName;
        int x = 540;
        int y = 960;
        if (Build.VERSION.SDK_INT <= 17) {
            surfaceClassName = "android.view.Surface";
        } else {
            surfaceClassName = "android.view.SurfaceControl";
        }
        Class clz = null;
        Bitmap b = null;
        try {
            clz = Class.forName(surfaceClassName);
            Method method = clz.getDeclaredMethod("screenshot",int.class,int.class);

            b = (Bitmap) method.invoke(null,x,y);
            if (b==null) {
                System.out.println("bitmap 为null");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return Bitmap.createBitmap(b, 0, 0, x,y);
    }
    private static void writeInt(byte[] bt2, int ch) throws IOException {
        bt2[0] = (byte) ((ch >> 24) & 0xFF);
        bt2[1] = (byte) ((ch >> 16) & 0xFF);
        bt2[2] = (byte) ((ch >> 8) & 0xFF);
        bt2[3] = (byte) ((ch >> 0) & 0xFF);
    }
}
