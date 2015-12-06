package com.vegadvisor.client;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vegadvisor.client.bo.Chdmensa;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Servidor
     */
    private String server;

    /**
     * Socket
     */
    private Socket socket;

    /**
     * Shutdown
     */
    private boolean shutdown;

    /**
     * Usuario en sesion
     */
    private String userId;

    /**
     * Data Output Stream del socket
     */
    private DataOutputStream outputStream;

    /**
     * Output router
     */
    private OutputRouter outputRouter;

    /**
     * Parseador de Json a objetos
     */
    private Gson gson = new Gson();

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
        userId = SessionData.getInstance().getUserId();
        //Por ahora hace toast
        Toast.makeText(getApplicationContext(), "INICIA SERVICIO CHAT: " + userId, Toast.LENGTH_SHORT).show();
        //Dirección Ip
        ipAddress = getResources().getString(R.string.chat_server_ip);
        //Puerto
        port = Integer.valueOf(getResources().getString(R.string.chat_server_port));
        //Servidor
        server = getResources().getString(R.string.server_path);
        //Parseador de Json
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
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
        //Retorna START STICKY
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
        //Mensaje recibido
        Log.d(Constants.DEBUG, "Mensaje recibido de: " + userIdFrom);
        //Crea notificacion
        Toast.makeText(getApplicationContext(), "Mensaje recibido de: " + userIdFrom, Toast.LENGTH_SHORT).show();
        //Revisa mensajes de chat
        checkMessages();
    }

    /**
     * Revisa mensajes de chat en el servidor
     */
    private void checkMessages() {
        //Mapa de parametros
        Map<String, String> params = new HashMap<>();
        //Ingresa parametros
        params.put("userId", userId);
        //Obtiene mensajes
        @SuppressWarnings("unchecked")
        List<Chdmensa> messages = (List<Chdmensa>) this.executeServiceList(getResources().getString(R.string.chat_recolectChatMessages),
                params, new TypeToken<List<Chdmensa>>() {
                }.getType());
        if (messages != null) {/*Llego objeto*/
            //Recorre mensajes para ingresarlos a la base de datos
            for (Chdmensa mensa : messages) {
                SessionData.getInstance().getDatabaseHandler().saveMessage(userId,
                        mensa.getId().getUsucusuak(), mensa.getMchmensaf(), Constants.ONE, mensa.getSenderName());
            }
        }
    }

    /**
     * Método para ejecutar un servicio en el servidor que retorna una lista de objetos
     *
     * @param service    Ruta del servicio a ejecutar
     * @param parameters Parámetros que se necesitan para ejecutar el servicio
     * @param classType  Tipo de objeto que se va a retornar
     * @return Lista resultado de la ejecución del servicio
     */
    private List<?> executeServiceList(String service, Map<String, String> parameters, Type classType) {
        try {
            //Cliente http
            HttpClient httpClient = new HttpClient();
            //Metodo post
            PostMethod postMethod = new PostMethod(server + service);
            //Asigna parámetros
            for (String key : parameters.keySet()) {
                postMethod.addParameter(key, parameters.get(key));
            }
            //Ejecuta llamado
            httpClient.executeMethod(postMethod);
            //Obtiene InputStream de respuesta
            InputStream stream = postMethod.getResponseBodyAsStream();
            //Convierte response a String
            String s_response = IOUtils.toString(stream);
            //Cierra stream
            stream.close();
            //Retorna objeto parseado
            return gson.fromJson(s_response, classType);
        } catch (Exception e) {/*Ocurrio un error*/
            e.printStackTrace();
            //Retorna null para que se trate el error en interfaz
            return null;
        }
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
                //Revisa mensajes de chat
                checkMessages();
                //Crea socket
                socket = new Socket(ipAddress, port);
                // Input y output stream del socket
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
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



