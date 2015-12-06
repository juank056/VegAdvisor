package com.vegadvisor.client.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.vegadvisor.client.bo.Esdopies;
import com.vegadvisor.client.bo.Esmestab;
import com.vegadvisor.client.bo.Evmevent;
import com.vegadvisor.client.bo.Fomhilfo;
import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.bo.Usmusuar;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Clase que contendra los datos de la sesión de la aplicación.
 * Servirá también para el envío de parámetros entre una actividad y otra.
 * Se tendrán apuntadores a los elementos que pueda requerir una actividad
 * Created by JuanCamilo on 11/11/2015.
 */
public class SessionData {

    /**
     * Instancia de singleton
     */
    private static SessionData instance;

    /******************************************
     * ATRIBUTOS DE LA SESIÓN DE LA APLICACIÓN
     *****************************************/

    /**
     * Actividad en ejecución
     */
    private VegAdvisorActivity activity;

    /**
     * Indicador de si hay o no usuario
     */
    private boolean user;

    /**
     * Id del usuario
     */
    private String userId;


    /**
     * Objeto del usuario
     */
    private Usmusuar usuarObject;

    /**
     * Establecimiento de usuario
     */
    private Esmestab userEstab;

    /**
     * Registro de Opinion
     */
    private Esdopies opinion;

    /**
     * Registro de Evento
     */
    private Evmevent event;

    /**
     * Registro de hilo de foro
     */
    private Fomhilfo forumThread;

    /**
     * Conector para ejecutar métodos en el servidor
     */
    private ServerConnector serverConnector;

    /**
     * Maximo de threads de llamadas
     */
    private static final int MAX_THREADS = 10;


    /**
     * Servicio ejecutor
     */
    private ExecutorService threadExecutor;

    /**
     * Cola de mensajes de chat
     */
    private BlockingQueue<String> messages;

    /**
     * Intent de creacion de servicio chat
     */
    private Intent chatServiceIntent;

    /**
     * Chat peer (Contiene id usuario y su nombre)
     */
    private String[] chatPeer;

    /**
     * Indicador de servicio de chat iniciado
     */
    private boolean chatServiceStarted;

    /**
     * Manejador base de datos chat
     */
    private ChatDatabaseHandler databaseHandler;


    /**
     * Constructor privado
     */
    private SessionData() {
        //Hay usuario false
        this.user = false;
        //Inicia thread executor
        threadExecutor = Executors.newFixedThreadPool(MAX_THREADS);
        // Inicia cola de mensajes
        this.messages = new LinkedBlockingQueue<>();
        //Chat service no iniciado
        chatServiceStarted = false;

    }

    /**
     * Obtiene instancia de la sesion
     *
     * @return Instancia de la sesión de datos de la aplicación
     */
    public static SessionData getInstance() {
        if (instance == null)
            instance = new SessionData();
        return instance;
    }

    /**
     * Inicia conectores
     *
     * @param usingCloudFront Indicador de si se usa cloud front o no para manejo de imagenes
     * @param serverPath      ruta del servidor
     * @param cloudFrontPath  Ruta del servidor cloud front
     * @param context         Contexto de ejecucion
     */
    public void initConnectors(String serverPath, boolean usingCloudFront,
                               String cloudFrontPath, Context context) {
        //Conector
        this.serverConnector = new ServerConnector(serverPath, usingCloudFront, cloudFrontPath);
        //Inicia database handler
        databaseHandler = new ChatDatabaseHandler(context);
    }

    /**
     * Método para ejecutar un servicio en el servidor que retorna Return Validation
     *
     * @param serviceId  Id del servicio a ejecutar
     * @param service    Ruta del servicio a ejecutar
     * @param parameters Parámetros que se necesitan para ejecutar el servicio
     */
    public void executeServiceRV(int serviceId, String service, Map<String, String> parameters) {
        //Cargando
        this.activity.showLoadingIcon();
        //Ejecuta llamada
        threadExecutor.execute(new ConnectorExecutorService(1, serviceId, service, parameters, null, null));
    }

    /**
     * Método para ejecutar un servicio en el servidor que retorna una lista de objetos
     *
     * @param serviceId  Id del servicio a ejecutar
     * @param service    Ruta del servicio a ejecutar
     * @param parameters Parámetros que se necesitan para ejecutar el servicio
     * @param classType  Clase esperada en retorno
     */
    public void executeServiceList(int serviceId, String service, Map<String, String> parameters, Type classType) {
        //Cargando
        this.activity.showLoadingIcon();
        //Ejecuta llamada
        threadExecutor.execute(new ConnectorExecutorService(2, serviceId, service, parameters, null,
                classType));
    }

