package com.xlingmao.vncdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.SystemClock;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.Surface;

import com.xlingmao.vncdemo.utils.Config;
import com.xlingmao.vncdemo.utils.SocketFunction;
import com.xlingmao.vncdemo.utils.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by tangyiping on 16/11/25.
 */

public class Main {
    private static  ServerSocket serverSocket;
    private static boolean flag;
    private static int port;
    private static Matrix matrix;
    private static EventInput eventInput = null;
    private static int times = 0;
    private static ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(50);
    public static void main(String args[])   {
        System.out.println("Andcast Main Entry!");
        try {
            port = Integer.parseInt(args[0]);
            serverSocket = new ServerSocket(port);
            //serverSocket.bind(new InetSocketAddress("localhost",port));
            HeartClient heartClient;
            matrix = new Matrix();
            matrix.setScale(0.55f, 0.55f);

            try {

                eventInput = new EventInput();

            } catch (Exception e) {
                e.printStackTrace();
            }

            while (true) {
                System.out.println( " is connecting");
                Socket client = serverSocket.accept();
                heartClient = new HeartClient(client);
                //心跳线程
                flag = true;
                new Thread(heartClient).start();
                new Thread(new HandlerClient(client)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 截图
     * @return
     * @throws Exception
     */



    /**
     * 心跳线程
     */
    private static class HeartClient implements Runnable {
        private Socket socket;

        HeartClient(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;
            try {
                inputStream = socket.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (flag) {
                try {
//                        if (socket.isClosed())
//                            return;
                    String motionType = bufferedReader.readLine();

                    System.out.println("======> mtype:" + motionType + "  [ " + System.currentTimeMillis()+" ]");

                    if(motionType==null) continue;

                    String[] pointX = motionType.split(",");
                    String i = pointX[0];
                    if (i.equals("1")||i.equals("2")||i.equals("3")){
                        if (i.equals("2")||i.equals("3")) {
                            final String type;
                            final String x;
                            final String y;
                            try {
                                type = pointX[1];
                                x = pointX[2];
                                y = pointX[3];
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }
                            Config config = Config.filtrate(i, type);
                            Method[] instance = SocketFunction.class.getDeclaredMethods();
                            for (final Method method : instance) {

                                if (config.getFunction().equals(method.getName())) {
                                    System.out.println(config.getFunction());
                                    threadPoolExecutor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                method.invoke(SocketFunction.class.newInstance(), new Object[]{new String[]{x, y, type}});
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
                                            } catch (InvocationTargetException e) {
                                                e.printStackTrace();
                                            } catch (InstantiationException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        }

                        times = 0;
                    }else {
                        times++;
                        if (times == 300) {
                            stopServer(socket);
                            times = 0;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        stopServer(socket);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
    /**
     * 连接线程
     */
    private static class HandlerClient implements Runnable{

        private Socket socket;

        HandlerClient(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {

                //System.out.println("client: " + socket);


                OutputStream outputStream = socket.getOutputStream();
                Bitmap bitmap = null;
                //OutputStream outs = socket.getOutputStream();
                while (flag) {
                    try {
                        bitmap = Utils.screenshot();
                        ByteArrayOutputStream bout = new ByteArrayOutputStream();
                        byte[] bytes ;

                        bitmap = bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bout);
                        bytes = bout.toByteArray();

                        //System.out.println("bitmap size : " + bytes.length);

                        writeInt(outputStream, bytes.length);
                        outputStream.write(bytes);
                        Thread.sleep(24);
                    } catch (Exception e) {
                        stopServer(socket);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
                try {
                    stopServer(socket);
                } catch (IOException e1) {
                    System.out.println("handlerCLient.stopserver "+ e.toString());
                }
            }
        }

    }
    private static void writeInt(OutputStream out,int v) throws IOException {
//        byte[] bt2 = new byte[4];
//        bt2[0] = (byte) ((ch >>> 24) & 0xFF);
//        bt2[1] = (byte) ((ch >>> 16) & 0xFF);
//        bt2[2] = (byte) ((ch >>> 8) & 0xFF);
//        bt2[3] = (byte) ((ch >> 0) & 0xFF);
//        out.write(bt2);
        out.write((v >>> 24) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>>  8) & 0xFF);
        out.write((v >>>  0) & 0xFF);
    }


    private static int readInt(InputStream in,Socket client) throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            stopServer(client);
        }
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    private static String readString(InputStream in,Socket client) throws IOException {
        byte[] a = new byte[1];

        in.read(a);
        return new String(a);
    }

    /**
     * 关闭socket
     * @param client
     * @throws IOException
     */
    public static void stopServer(Socket client) throws IOException {
        flag = false;
        if (!client.isClosed()) {
            client.shutdownInput();
            client.shutdownOutput();
            client.close();
            System.out.println("handlerCLient.stopserver ");
        }
    }



}

