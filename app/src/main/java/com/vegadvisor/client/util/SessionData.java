package com.vegadvisor.client.util;

import android.util.Log;

import com.vegadvisor.client.bo.ReturnValidation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     * Contraseña del usuario
     */
    private String userPasswd;

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
     * Constructor privado
     */
    private SessionData() {
        //Hay usuario false
        this.user = false;
        //Inicia thread executor
        threadExecutor = Executors.newFixedThreadPool(MAX_THREADS);
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
     * @param serverPath ruta del servidor
     */
    public void initConnectors(String serverPath) {
        //Conector
        this.serverConnector = new ServerConnector(serverPath);
    }

    /**
     * Método para ejecutar un servicio en el servidor que retorna Return Validation
     *
     * @param service    Ruta del servicio a ejecutar
     * @param parameters Parámetros que se necesitan para ejecutar el servicio
     */
    public void executeServiceRV(String service, Map<String, String> parameters) {
        //Ejecuta llamada
        threadExecutor.execute(new ConnectorExecutorService(1, service, parameters));
    }

    /**
     * Método para ejecutar un servicio en el servidor que retorna una lista de objetos
     *
     * @param service    Ruta del servicio a ejecutar
     * @param parameters Parámetros que se necesitan para ejecutar el servicio
     */
    public void executeServiceList(String service, Map<String, String> parameters) {
        //Ejecuta llamada
        threadExecutor.execute(new ConnectorExecutorService(2, service, parameters));
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
     * Obtiene la contraseña del usuario en sesion
     *
     * @return Contraseña del usuario en sesion
     */
    public String getUserPasswd() {
        return userPasswd;
    }

    /**
     * Asigna contraseña del usuario en sesión
     *
     * @param userPasswd Contraseña del usuario en sesión
     */
    public void setUserPasswd(String userPasswd) {
        this.userPasswd = userPasswd;
    }

    /*************************
     * GETTERS DE CONNECTORES
     *************************/

    /**
     * Obtiene conector para ReturnValidation
     *
     * @return Conector para métodos que retornan ReturnValidation
     */
    public ServerConnector getServerConnector() {
        return serverConnector;
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
     * Clase interna para ejecutar los llamados a los servicios del servidor
     */
    private class ConnectorExecutorService implements Runnable {

        /**
         * Servicio a ejecutar
         */
        private String service;

        /**
         * Parámetros de ejecución
         */
        private Map<String, String> parameters;

        /**
         * Tipo de servicio
         * 1. ReturnValidation
         * 2. List
         */
        private int serviceType;

        /**
         * Constructor del thread para ejecutar los servicios con el servidor
         *
         * @param serviceType Tipo de servicio a llamar
         * @param service     Ruta del servicio
         * @param parameters  Parámetros de ejecución del servicio
         */
        public ConnectorExecutorService(int serviceType, String service, Map<String, String> parameters) {
            //Asigna parámetros
            this.serviceType = serviceType;
            this.service = service;
            this.parameters = parameters;
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
                    activity.receiveServerCallResult(service, responseRV);
                    break;
                case 2: /*Lista*/
                    //Ejecuta servicio del server connector
                    List<?> responseList = serverConnector.executeServiceList(service, parameters);
                    //Notifica a la actividad para que haga algo con la respuesta
                    activity.receiveServerCallResult(service, responseList);
                    break;
            }
            Log.d(Constants.DEBUG, "Finaliza ejecución servicio: " + service);
        }
    }
}