    /**
     * Método para ejecutar un servicio en el servidor que retorna una imagen
     *
     * @param serviceId  Id del servicio a ejecutar
     * @param service    Ruta del servicio a ejecutar
     * @param parameters Parámetros que se necesitan para ejecutar el servicio
     */
    public void executeServiceImage(int serviceId, String service, Map<String, String> parameters) {
        //Cargando
        this.activity.showLoadingIcon();
        //Ejecuta llamada
        threadExecutor.execute(new ConnectorExecutorService(3, serviceId, service, parameters, null, null));
    }

    /**
     * Método para ejecutar un servicio en el servidor que retorna Return Validation
     *
     * @param serviceId  Id del servicio a ejecutar
     * @param service    Ruta del servicio a ejecutar
     * @param parameters Parámetros que se necesitan para ejecutar el servicio
     * @param imageFile  Bitmap de imagen a enviar al servidor
     */
    public void executeServiceRV(int serviceId, String service, Map<String, String> parameters, File imageFile) {
        //Cargando
        this.activity.showLoadingIcon();
        //Ejecuta llamada
        threadExecutor.execute(new ConnectorExecutorService(4, serviceId, service, parameters, imageFile, null));
    }

    /**
     * Método para ejecutar un servicio en el servidor que retorna un objeto específico
     *
     * @param serviceId  Id del servicio a ejecutar
     * @param service    Ruta del servicio a ejecutar
     * @param parameters Parámetros que se necesitan para ejecutar el servicio
     * @param classType  Clase esperada en retorno
     */
    public void executeServiceObject(int serviceId, String service, Map<String, String> parameters, Type classType) {
        //Cargando
        this.activity.showLoadingIcon();
        //Ejecuta llamada
        threadExecutor.execute(new ConnectorExecutorService(5, serviceId, service, parameters, null,
                classType));
    }

    /**
     * Obtiene indicador de si hay usuario o no en sesion
     *
     * @return Indicador de usuario en sesion
     */
    public boolean isUser() {
        return user;
    }

    /**
     * Asigna indicador de usuario en sesion
     *
     * @param user Indicador de usuario en sesion
     */
    public void setUser(boolean user) {
        this.user = user;
    }

    /**
     * Obtiene el id del usuario en sesion
     *
     * @return Id del usuario en sesion
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Asigna id del usuario en sesion
     *
     * @param userId Id del usuario
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }


    /**
     * Obtiene objeto del usuario
     *
     * @return Objeto del usuario
     */
    public Usmusuar getUsuarObject() {
        return usuarObject;
    }

    /**
     * Asigna objeto del usuarii
     *
     * @param usuarObject Usuario del objeto a asignar
     */
    public void setUsuarObject(Usmusuar usuarObject) {
        this.usuarObject = usuarObject;
    }

    /**
     * Obtiene el establecimiento de un usuario
     *
     * @return Establecimiento de un usuario para ver/editar
     */
    public Esmestab getUserEstab() {
        return userEstab;
    }

    /**
     * Asigna establecimiento de usuario
     *
     * @param userEstab Asigna el establecimiento de un usuario
     */
    public void setUserEstab(Esmestab userEstab) {
        this.userEstab = userEstab;
    }

    /**
     * Obtiene hilo de foro de sesion
     *
     * @return Hilo de foro de sesion
     */
    public Fomhilfo getForumThread() {
        return forumThread;
    }

    /**
     * Asigna hilo de foro de sesion
     *
     * @param forumThread Hilo de foro de sesion
     */
    public void setForumThread(Fomhilfo forumThread) {
        this.forumThread = forumThread;
    }

    /**
     * Obtiene evento de sesion
     *
     * @return Evento de sesión
     */
    public Evmevent getEvent() {
        return event;
    }

    /**
     * Asigna evento de sesion
     *
     * @param event Evento de sesion
     */
    public void setEvent(Evmevent event) {
        this.event = event;
    }

    /**
     * Obtiene opinion de sesion
     *
     * @return Opinion de sesion
     */
    public Esdopies getOpinion() {
        return opinion;
    }

    /**
     * Asigna opinion de sesion
     *
     * @param opinion Opinion de sesion
     */
    public void setOpinion(Esdopies opinion) {
        this.opinion = opinion;
    }

    /**
     * Obtiene cola de mensajes de chat
     *
     * @return Cola de mensajes de chat
     */
    public BlockingQueue<String> getMessages() {
        return messages;
    }

    /**
     * Obtiene intent de servicio de chat
     *
     * @return Intent de servicio de chat
     */
    public Intent getChatServiceIntent() {
        return chatServiceIntent;
    }

