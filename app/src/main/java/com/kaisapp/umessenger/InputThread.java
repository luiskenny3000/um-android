package com.kaisapp.umessenger;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by kenny on 5/2/17.
 */

public class InputThread extends Thread {
    private Context context;
    private Socket socket;
    private MessageListener listener;

    public InputThread(Context context, Socket socket, MessageListener listener) {
        this.context = context;
        this.socket = socket;
        this.listener = listener;
    }

    public void run() {
        try {
            BufferedReader bufer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String mensaje;
            while ((mensaje = bufer.readLine()) != null) {
                listener.addMessage(mensaje);
            }
        } catch (IOException ex) {
            Toast.makeText(context, "Falló recepción de mensaje.", Toast.LENGTH_LONG).show();
        }
    }

    public interface MessageListener{
        void addMessage(String message);
    }
}
