package com.vegadvisor.client;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vegadvisor.client.bo.Evdimaev;
import com.vegadvisor.client.bo.Evmevent;
import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.DateUtils;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

public class DetalleEventoActivity extends VegAdvisorActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //Mapa
    private GoogleMap googleMap;

    //Campos TextView de la pantalla
    private TextView tipo_evento, descripcion, localizacion, fecha_evento, hora_evento, numParticip;

    /**
     * Imagenes
     */
    private ViewFlipper imagenes;

    /**
     * Detector
     */
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());

    /**
     * Botones
     */
    private Button b1, b2;

    /**
     * Evento a consultar
     */
    private Evmevent evento;

    /**
     * Indicador de usuario participando
     */
    private boolean userParticipating;

    /**
     * @param savedInstanceState Instancia
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_evento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Imagenes
        imagenes = (ViewFlipper) findViewById(R.id.imagenes);
        //Botones
        b1 = (Button) findViewById(R.id.b1);
        b2 = (Button) findViewById(R.id.b2);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        //Evento a consultar
        evento = SessionData.getInstance().getEvent();
        //Campos de la pantalla
        tipo_evento = (TextView) findViewById(R.id.tipo_evento);
        descripcion = (TextView) findViewById(R.id.descripcion);
        localizacion = (TextView) findViewById(R.id.localizacion);
        fecha_evento = (TextView) findViewById(R.id.fecha_evento);
        hora_evento = (TextView) findViewById(R.id.hora_evento);
        numParticip = (TextView) findViewById(R.id.numParticip);
        //Inicia view Flipper
        initViewFlipper();
        //Acciones de mapa
        mapActions();
        //Inicia google api client
        buildGoogleApiClient();
        //Inicia pantalla
        initScreen();
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
                    case 342: /*Indicador de participacion*/
                        //Revisa si fue exitosa la actualización
                        if (Constants.ONE.equals(result.getValidationInd())) {/*Exitoso*/
                            //Cambia indicador de participacion
                            userParticipating = !userParticipating;
                            //Numero de participantes
                            int particip = Integer.valueOf(numParticip.getText().toString());
                            //Actualiza boton
                            //Revisa si esta participando
                            if (userParticipating) {
                                //Incrementa participantes
                                particip++;
                                //Asigna texto de dejar de asistir
                                b1.setText(R.string.no_asistire);
                                b1.setTextColor(Color.RED);
                            } else {
                                //Decrementa participantes
                                particip--;
                                //Asigna texto de Asistir
                                b1.setText(R.string.asistire);
                                b1.setTextColor(Color.rgb(0, 153, 76));
                            }
                            //Actualiza participantes
                            numParticip.setText(Constants.BLANKS + particip);
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void receiveServerCallResult(final int serviceId, final String service, final Bitmap result) {
        //Super
        super.receiveServerCallResult(serviceId, service, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Revisa que se tenga imagen
                if (result != null) {
                    //Crea nueva image view
                    ImageView image = new ImageView(getApplicationContext());
                    //Asigna bitmap
                    image.setImageBitmap(result);
                    //Ingresa imagen al list view de imagenes
                    imagenes.addView(image, 0);
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
        Intent intent;
        switch (v.getId()) {
            case R.id.b1: /*Asistir a evento*/
                //Envia actualizacion de asistir
                //Envía actualización al servidor
                SessionData.getInstance().executeServiceRV(342,
                        getResources().getString(R.string.event_registerUsersEventParticipation),
                        this.createParametersMap(
                                "countryCode", evento.getId().getPaicpaiak(),
                                "cityCode", evento.getId().getCiucciuak(),
                                "eventDate", DateUtils.getDateStringYYYYMMDD(evento.getId().getEvefevefk()),
                                "eventSec", Constants.BLANKS + evento.getId().getEvecevenk(),
                                "userId", SessionData.getInstance().getUserId(),
                                "participationInd", userParticipating ? Constants.ZERO : Constants.TWO
                        ));
                break;
            case R.id.b2: /*Editar evento*/
                //Navega hacia la edicion de Evento
                intent = new Intent(this, EditarEventoActivity.class);
                //Navega
                startActivity(intent);
                break;
        }
    }

    /**
     * Inicia Pantalla
     */
    private void initScreen() {
        //Tipo Evento
        tipo_evento.setText(evento.getEventTypeName());
        //Descripción
        descripcion.setText(evento.getEvedeveaf());
        //Localizacion
        localizacion.setText(evento.getEstloceaf());
        //Fecha Evento
        fecha_evento.setText(DateUtils.getDateString(evento.getId().getEvefevefk()));
        //Hora evento
        hora_evento.setText(evento.getEvehoratf());
        //Participantes
        numParticip.setText(Constants.BLANKS + evento.getEvenparnf());
        //Carga imagenes
        for (Evdimaev imaev : evento.getImages()) {
            //Envia petición para cargar imagen
            SessionData.getInstance().executeServiceImage(341,
                    getResources().getString(R.string.image_downloadImage),
                    DetalleEventoActivity.this.createParametersMap("imagePath", imaev.getImerimaaf()));
        }
        //Revisa si el usuario es el dueño del evento
        if (!evento.getUsucusuak().equals(SessionData.getInstance().getUserId())) {
            b2.setVisibility(View.GONE);
        }
        //Indicador de usuario participando
        userParticipating = Constants.TWO.equals(evento.getUserParticipating());
        //Revisa si esta participando
        if (userParticipating) {
            //Asigna texto de dejar de asistir
            b1.setText(R.string.no_asistire);
            b1.setTextColor(Color.RED);
        } else {
            //Asigna texto de Asistir
            b1.setText(R.string.asistire);
            b1.setTextColor(Color.rgb(0, 153, 76));
        }
        //Si no hay usuario en sesion esconde boton de asistir
        if (!SessionData.getInstance().isUser()) {
            b1.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Si el usuario esta participando, se indica a no participar

    }

    /**************************************
     * PARA EL VIEW FLIPPER
     *************************************/

    /**
     * Inicia view Flipper
     */
    private void initViewFlipper() {
        imagenes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });
    }

    /**
     * Detector de gestos
     */
    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    imagenes.setInAnimation(AnimationUtils.loadAnimation(DetalleEventoActivity.this.getApplicationContext()
                            , R.anim.left_in));
                    imagenes.setOutAnimation(AnimationUtils.loadAnimation(DetalleEventoActivity.this.getApplicationContext(),
                            R.anim.left_out));
                    imagenes.showNext();
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    imagenes.setInAnimation(AnimationUtils.loadAnimation
                            (DetalleEventoActivity.this.getApplicationContext(), R.anim.right_in));
                    imagenes.setOutAnimation(
                            AnimationUtils.loadAnimation(DetalleEventoActivity.this.getApplicationContext(), R.anim.right_out));
                    imagenes.showPrevious();
                    return true;
                }
            } catch (Exception e) {/*Error*/
                e.printStackTrace();
            }

            return false;
        }
    }

    /***************************************
     * PARA OBTENER LOCALIZACION CON EL GPS
     **************************************/

    /**
     * Acciones del mapa
     */
    private void mapActions() {
        //Obtiene fragmento del mapa
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.ubicacion);
        //Obtiene mapa
        googleMap = mapFragment.getMap();
    }


    protected synchronized void buildGoogleApiClient() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //Conecta
        mGoogleApiClient.connect();
    }

    /**
     * Cuando se conecta con el Api de google para mapas
     *
     * @param bundle Bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        //Long lat
        LatLng latLng = new LatLng(evento.getEvelatinf(), evento.getEvelongnf());
        //Posiciona el mapa segun la localizacion si ya esta
        if (googleMap != null) {
            //Adiciona marker al mapa
            googleMap.addMarker(new MarkerOptions().position(latLng).title(evento.getEventTypeName()).snippet(evento.getEvedeveaf()));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        /*Nada*/
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*Nada*/
    }

}