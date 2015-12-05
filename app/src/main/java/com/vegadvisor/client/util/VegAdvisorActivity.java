package com.vegadvisor.client.util;


import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.vegadvisor.client.R;
import com.vegadvisor.client.bo.ReturnValidation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que representa una actividad de VegAdvisor
 * Created by JuanCamilo on 12/11/2015.
 */
public abstract class VegAdvisorActivity extends AppCompatActivity {

    /**
     * Dialogo de progreso
     */
    private ProgressDialog loadingDialog;

    /**
     * Indicador de show loading icon
     */
    private boolean showLoadingIcon;

    /**
     * Datos del Swipe
     */
    protected static final int SWIPE_MIN_DISTANCE = 50;
    protected static final int SWIPE_THRESHOLD_VELOCITY = 100;

    /**
     * Sobre-escribe onCreate
     *
     * @param savedInstanceState Instancia
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setMessage(getResources().getString(R.string.espere));
        loadingDialog.setIndeterminate(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        //Show loading true
        showLoadingIcon = true;
        //Super
        super.onCreate(savedInstanceState);
    }

    /**
     * Sobre-escribe método on resume
     */
    @Override
    protected void onResume() {
        super.onResume();
        //Asigna actividad a los datos de sesión
        SessionData.getInstance().setActivity(this);
    }


