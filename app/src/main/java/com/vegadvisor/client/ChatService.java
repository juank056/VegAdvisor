package com.vegadvisor.client;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
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
import java.util.ArrayList;
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
        //Obtiene id del usuario de las perferencias
        //Obtiene shared Preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Obtiene nombre de usuario
        userId = sharedPref.getString(Constants.USERID_PREFERENCE, Constants.BLANKS);
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
            //Termina servicio para que se reinicie
            restartService();
        }
        //Retorna START STICKY
        return Service.START_STICKY;
    }

    /**
     * Finaliza servicio Chat
     */
    @Override
    public void onDestroy() {
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
        Log.d(Constants.DEBUG, "MENSAJE RECIBIDO DE : " + userIdFrom);
        //Revisa mensajes de chat
        List<Chdmensa> messages = checkMessages();
        //Datos de notificación
        String title = null, content = null;
        //Numero de mensajes de diferentes personas
        int people = 0;
        //Lista de ids de usuario
        List<String> users = new ArrayList<>();
        //Revisa mensajes
        if (messages != null && messages.size() > 0) {/*Hay mensajes*/
            //Recorre lista
            for (Chdmensa mensa : messages) {
                //Revisa si ya estaba el usuario
                if (!users.contains(mensa.getId().getUsucusuak())) {
                    //Incrementa personas
                    people++;
                    //Ingresa persona
                    users.add(mensa.getId().getUsucusuak());
                }
            }
            if (messages.size() == 1) {/*Un solo mensaje de una persona*/
                //Titulo
                title = getResources().getString(R.string.nuevo_mensaje).
                        replace(Constants.URL_PARAM01, messages.get(0).getSenderName());
                //Contenido
                content = messages.get(0).getMchmensaf();
            } else if (people == 1) {/*Mas de un mensaje (1 sola persoan)*/
                //Titulo
                title = getResources().getString(R.string.nuevos_mensajes_one).
                        replace(Constants.URL_PARAM02, messages.get(0).getSenderName()).
                        replace(Constants.URL_PARAM01, Constants.BLANKS + messages.size());
                content = messages.get(messages.size() - 1).getMchmensaf();
            } else {/*Mas de un mensaje, mas de una persona*/
                //Titulo
                title = getResources().getString(R.string.nuevos_mensajes).
                        replace(Constants.URL_PARAM02, Constants.BLANKS + people).
                        replace(Constants.URL_PARAM01, Constants.BLANKS + messages.size());
                content = title;
            }
        }
        //Revisa si se esta en la pantalla de Conversación
        if (SessionData.getInstance().getActivity() != null) {
            if (SessionData.getInstance().getActivity() instanceof ConversacionActivity) {
                ConversacionActivity activity = (ConversacionActivity) SessionData.getInstance().getActivity();
                //Revisa ahora si la persona con la que esta hablando es la que envio mensaje
                String userOther = SessionData.getInstance().getChatPeer()[0];
                if (userIdFrom.equals(userOther)) {
                    //Refresca conversacion
                    activity.setConversation(SessionData.getInstance().getDatabaseHandler().
                            getMessages(userId, userIdFrom, Constants.MAX_MESSAGES));
                    activity.refreshMessages();
                    if (people > 1) {/*Hay otra conversación*/
                        //Genera notificación
                        buildNotification(userIdFrom, messages.get(0).getSenderName(), people, title, content);
                    }
                } else {/*Esta conversando con otra persona*/
                    //Genera notificación
                    buildNotification(userIdFrom, messages.get(0).getSenderName(), people, title, content);
                }
            } else {/*Esta en otra actividad*/
                //Genera notificación
                buildNotification(userIdFrom, messages.get(0).getSenderName(), people, title, content);
            }
        } else {/*No hay actividad*/
            //Genera notificación
            buildNotification(userIdFrom, messages.get(0).getSenderName(), people, title, content);
        }
    }

    /**
     * Construye notificacion en el sistema
     *
     * @param userIdFrom Usuario que ha enviado mensaje
     * @param userName   Nombre del usuario de la conversacion
     * @param people     Numero de personas que envian mensajes
     * @param title      Titulo de la notificacion
     * @param content    Contenido de la notificación
     */
    private void buildNotification(String userIdFrom, String userName, int people, String title, String content) {
        //Actividad que resolvera el intent (iniciada en chat activity por defecto)
        Class<?> resolverActivity = ChatActivity.class;
        //Si el numero de personas es una entonces se inicia en conversacion
        if (people == 1) {
            resolverActivity = ConversacionActivity.class;
        }
        //Builder
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_notity)
                .setContentTitle(title)
                .setContentText(content);
        // Crea intent
        Intent resultIntent = new Intent(this, resolverActivity);
        //Datos extras para el intent
        resultIntent.putExtra(Constants.USER_ID, userId);
        resultIntent.putExtra(Constants.USER_CONVERSATION, userIdFrom);
        resultIntent.putExtra(Constants.USER_NAME, userName);
        // Stack builder
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(resolverActivity);
        //Siguiente intent
        stackBuilder.addNextIntent(resultIntent);
        //Intent pending
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        //Obtiene servicio de notificaciones
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //Crea notificacion finalmente
        mNotificationManager.notify(Constants.NOTIFICATION_ID, mBuilder.build());
    }

    /**
     * Revisa mensajes de chat en el servidor
     */
    private List<Chdmensa> checkMessages() {
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
        //Retorna mensajes
        return messages;
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
                        if (!shutdown) {
                            e.printStackTrace();
                            //Termina servicio para que se reinicie
                            restartService();
                        }
                    }
                }
            } catch (Exception e) {/*Error */
                e.printStackTrace();
                //Termina servicio para que se reinicie
                restartService();
            }
        }
    }

    /**
     * Reinicia servicio
     */
    private void restartService() {
        stopSelf();
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
                    Log.d(Constants.DEBUG, "NOTIFICANDO ENVIO A: " + message);
                    // Escribe longitud del mensaje
                    outputStream.writeInt(message.length());
                    // Escribe mensaje
                    outputStream.write(message.getBytes());
                } catch (Exception e) {/*Error*/
                    if (!shutdown) {
                        e.printStackTrace();
                        //Termina servicio para que se reinicie
                        restartService();
                    }
                }
            }
        }
    }

}



