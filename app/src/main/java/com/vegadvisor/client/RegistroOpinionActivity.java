package com.vegadvisor.client;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vegadvisor.client.bo.Esmestab;
import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RegistroOpinionActivity extends VegAdvisorActivity implements View.OnClickListener {

    /**
     * Detalle de la opinion
     */
    private EditText detalle_opinion;

    /**
     * Establecimiento
     */
    private TextView establecimiento;

    /**
     * Estrellas del establecimiento
     */
    private ImageView star1, star2, star3, star4, star5;

    /**
     * Establecimiento
     */
    private Esmestab estab;

    /**
     * Boton Adicionar imagen
     */
    private ImageButton b2;

    /**
     * Estrellas de la opinion
     */
    private int opinionStars;

    /**
     * Layout de imagenes
     */
    private LinearLayout imagenes;

    /**
     * Lista de imágenes a enviar
     */
    private List<File> opinionImagesFiles;

    /**
     * Upload de imagenes
     */
    private int totalUploadResponses;

    /**
     * @param savedInstanceState Instancia
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_opinion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Lista de imagenes a enviar
        opinionImagesFiles = new ArrayList<>();
        //Upload en cero
        totalUploadResponses = 0;
        //Estrellas
        star1 = (ImageView) findViewById(R.id.star1);
        star2 = (ImageView) findViewById(R.id.star2);
        star3 = (ImageView) findViewById(R.id.star3);
        star4 = (ImageView) findViewById(R.id.star4);
        star5 = (ImageView) findViewById(R.id.star5);
        //Inicia en 1 estrella
        opinionStars = 1;
        //Establecimiento
        establecimiento = (TextView) findViewById(R.id.establecimiento);
        //Detalle Opinion
        detalle_opinion = (EditText) findViewById(R.id.detalle_opinion);
        //Adicionar imagen
        b2 = (ImageButton) findViewById(R.id.b2);
        //Imagenes
        imagenes = (LinearLayout) findViewById(R.id.imagenes);
        //Listeners para onclick
        findViewById(R.id.b1).setOnClickListener(this);
        star1.setOnClickListener(this);
        star2.setOnClickListener(this);
        star3.setOnClickListener(this);
        star4.setOnClickListener(this);
        star5.setOnClickListener(this);
        b2.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Asigna establecimiento
        estab = SessionData.getInstance().getUserEstab();
        //Establecimiento
        establecimiento.setText(estab.getEstnestaf());
        //Asigna estrellas
        setOpinionStars();
    }

    /**
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    @Override
    public void receiveServerCallResult(final int serviceId, final String service, final ReturnValidation result) {
        //Super
        super.receiveServerCallResult(serviceId, service, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Muestra mensaje recibido
                Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                //Revisa de acuerdo a lo ejecutado
                switch (serviceId) {
                    case 241: /*Creacion Registro de opinión*/
                        //Revisa si fue exitosa la actualización
                        if (Constants.ONE.equals(result.getValidationInd())) {/*Exitoso*/
                            //Revisa si no habían imagenes
                            if (opinionImagesFiles.size() == 0) {/*Sin imagenes*/
                                //Navega hacia la consulta de lestablecimiento de nuevo
                                Intent intent = new Intent(RegistroOpinionActivity.this, ConsultaEstabActivity.class);
                                //Navega
                                startActivity(intent);
                                //Finaliza Actividad
                                finish();
                            } else {/*Hay Imagenes*/
                                //Establecimiento
                                estab = SessionData.getInstance().getUserEstab();
                                //Fecha de registro de la opinion
                                String oesfregfk = result.getParams().get("oesfregfk");
                                //Secuencia asignada a la opinion
                                String oescoesnk = result.getParams().get("oescoesnk");
                                //Envía imágenes al servidor
                                for (File image : opinionImagesFiles) {
                                    SessionData.getInstance().executeServiceRV(242,
                                            getResources().getString(R.string.image_uploadOpinionImage),
                                            RegistroOpinionActivity.this.createParametersMap("establishmentId",
                                                    Constants.BLANKS + estab.getEstcestnk(),
                                                    "opinionDate", oesfregfk,
                                                    "opinionSecuence", oescoesnk),
                                            image);
                                }
                            }
                        }
                        break;
                    case 242: /*Respuesta de upload de imagen*/
                        //Incrementa contador
                        totalUploadResponses++;
                        if (totalUploadResponses == opinionImagesFiles.size()) {/*Todos*/
                            //Navega hacia la consulta de lestablecimiento de nuevo
                            Intent intent = new Intent(RegistroOpinionActivity.this, ConsultaEstabActivity.class);
                            //Navega
                            startActivity(intent);
                            //Finaliza Actividad
                            finish();
                        }
                }
            }
        });
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        //Revisa el caso
        switch (v.getId()) {
            case R.id.b1:/*Registar opinion*/
                //Guarda opinion
                saveOpinion();
                break;
            case R.id.b2:/*Adicionar una nueva imagen*/
                //Lanza dialogo de selección de imagen
                this.launchSelectImageDialog();
                break;
            case R.id.star1: /*1 Estrella*/
                opinionStars = 1;
                setOpinionStars();
                break;
            case R.id.star2: /*2 Estrella*/
                opinionStars = 2;
                setOpinionStars();
                break;
            case R.id.star3: /*3 Estrella*/
                opinionStars = 3;
                setOpinionStars();
                break;
            case R.id.star4: /*4 Estrella*/
                opinionStars = 4;
                setOpinionStars();
                break;
            case R.id.star5: /*5 Estrella*/
                opinionStars = 5;
                setOpinionStars();
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
     * Metodo para validar los datos de una opinion y registrarla en el sistema
     */
    private boolean saveOpinion() {
        //Detalle Opinión
        if (Constants.BLANKS.equals(detalle_opinion.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.detalle_opinion).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Establecimiento
        estab = SessionData.getInstance().getUserEstab();
        //Envía actualización al servidor
        SessionData.getInstance().executeServiceRV(241,
                getResources().getString(R.string.opinion_registerUsersOpinion),
                this.createParametersMap(
                        "establishmentId", Constants.BLANKS + estab.getEstcestnk(),
                        "userId", SessionData.getInstance().getUserId(),
                        "stars", Constants.BLANKS + opinionStars,
                        "opinion", detalle_opinion.getText().toString().trim()));
        //Finaliza Ok
        return true;
    }

    /**
     * Asigna bien las estrellas de acuerdo al numero
     */
    private void setOpinionStars() {
        //Revisa rango
        if (opinionStars < 1 || opinionStars > 5) {
            //Asigna 1 por defecto
            opinionStars = 1;
        }
        //Asigna a todas el opaco
        star1.setImageResource(R.drawable.star_opaque);
        star2.setImageResource(R.drawable.star_opaque);
        star3.setImageResource(R.drawable.star_opaque);
        star4.setImageResource(R.drawable.star_opaque);
        star5.setImageResource(R.drawable.star_opaque);
        switch (opinionStars) {
            case 1: /*1 Estrella*/
                star1.setImageResource(R.drawable.star);
                break;
            case 2: /*2 Estrellas*/
                star1.setImageResource(R.drawable.star);
                star2.setImageResource(R.drawable.star);
                break;
            case 3: /*3 Estrellas*/
                star1.setImageResource(R.drawable.star);
                star2.setImageResource(R.drawable.star);
                star3.setImageResource(R.drawable.star);
                break;
            case 4: /*4 Estrellas*/
                star1.setImageResource(R.drawable.star);
                star2.setImageResource(R.drawable.star);
                star3.setImageResource(R.drawable.star);
                star4.setImageResource(R.drawable.star);
                break;
            case 5: /*5 Estrellas*/
                star1.setImageResource(R.drawable.star);
                star2.setImageResource(R.drawable.star);
                star3.setImageResource(R.drawable.star);
                star4.setImageResource(R.drawable.star);
                star5.setImageResource(R.drawable.star);
                break;
        }
    }
}