    protected void showConfirmDialog(final int dialogId, final String title,
                                     final String message, final String positive, final String negative) {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                executeConfirmDialogAction(dialogId, true);
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                executeConfirmDialogAction(dialogId, false);
            }
        });
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
    }

    /**
     * Ejecuta acción al confirmar un dialogo
     *
     * @param dialogId Id del dialogo de confirmación
     * @param positive Si la respuesta fue positiva o no
     */
    protected void executeConfirmDialogAction(int dialogId, boolean positive) {
        Toast.makeText(getApplicationContext(), "Response: " + dialogId + " was: " + positive, Toast.LENGTH_SHORT).show();
    }

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    public void receiveServerCallResult(final int serviceId, final String service, final ReturnValidation result) {
        //Esconde icono de cargando
        this.hideLoadingIcon();
    }

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    public void receiveServerCallResult(final int serviceId, final String service, final List<?> result) {
        //Esconde icono de cargando
        this.hideLoadingIcon();
    }

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    public void receiveServerCallResult(final int serviceId, final String service, final Object result) {
        //Esconde icono de cargando
        this.hideLoadingIcon();
    }

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    public void receiveServerCallResult(final int serviceId, final String service, final Bitmap result) {
        //Esconde icono de cargando
        this.hideLoadingIcon();
    }

    /**
     * Muestra icono de cargando
     */
    public void showLoadingIcon() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Revisa si estaba mostrando
                if (isShowLoadingIcon())
                    loadingDialog.show();
            }
        });

    }

    /**
     * Esconde icono de cargando
     */
    public void hideLoadingIcon() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Revisa si estaba mostrando
                if (isShowLoadingIcon())
                    loadingDialog.hide();
            }
        });

    }

    /**
     * Asigna indicador de mostrar icono de cargando
     *
     * @param showLoadingIcon Indicador de mostrar icono de cargando
     */
    public void setShowLoadingIcon(boolean showLoadingIcon) {
        this.showLoadingIcon = showLoadingIcon;
    }

    /**
     * Obtiene indicador de mostrar icono de cargando
     *
     * @return Indicador de mostrar icono de cargando
     */
    public boolean isShowLoadingIcon() {
        return showLoadingIcon;
    }

    /**
     * Lanza Dialogo para seleccionar una imagen
     */
    protected void launchSelectImageDialog() {
        //Items del menu
        final CharSequence[] items = {getResources().getString(R.string.tomar_foto),
                getResources().getString(R.string.seleccionar_galeria),
                getResources().getString(R.string.cancelar)};
        //Crea dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Titulo del dialogo
        builder.setTitle(getResources().getString(R.string.seleccionar_imagen));
        //Asigna Items con el Listener
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //Intent que se va a realizar
                Intent intent;
                //Revisa caso seleccionado
                switch (item) {
                    case 0:/*Tomar Foto*/
                        //Intent para tomar la foto
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, Constants.REQUEST_CAMERA);
                        }
                        break;
                    case 1: /*Seleccionar de Galería*/
                        //Intent para seleccionar de la galería
                        intent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(
                                Intent.createChooser(intent, getResources().getString(R.string.seleccionar_imagen)),
                                Constants.SELECT_FILE);
                        break;
                    case 2: /*Cancelar*/
                        //Cancela dialogo
                        dialog.dismiss();
                        break;
                }
            }
        });
        //Construye builder
        builder.show();
    }

    /**
     * Llamado cuando se ha ejecutado una acción solicitada por la actividad
     *
     * @param requestCode Codigo de request enviado
     * @param resultCode  Codigo de resultado obtenido
     * @param data        Datos de respuesta
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {/*Revisa que el resultado sea ok*/
            switch (requestCode) {
                case Constants.REQUEST_CAMERA:/*Camara*/
                    //Obtiene datos extra
                    Bundle extras = data.getExtras();
                    //Obtiene bitmap de la imagen
                    Bitmap bmImage = (Bitmap) extras.get("data");
                    //Guarda imagen en internal storage
                    String path = null;
                    try {
                        path = this.saveToInternalSorage(bmImage);
                    } catch (IOException e) {/*Error guardando el archivo*/
                        e.printStackTrace();
                    }
                    //Procesa evento de imagen seleccionada
                    this.processImageSelectedResponse(bmImage, path);
                    break;
                case Constants.SELECT_FILE: /*Seleccionar un archivo*/
                    //Obtiene URI de la imagen
                    Uri selectedImageUri = data.getData();
                    //Obtiene path de la imagen
                    String tempPath = getPath(selectedImageUri, this);
                    //Bitmap de la imagen
                    Bitmap bmImageS = BitmapFactory.decodeFile(tempPath);
                    //Procesa evento de imagen seleccionada
                    this.processImageSelectedResponse(bmImageS, tempPath);
                    break;
            }
        }
    }

    /**
     * Obtiene el Path de una URI dad
     *
     * @param uri      URI dada
     * @param activity Actividad
     * @return Path de un archivo
     */
    private String getPath(Uri uri, VegAdvisorActivity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * Guarda una imagen en el almacenamiento interno del telefono
     *
     * @param bitmapImage Imagen a guardar
     * @return Ruta de la imagen guardada
     */
    private String saveToInternalSorage(Bitmap bitmapImage) throws IOException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // Directorio de la imagen
        File directory = cw.getDir(Constants.IMAGE_DIR,
                Context.MODE_PRIVATE);
        // Imagen a guardar
        File image = new File(directory, String.valueOf(System.currentTimeMillis()) + Constants.JPG_EXT);
        //Output stream
        FileOutputStream fos;
        //Asigna imagen
        fos = new FileOutputStream(image);
        //Guarda imagen (JPG , de fotos)
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        //Cierra output stream
        fos.close();
        //Retorna con el path completo donde ha quedado la imagen
        return image.getAbsolutePath();
    }

    /**
     * Se engarga de procesar el resultado de cargar una imagen
     *
     * @param imageBitmap Bitmap de la imagen cargada
     * @param imagePath   Ruta de la imagen cargada
     */
    public void processImageSelectedResponse(Bitmap imageBitmap, String imagePath) {
        Log.d(Constants.DEBUG, "Image Path: " + imagePath);
    }

    /**
     * Se encarga de crear mapa de parametros para enviar al servidor
     *
     * @param values Valores de los parámetros (Se reciben por parejas)
     * @return Mapa de parámetros creado
     */
    protected Map<String, String> createParametersMap(String... values) {
        //Crea mapa de parámetros
        Map<String, String> parameters = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            //Ingresa nuevo valor al mapa
            parameters.put(values[i], values[i + 1]);
            //incrementa i en 1
            i++;
        }
        //Retorna parámetros
        return parameters;
    }


    /**
     * Revisa si un servicio esta en ejecución
     *
     * @param serviceClass Clase del servicio
     * @return True si el servicio esta en ejecución, false de lo contrario
     */
    protected boolean checkForRunningService(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
