package com.vegadvisor.client;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vegadvisor.client.bo.Fomhilfo;
import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.io.File;
import java.util.List;

public class RespuestaHiloActivity extends VegAdvisorActivity implements View.OnClickListener {

    // Encabezado campo respuesta
    private TextView respuestaHeader, hilo;

    // Campo respuesta
    private EditText respuestaContent;

    // Botón imágenes
    private ImageButton b2;

    // Hilo del foro
    private Fomhilfo hiloForo;

    // Layout de imágenes
    private LinearLayout imagenes;

   // Lista de imágenes a enviar
    private List<File> opinionImagesFiles;

    //Upload de imágenes
    private int totalUploadResponses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respuesta_hilo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Encabezado campo hilo y campo respuesta
        hilo = (TextView)findViewById(R.id.foro_hilo);
        respuestaHeader = (TextView)findViewById(R.id.foro_respuestaheader);
        //Respuesta
        respuestaContent = (EditText)findViewById(R.id.foro_respuesta);
        //Imágenes
        imagenes = (LinearLayout) findViewById(R.id.imagenes);
        //Boton imágenes
        b2 = (ImageButton)findViewById(R.id.b2);
        //Boton enviar respuesta y botón imagen ejecuta método onClick()
        findViewById(R.id.foro_btn_envRespuesta).setOnClickListener(this);
        b2.setOnClickListener(this);
    }

    /**
     * Actualiza la pantalla con el contenido del hilo especificado sobre él que se va a responder
     */
    @Override
    protected void onResume() {
        super.onResume();
        //Asigna hilo
        hiloForo = SessionData.getInstance().getForumThread();
        //Detalle del hilo
        hilo.setText(hiloForo.getHifdetaaf());
    }

    /* TODO
    @Override
    public void receiveServerCallResult(final int serviceId, final String service, final ReturnValidation result) {
        //Super
        super.receiveServerCallResult(serviceId, service, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        }
    }

    */

    /**
     * Recoge evento click sobre un botón y lleva a cabo una acción en función de cual haya sido
     * @param v, botón clicado
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.foro_btn_envRespuesta:
                saveReplicaHilo();
                break;
            case R.id.b2: //Botón de añadir imágenes
                //Lanza dialogo de selección de imagen
                this.launchSelectImageDialog();
                break;
        }
    }

    /**
     * Se engarga de procesar el resultado de cargar una imagen
     *
     * @param imageBitmap Bitmap de la imagen cargada
     * @param imagePath   Ruta de la imagen cargada
     */
    @Override
    public void processImageSelectedResponse(Bitmap imageBitmap, String imagePath) {
        //Crea nueva image view
        ImageView image = new ImageView(getApplicationContext());
        Bitmap scaled = Bitmap.createScaledBitmap(imageBitmap, b2.getWidth(), b2.getHeight(), true);
        //Asigna bitmap
        image.setImageBitmap(scaled);
        //Padding
        image.setPadding(5, 5, 5, 5);
        //Ingresa imagen al list view de imagenes
        imagenes.addView(image, 0);
        //Adiciona nuevo file para enviar al servidor
        opinionImagesFiles.add(new File(imagePath));
    }

    /**
     * Metodo para validar los datos de respuesta y registrarla en el sistema
     */
    private boolean saveReplicaHilo() {
        //Detalle Opinión
        if (Constants.BLANKS.equals(respuestaContent.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.detalle_respuesta).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Hilo
        hiloForo = SessionData.getInstance().getForumThread();
        //Envía actualización al servidor
       /* SessionData.getInstance().executeServiceRV(241,
                getResources().getString(R.string.opinion_registerUsersOpinion),
                this.createParametersMap(
                        "establishmentId", Constants.BLANKS + estab.getEstcestnk(),
                        "userId", SessionData.getInstance().getUserId(),
                        "stars", Constants.BLANKS + opinionStars,
                        "opinion", detalle_opinion.getText().toString().trim()));  */
        //Finaliza Ok
        return true;
    }

}
