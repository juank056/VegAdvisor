package com.vegadvisor.client;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vegadvisor.client.bo.Esdimaes;
import com.vegadvisor.client.bo.Esmestab;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

public class ConsultaEstabActivity extends VegAdvisorActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /**
     * Establecimiento consultado
     */
    private Esmestab estab;

    /**
     * Mapa
     */
    private GoogleMap googleMap;

    /**
     * Campos de texto a desplegar
     */
    private TextView nombreEstab, descripcion, direccion, telefono, horario;

    /**
     * Estrellas del establecimiento
     */
    private ImageView star1, star2, star3, star4, star5;

    /**
     * Imagenes
     */
    private ViewFlipper imagenes;

    /**
     * Detector
     */
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());

    /**
     * Datos del Swipe
     */
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;


    /**
     * @param savedInstanceState Instancia
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_estab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Establecimiento consultado
        estab = SessionData.getInstance().getUserEstab();
        //Campos de pantalla
        nombreEstab = (TextView) findViewById(R.id.nombreEstab);
        descripcion = (TextView) findViewById(R.id.descripcion);
        direccion = (TextView) findViewById(R.id.direccion);
        telefono = (TextView) findViewById(R.id.telefono);
        horario = (TextView) findViewById(R.id.horario);
        //Estrellas
        star1 = (ImageView) findViewById(R.id.star1);
        star2 = (ImageView) findViewById(R.id.star2);
        star3 = (ImageView) findViewById(R.id.star3);
        star4 = (ImageView) findViewById(R.id.star4);
        star5 = (ImageView) findViewById(R.id.star5);
        //Imagenes
        imagenes = (ViewFlipper) findViewById(R.id.imagenes);
        //Inicia view Flipper
        initViewFlipper();
        //Acciones de mapa
        mapActions();
        //Inicia google api client
        buildGoogleApiClient();
        //Inicia pantalla
        initScreen();
        //Objetos ok click
        telefono.setOnClickListener(this);
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
     * Inicia Pantalla
     */
    private void initScreen() {
        nombreEstab = (TextView) findViewById(R.id.nombreEstab);
        descripcion = (TextView) findViewById(R.id.descripcion);
        direccion = (TextView) findViewById(R.id.direccion);
        telefono = (TextView) findViewById(R.id.telefono);
        //Nombre establecimiento
        nombreEstab.setText(estab.getEstnestaf());
        //Descripción
        descripcion.setText(estab.getEstdestaf());
        //Dirección
        direccion.setText(estab.getEstdireaf());
        //Telefono
        telefono.setText(estab.getEstteleaf());
        //Horario
        String sHorario = estab.getEsthoratf() + " - " + estab.getEsthorctf();
        horario.setText(sHorario);
        //Sin visivilidad
        star1.setVisibility(View.GONE);
        star2.setVisibility(View.GONE);
        star3.setVisibility(View.GONE);
        star4.setVisibility(View.GONE);
        star5.setVisibility(View.GONE);
        if (estab.getEstpestnf() != 0) {/*Hay algo de estrellas*/
            //Obtiene parte fracion de las estrellas
            long iPart = (long) estab.getEstpestnf();
            double fPart = estab.getEstpestnf() - iPart;
            //Estrellas
            if (estab.getEstpestnf() > 0 && estab.getEstpestnf() < 1) {/*Entre cero y 1*/
                if (fPart > 0.1) {/*Hay escala*/
                    star1.getLayoutParams().width = (int) (star1.getLayoutParams().width * fPart);
                    star1.getLayoutParams().height = (int) (star1.getLayoutParams().height * fPart);
                    star1.setVisibility(View.VISIBLE);
                    star1.requestLayout();
                }
            } else if (estab.getEstpestnf() >= 1 && estab.getEstpestnf() < 2) {/*Entre 1 y 2*/
                //Visibilidad a estrellas
                star1.setVisibility(View.VISIBLE);
                if (fPart > 0.1) {/*Hay escala*/
                    star2.getLayoutParams().width = (int) (star2.getLayoutParams().width * fPart);
                    star2.getLayoutParams().height = (int) (star2.getLayoutParams().height * fPart);
                    star2.setVisibility(View.VISIBLE);
                    star2.requestLayout();
                }
            } else if (estab.getEstpestnf() >= 2 && estab.getEstpestnf() < 3) {/*Entre 2 y 3*/
                //Visibilidad a estrellas
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.VISIBLE);
                if (fPart > 0.1) {/*Hay escala*/
                    star3.getLayoutParams().width = (int) (star3.getLayoutParams().width * fPart);
                    star3.getLayoutParams().height = (int) (star3.getLayoutParams().height * fPart);
                    star3.setVisibility(View.VISIBLE);
                    star3.requestLayout();
                }
            } else if (estab.getEstpestnf() >= 3 && estab.getEstpestnf() < 4) {/*Entre 3 y 4*/
                //Visibilidad a estrellas
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.VISIBLE);
                star3.setVisibility(View.VISIBLE);
                if (fPart > 0.1) {/*Hay escala*/
                    star4.getLayoutParams().width = (int) (star4.getLayoutParams().width * fPart);
                    star4.getLayoutParams().height = (int) (star4.getLayoutParams().height * fPart);
                    star4.setVisibility(View.VISIBLE);
                    star4.requestLayout();
                }
            } else if (estab.getEstpestnf() >= 4 && estab.getEstpestnf() <= 5) {/*Entre 4 y 5*/
                //Visibilidad a estrellas
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.VISIBLE);
                star3.setVisibility(View.VISIBLE);
                star4.setVisibility(View.VISIBLE);
                if (fPart > 0.1) {/*Hay escala*/
                    star5.getLayoutParams().width = (int) (star5.getLayoutParams().width * fPart);
                    star5.getLayoutParams().height = (int) (star5.getLayoutParams().height * fPart);
                    star5.setVisibility(View.VISIBLE);
                    star5.requestLayout();
                }
            }
        }
        //Carga imagenes
        for (Esdimaes imaes : estab.getImages()) {
            //Envia petición para cargar imagen
            SessionData.getInstance().executeServiceImage(221,
                    getResources().getString(R.string.image_downloadImage),
                    ConsultaEstabActivity.this.createParametersMap("imagePath", imaes.getIesrimaaf()));
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.telefono:/*Numero de telefono*/
                //Intent para realizar llamada
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(Constants.TEL + telefono.getText().toString().trim()));
                //Intenta llamada
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                }
                break;
        }
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
                    imagenes.setInAnimation(AnimationUtils.loadAnimation(ConsultaEstabActivity.this.getApplicationContext()
                            , R.anim.left_in));
                    imagenes.setOutAnimation(AnimationUtils.loadAnimation(ConsultaEstabActivity.this.getApplicationContext(),
                            R.anim.left_out));
                    imagenes.showNext();
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    imagenes.setInAnimation(AnimationUtils.loadAnimation
                            (ConsultaEstabActivity.this.getApplicationContext(), R.anim.right_in));
                    imagenes.setOutAnimation(
                            AnimationUtils.loadAnimation(ConsultaEstabActivity.this.getApplicationContext(), R.anim.right_out));
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
        /*
      Fragmento del mapa
     */
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.ubicacion);
        //Obtiene mapa
        googleMap = mapFragment.getMap();
    }


    protected synchronized void buildGoogleApiClient() {
        /*
      Google api client
     */
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
        LatLng latLng = new LatLng(estab.getEstlatinf(), estab.getEstlongnf());
        //Posiciona el mapa segun la localizacion si ya esta
        if (googleMap != null) {
            //Adiciona marker al mapa
            googleMap.addMarker(new MarkerOptions().position(latLng).title(estab.getEstnestaf()).snippet(estab.getEstdestaf()));
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
