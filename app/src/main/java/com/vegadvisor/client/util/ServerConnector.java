package com.vegadvisor.client.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vegadvisor.client.bo.ReturnValidation;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
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
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
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
            //Parsea respuesta y retorna
            //Retorna objeto parseado
            return gson.fromJson(s_response, ReturnValidation.class);
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
     * @param classType  Tipo de objeto que se va a retornar
     * @return Lista resultado de la ejecución del servicio
     */
    public List<?> executeServiceList(String service, Map<String, String> parameters, Type classType) {
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
     * Método para ejecutar un servicio en el servidor que retorna una lista de objetos
     *
     * @param service    Ruta del servicio a ejecutar
     * @param parameters Parámetros que se necesitan para ejecutar el servicio
     * @param classType  Tipo de objeto que se va a retornar
     * @return Objeto retornado en la consulta
     */
    public Object executeServiceObject(String service, Map<String, String> parameters, Type classType) {
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
     * Método para ejecutar un servicio en el servidor que retorna un input stream de una imagen
     *
     * @param service    Ruta del servicio a ejecutar
     * @param parameters Parámetros que se necesitan para ejecutar el servicio
     * @return Input Stream de la imagen obtenida
     */
    public Bitmap executeServiceImage(String service, Map<String, String> parameters) {
        try {
            //Duerme un poco para no saturar server
            Thread.sleep(50);
            InputStream response;
            //Revisa si es cloud front o el servidor directamente
            if (usingCloudFront) {/*Cloud Front*/
                //Ruta de imagen
                String imageUrl = cloudFrontPath + parameters.get(Constants.IMAGE_KEY);
                //URL de llamado
                URL url = new URL(imageUrl);
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
            //Bitmap de respuesta
            Bitmap b_response = BitmapFactory.decodeStream(response);
            //Cierra stream
            response.close();
            //Retorna bitmap
            return b_response;
        } catch (Exception e) {/*Ocurrio un error*/
            e.printStackTrace();
            //Retorna null para que se trate el error en interfaz
            return null;
        }
    }

    /**
     * Método para ejecutar un servicio en el servidor que retorna Return Validation
     * Especial para subir imagenes al servidor
     *
     * @param service    Ruta del servicio a ejecutar
     * @param parameters Parámetros que se necesitan para ejecutar el servicio
     * @param imageFile  Archivo de la imagen a subir al servidor
     * @return Objeto T resultado de la ejecución del servicio
     */
    public ReturnValidation executeServiceRV(String service, Map<String, String> parameters, File imageFile) {
        try {
            //Duerme un poco para no saturar server
            Thread.sleep(50);
            //Crea cliente
            org.apache.http.client.HttpClient client = new DefaultHttpClient();
            //Http Post
            HttpPost post = new HttpPost(server + service);
            //Entity
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            //Adiciona imagen
            entity.addPart(Constants.IMAGE, new FileBody(imageFile));
            //Parámetros adicionales
            for (String key : parameters.keySet()) {
                entity.addPart(key, new StringBody(parameters.get(key)));
            }
            //Post
            post.setEntity(entity);
            //Ejecuta
            HttpResponse response = client.execute(post);
            //Obtiene InputStream de respuesta
            InputStream stream = response.getEntity().getContent();
            //Convierte response a String
            String s_response = IOUtils.toString(stream);
            //Cierra stream
            stream.close();
            return gson.fromJson(s_response, ReturnValidation.class);
        } catch (Exception e) {/*Ocurrio error*/
            e.printStackTrace();
            //Retorna null para que se trate el error en interfaz
            return null;
        }
    }
}
