package com.webclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 111 on 2018/4/11.
 */

public class ClientManager {
    private static Map<String,Socket> clientlist = new HashMap<>();
    private static ServerThread serverThread = null;

    private static class ServerThread implements Runnable {
        private int port = 32100;
        private boolean isEXit = false;
        private ServerSocket server;

        public ServerThread(){
            try{
                server = new ServerSocket(port);
                System.out.println("Service on "+port+"starting successfully");
            } catch (IOException e){
                System.out.println("Service started failed");
            }
        }

        @Override
        public void run(){
            try{
                while(!isEXit){
                    System.out.println("Waiting.......");
                    final Socket socket = server.accept();
                    final String address = socket.getRemoteSocketAddress().toString();
                    System.out.println("Connect to "+address+" 's android phone");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                synchronized (this){
                                    clientlist.put(address,socket);
                                }
                                InputStream inputStream = socket.getInputStream();
                                byte[] buffer = new byte[1024];
                                int len;
                                while ((len=inputStream.read(buffer))!=-1){
                                    String text = new String(buffer,0,len);
                                    sendMsg(text);
                                }
                            }catch (Exception e){
                                System.out.println(e.getMessage());
                            }finally {
                                synchronized (this){
                                    System.out.println("Connect slose to "+address+" 's android phone");
                                    clientlist.remove(address);
                                }
                            }
                        }
                    }).start();

                }
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }

        public void Stop(){
            isEXit = true;
            if(server != null){
                try{
                    server.close();
                    System.out.println("Server closed");
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static ServerThread startServer(){
        System.out.println("Start Service");
        if (serverThread!=null){
            shutServer();
        }
        serverThread = new ServerThread();
        new Thread(serverThread).start();
        System.out.println("Start Service successfully");
        return serverThread;
    }

    public static void shutServer(){
        for(Socket socket : clientlist.values()){
            try{
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        serverThread.Stop();
        clientlist.clear();
    }

    public static boolean sendMsg(String text){
        try{
            for(Socket socket : clientlist.values()) {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(text.getBytes("utf-8"));
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
