package com.vegadvisor.client;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.vegadvisor.client.util.SessionData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatService extends Service {

    /**
     * Direccion ip
     */
    private String ipAddress;

    /**
     * Puerto
     */
    private int port;

    /**
     * Socket
     */
    private Socket socket;

    /**
     * Shutdown
     */
    private boolean shutdown;

    /**
     * Data Input Stream del socket
     */
    private DataInputStream inputStream;

    /**
     * Data Output Stream del socket
     */
    private DataOutputStream outputStream;

    /**
     * Output router
     */
    private OutputRouter outputRouter;

    /**
     * Constructor
     */
    public ChatService() {
    }


    /**
     * Inicia Servicio de chat
     *
     * @param intent  Intent
     * @param flags   Flags del servicio
     * @param startId Id de start
     * @return Service Start Sticky
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Obtiene id del usuario
        String userId = SessionData.getInstance().getUserId();
        //Por ahora hace toast
        Toast.makeText(getApplicationContext(), "INICIA SERVICIO CHAT: " + userId, Toast.LENGTH_SHORT).show();
        //Dirección Ip
        ipAddress = getResources().getString(R.string.chat_server_ip);
        //Puerto
        port = Integer.valueOf(getResources().getString(R.string.chat_server_port));
        //Shutdown false
        shutdown = false;
        try {
            //Escribe el nombre del usuario para que llegue al servidor
            SessionData.getInstance().getMessages().offer(userId);
            //Inicia input router y output router
            InputRouter inputRouter = new InputRouter();
            outputRouter = new OutputRouter();
            //Inicia Input router
            new Thread(inputRouter).start();
        } catch (Exception e) {/*Error */
            e.printStackTrace();
        }
        //Retorna el id recibid
        return Service.START_STICKY;
    }

    /**
     * Finaliza servicio Chat
     */
    @Override
    public void onDestroy() {
        //Por ahora hace toast
        Toast.makeText(getApplicationContext(), "FINALIZA SERVICIO CHAT.....", Toast.LENGTH_SHORT).show();
        //Shutdown a true
        shutdown = true;
        //Cierra socket
        if (socket != null) {
            if (socket.isConnected()) {
                try {
                    socket.close();
                } catch (IOException e) {/*Error cerrando socket*/
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Bind
     *
     * @param intent intent
     * @return No esta implementado
     */
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("No se puede hacer Bind de este servicio");
    }

    /**
     * Notifica que se ha recibido un nuevo mensaje de chat
     *
     * @param userIdFrom Usuario que ha enviado el mensaje
     */
    private void notifyNewMessage(String userIdFrom) {
        //Por ahora hace toast
        Toast.makeText(getApplicationContext(), "Mensaje recibido de: " + userIdFrom, Toast.LENGTH_SHORT).show();
    }

    /**
     * Clase privada de Input Router
     */
    private class InputRouter implements Runnable {


        /**
         * Ejecuta Input router
         */
        @Override
        public void run() {
            try {
                //Crea socket
                socket = new Socket(ipAddress, port);
                // Input y output stream del socket
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());
                //Inicia output router
                new Thread(outputRouter).start();
                // Ejecuta mientras no se finalice el servicio
                while (!shutdown) {
                    try {
                        // Lee longitud del id del usuario
                        int length = inputStream.readInt();
                        // Crea buffer del tamaño
                        byte[] buffer = new byte[length];
                        // Lee bytes del nombre de usuario
                        int readed = inputStream.read(buffer);
                        if (readed > 0) {
                            // Nombre de usuario
                            String userIdFrom = new String(buffer);
                            // Notifica
                            notifyNewMessage(userIdFrom);
                        }
                    } catch (Exception e) {/*Error*/
                        if (!shutdown)
                            e.printStackTrace();
                    }
                }
            } catch (Exception e) {/*Error */
                e.printStackTrace();
            }
        }
    }

    /**
     * Clase privada de Output Router
     */
    private class OutputRouter implements Runnable {

        /**
         * Ejecuta Input router
         */
        @Override
        public void run() {
            // Ejecuta mientras no se finalice el servicio
            while (!shutdown) {
                try {
                    // Obtiene mensaje a enviar
                    String message = SessionData.getInstance().getMessages().take();
                    // Escribe longitud del mensaje
                    outputStream.writeInt(message.length());
                    // Escribe mensaje
                    outputStream.write(message.getBytes());
                } catch (Exception e) {/*Error*/
                    if (!shutdown)
                        e.printStackTrace();
                }
            }
        }
    }

}