    /**
     * Asigna intent de servicio de chat
     *
     * @param chatServiceIntent intent de servicio de chat
     */
    public void setChatServiceIntent(Intent chatServiceIntent) {
        this.chatServiceIntent = chatServiceIntent;
    }

    /**
     * Obtiene char peer
     *
     * @return Chat peer
     */
    public String[] getChatPeer() {
        return chatPeer;
    }

    /**
     * Asigna chat peer
     *
     * @param chatPeer Chat peer a asignar
     */
    public void setChatPeer(String[] chatPeer) {
        this.chatPeer = chatPeer;
    }

    /**
     * Obtiene si el servicio de chat ha sido iniciado
     *
     * @return Indicador de servicio de chat iniciado
     */
    public boolean isChatServiceStarted() {
        return chatServiceStarted;
    }

    /**
     * Asigna indicador de servicio de chat iniciado
     *
     * @param chatServiceStarted Indicador de servicio de chat iniciado
     */
    public void setChatServiceStarted(boolean chatServiceStarted) {
        this.chatServiceStarted = chatServiceStarted;
    }

    /**
     * Asigna la actividad que se está ejecutando en pantalla
     *
     * @param activity Actividad que ejecuta en pantalla
     */
    public void setActivity(VegAdvisorActivity activity) {
        this.activity = activity;
    }

    /**
     * Obtiene database handler
     *
     * @return Handler de base de datos
     */
    public ChatDatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }

    /**
     * Limpia datos de la sesion
     */
    public void cleanData() {
        this.user = false;
        this.userId = null;
        this.usuarObject = null;
        this.event = null;
        this.userEstab = null;
        this.forumThread = null;
    }

    /**
     * Clase interna para ejecutar los llamados a los servicios del servidor
     */
    private class ConnectorExecutorService implements Runnable {

        /**
         * Id del servicio
         */
        private int serviceId;

        /**
         * Servicio a ejecutar
         */
        private String service;

        /**
         * Parámetros de ejecución
         */
        private Map<String, String> parameters;

        /**
         * Bitmap de imagen
         */
        private File imageFile;

        /**
         * Tipo de servicio
         * 1. ReturnValidation
         * 2. List
         */
        private int serviceType;

        /**
         * Clase de retorno
         */
        private Type classType;

        /**
         * Constructor del thread para ejecutar los servicios con el servidor
         *
         * @param serviceId   Id del servicio
         * @param serviceType Tipo de servicio a llamar
         * @param service     Ruta del servicio
         * @param parameters  Parámetros de ejecución del servicio
         */
        public ConnectorExecutorService(int serviceType, int serviceId, String service,
                                        Map<String, String> parameters, File imageFile, Type classType) {
            //Asigna parámetros
            this.serviceId = serviceId;
            this.serviceType = serviceType;
            this.service = service;
            this.parameters = parameters;
            this.imageFile = imageFile;
            this.classType = classType;
        }

        /**
         * Ejecuta Servicio
         */
        @Override
        public void run() {
            Log.d(Constants.DEBUG, "Inicia ejecución servicio: " + service);
            switch (this.serviceType) {
                case 1: /*Return validation*/
                    //Ejecuta servicio del server connector
                    ReturnValidation responseRV = serverConnector.executeServiceRV(service, parameters);
                    //Notifica a la actividad para que haga algo con la respuesta
                    activity.receiveServerCallResult(serviceId, service, responseRV);
                    break;
                case 2: /*Lista*/
                    //Ejecuta servicio del server connector
                    List<?> responseList = serverConnector.executeServiceList(service, parameters, classType);
                    //Notifica a la actividad para que haga algo con la respuesta
                    activity.receiveServerCallResult(serviceId, service, responseList);
                    break;
                case 3: /*Imagen*/
                    //Ejecuta servicio del server connector
                    Bitmap responseImage = serverConnector.executeServiceImage(service, parameters);
                    //Notifica a la actividad para que haga algo con la respuesta
                    activity.receiveServerCallResult(serviceId, service, responseImage);
                    break;
                case 4: /*Envío de imagen*/
                    //Ejecuta servicio del server connector
                    ReturnValidation responseRVImage = serverConnector.executeServiceRV(service, parameters, imageFile);
                    //Notifica a la actividad para que haga algo con la respuesta
                    activity.receiveServerCallResult(serviceId, service, responseRVImage);
                    break;
                case 5: /*Objeto específico*/
                    //Ejecuta servicio del server connector
                    Object responseObject = serverConnector.executeServiceObject(service, parameters, classType);
                    //Notifica a la actividad para que haga algo con la respuesta
                    activity.receiveServerCallResult(serviceId, service, responseObject);
                    break;
            }
            Log.d(Constants.DEBUG, "Finaliza ejecución servicio: " + service);
        }
    }
}
