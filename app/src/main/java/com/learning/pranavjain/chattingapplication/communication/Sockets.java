package com.learning.pranavjain.chattingapplication.communication;

import android.util.Log;

import com.learning.pranavjain.chattingapplication.interfacer.Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Pranav Jain on 11/2/2016.
 */

public class Sockets implements Socket {

    private static final String AUTHENTICATION_SERVER_ADDRESS = "http://127.0.0.1/chattingapplication";
    private int listeningPort = 0;
    private static final String HTTP_REQUEST_FAILED = null;
    private HashMap<InetAddress, Socket> sockets = new HashMap<InetAddress, Socket>();
    private ServerSocket serverSocket = null;
    private boolean listening;

    private class ReceiveConnection extends Thread{

        java.net.Socket clientSocket = null;

        public ReceiveConnection(Socket socket){
            this.clientSocket = (java.net.Socket) socket;
            Sockets.this.sockets.put(((ServerSocket) socket).getInetAddress(),socket);
        }

        @Override
        public void run() {

            try{

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null){
                    if(!(inputLine.equals("exit"))){

                    }else{
                        clientSocket.shutdownInput();
                        clientSocket.shutdownOutput();
                        clientSocket.close();
                        Sockets.this.sockets.remove(clientSocket.getInetAddress());
                    }
                }

            }catch (Exception e){
                Log.e("ERROR in connection","");
                e.printStackTrace();
            }

            super.run();
        }
    }

    @Override
    public String sendHTTPRequest(String Params) {

        URL url;
        String result = "";
        try {

            url = new URL(AUTHENTICATION_SERVER_ADDRESS);
            HttpURLConnection httpURLConnection;
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);

            PrintWriter out = new PrintWriter(httpURLConnection.getOutputStream());

            out.println(Params);
            out.close();

            BufferedReader in =  new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;

            while((inputLine = in.readLine()) != null){
                result = result.concat(inputLine);
            }
            in.close();

        }catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(result.length() == 0){
            result = HTTP_REQUEST_FAILED;
        }

        return result;
    }

    @Override
    public int startListeningPort(int Port) {
        return 0;
    }

    @Override
    public void stopListening() {

    }

    @Override
    public void exit() {

    }
}
