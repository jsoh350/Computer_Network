package com.example.sw;

import android.graphics.Color;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpClient {

    public static Socket socket;
    final static String address = "140.143.126.73";
    final static int port = 2020;

    public static void startClient(final TextView status){
        if(address==null){
            return;
        }
        if(socket==null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        socket = new Socket(address,port);
                        status.setText("Socket Connected!");
                        status.setTextColor(Color.parseColor("#9DCC6B"));

                        PrintWriter pw = new PrintWriter(socket.getOutputStream());
                        InputStream inputStream = socket.getInputStream();
                        byte[] buffer = new byte[1024];
                        int len = -1;

                        while ((len = inputStream.read(buffer)) != -1){
                            String data = new String(buffer, 0, len);
                            EventBus.getDefault().post(new ShowResponse(data));
                        }

                        pw.close();


                    } catch (Exception EE) {
                        EE.printStackTrace();
                        status.setText("Something Wrong!");
                        status.setTextColor(Color.parseColor("#E84034"));
                    }finally {
                        status.setText("Socket Break Off!");
                        status.setTextColor(Color.parseColor("#E84034"));
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        socket = null;
                    }
                }
            }).start();
        }
    }

    public static void SendMessage(String message){
        System.out.println(message);
        if (socket != null && socket.isConnected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(message);
                        socket.getOutputStream().write(message.getBytes());
                        socket.getOutputStream().flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
