package com.vegadvisor.client.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.vegadvisor.client.bo.ReturnValidation;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Clase para conectarse con el servidor y ejecutar todos los servicios de red necesarios
 * Created by JuanCamilo on 11/11/2015.
 */
public class ServerConnector {

    /*************************************
     * ATRIBUTOS DEL SERVER CONNECTOR
     ************************************/

    /**
     * Ruta del servidor
     */
    private String server;

    /**
     * Indicador de uso cloud front
     */
    private boolean usingCloudFront;

    /**
     * Ruta de cloud front
     */
    private String cloudFrontPath;

    /**
     * Parseador de Json a objetos
     */
    private Gson gson = new Gson();

    /**
     * Constructor
     *
     * @param server Ruta de conexión con el servidor
     */
    public ServerConnector(String server, boolean usingCloudFront, String cloudFrontPath) {
        //Asigna atributos
        this.server = server;
        this.usingCloudFront = usingCloudFront;
        this.cloudFrontPath = cloudFrontPath;
        this.gson = new Gson();
        //Crea configuración del cliente
    }

    /**
     * Método para ejecutar un servicio en el servidor que retorna Return Validation
     *
     * @param service    Ruta del servicio a ejecutar
     * @param parameters Parámetros que se necesitan para ejecutar el servicio
     * @return Objeto T resultado de la ejecución del servicio
     */
    public ReturnValidation executeServiceRV(String service, Map<String, String> parameters) {
        Log.d(Constants.DEBUG, "Ejecutando Servicio: " + server + service);
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
            //Retorna objeto parseado
            return gson.fromJson(postMethod.getResponseBodyAsString(), ReturnValidation.class);
        } catch (Exception e) {/*Ocurrio un error*/
            e.printStackTrace();
            //Retorna null para que se trate el error en interfaz
            return null;
        }
    }

    /**
     * Método para ejecutar un servicio en el servidor que retorna una lista de objetos
     *
     * @param service    Ruta del servicio a ejecutar
     * @param parameters Parámetros que se necesitan para ejecutar el servicio
     * @return Lista resultado de la ejecución del servicio
     */
    public List<?> executeServiceList(String service, Map<String, String> parameters) {
        Log.d(Constants.DEBUG, "Ejecutando Servicio: " + server + service);
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
            //Retorna objeto parseado
            return gson.fromJson(postMethod.getResponseBodyAsString(), List.class);
        } catch (Exception e) {/*Ocurrio un error*/
            e.printStackTrace();
            //Retorna null para que se trate el error en interfaz
            return null;
        }
    }

    /**
     * Método para ejecutar un servicio en el servidor que retorna un input stream de una imagen
     *
     * @param service    Ruta del servicio a ejecutar
     * @param parameters Parámetros que se necesitan para ejecutar el servicio
     * @return Input Stream de la imagen obtenida
     */
    public Bitmap executeServiceImage(String service, Map<String, String> parameters) {
        Log.d(Constants.DEBUG, "Ejecutando Servicio: " + server + service);
        try {
            InputStream response;
            //Revisa si es cloud front o el servidor directamente
            if (usingCloudFront) {/*Cloud Front*/
                //Ruta de imagen
                String imageUrl = cloudFrontPath + parameters.get(Constants.IMAGE_KEY);
                //URL de llamado
                URL url = new URL(imageUrl);
                Log.d(Constants.DEBUG, "Obteniendo imagen de : " + imageUrl);
                //Conexion con el servidor
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //Retorna input stream
                response = connection.getInputStream();
            } else {/*Servidor normal*/
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
                //Retorna respuesta
                response = postMethod.getResponseBodyAsStream();
            }
            //Genera BitMap de la imagen
            return BitmapFactory.decodeStream(response);
        } catch (Exception e) {/*Ocurrio un error*/
            e.printStackTrace();
            //Retorna null para que se trate el error en interfaz
            return null;
        }
    }
}
