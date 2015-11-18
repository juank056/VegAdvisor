package com.vegadvisor.client.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.vegadvisor.client.bo.ReturnValidation;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
            //Obtiene InputStream de respuesta
            InputStream stream = postMethod.getResponseBodyAsStream();
            //Convierte response a String
            String s_response = IOUtils.toString(stream);
            //Cierra stream
            stream.close();
            //Retorna objeto parseado
            return gson.fromJson(s_response, List.class);
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
     * @param service     Ruta del servicio a ejecutar
     * @param parameters  Parámetros que se necesitan para ejecutar el servicio
     * @param imageBitMap Archivo de la imagen a subir al servidor
     * @return Objeto T resultado de la ejecución del servicio
     */
    public ReturnValidation executeServiceRV(String service, Map<String, String> parameters, Bitmap imageBitMap) {
        Log.d(Constants.DEBUG, "Ejecutando Servicio: " + server + service);
        try {
            //Http Client
            CloseableHttpClient httpclient = HttpClients.createDefault();
            //Método Post
            HttpPost httppost = new HttpPost(server + service);
            //Persiste Imagen
            String imagePath = persistImage(imageBitMap);
            //File Body
            FileBody image = new FileBody(new File(imagePath));
            //Builder para parametros
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            //Imagen
            builder.addPart(Constants.IMAGE, image);
            //Asigna parámetros adicionales
            for (String key : parameters.keySet()) {
                builder.addPart(key, new StringBody(parameters.get(key), ContentType.TEXT_PLAIN));
            }
            //Construye entity de parámetros
            HttpEntity reqEntity = builder.build();
            //Asigna Entity a método Post
            httppost.setEntity(reqEntity);
            //Ejecuta
            CloseableHttpResponse response = httpclient.execute(httppost);
            //Entity de respuesta
            HttpEntity resEntity = response.getEntity();
            //Obtiene InputStream de respuesta
            InputStream stream = resEntity.getContent();
            //Convierte response a String
            String s_response = IOUtils.toString(stream);
            //Cierra stream
            stream.close();
            //Cierra response
            response.close();
            //Cierra cliente
            httpclient.close();
            //Elimina imagen que se creo para enviar
            new File(imagePath).delete();
            //Parsea respuesta y retorna
            //Retorna objeto parseado
            return gson.fromJson(s_response, ReturnValidation.class);
        } catch (Exception e) {/*Ocurrio error*/
            e.printStackTrace();
            //Retorna null para que se trate el error en interfaz
            return null;
        }
    }

    /**
     * Guarda imagen en almacenamiento del teléfono
     *
     * @param bitmap Bitmap de la imagen a guardar
     */
    private String persistImage(Bitmap bitmap) {
        try {
            //Obtiene fecha actual
            Date current = new Date();
            Random random = new Random();
            //Genera Path de la imagen
            String path = Constants.BLANKS + current.getTime() + random.nextLong();
            //Crea file
            File imageFile = new File(path);
            //Output stream
            OutputStream os = new FileOutputStream(imageFile);
            //Comprime imagen
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            //Flush y termina
            os.flush();
            os.close();
            //Retorna path de la imagen
            return path;
        } catch (Exception e) {/*ocurrio un error*/
            e.printStackTrace();
            //retorna null
            return null;
        }
    }
}
