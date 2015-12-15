package com.vegadvisor.client;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.reflect.TypeToken;
import com.vegadvisor.client.bo.Cspciuda;
import com.vegadvisor.client.bo.Csptiest;
import com.vegadvisor.client.bo.Csptpais;
import com.vegadvisor.client.bo.Esdimaes;
import com.vegadvisor.client.bo.Esmestab;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.List;

public class DetalleEstabActivity extends VegAdvisorActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    /**
     * Mapa
     */
    private GoogleMap googleMap;

    /**
     * Fragmento del mapa
     */
    private MapFragment mapFragment;

    /**
     * Google api client
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Campos text view de la pantalla
     */
    private TextView nombreEstab, descripcion, direccion, telefono, apertura, cierre;

    /**
     * Campos de autocomplete
     */
    private TextView pais, ciudad, tipo_establecimiento;

    /**
     * Layout de imagenes
     */
    private LinearLayout imagenes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_estab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Obtiene elementos de la pantalla
        nombreEstab = (TextView) findViewById(R.id.nombreEstab);
        descripcion = (TextView) findViewById(R.id.descripcion);
        direccion = (TextView) findViewById(R.id.direccion);
        telefono = (TextView) findViewById(R.id.telefono);
        apertura = (TextView) findViewById(R.id.apertura);
        cierre = (TextView) findViewById(R.id.cierre);
        pais = (TextView) findViewById(R.id.pais);
        ciudad = (TextView) findViewById(R.id.ciudad);
        tipo_establecimiento = (TextView) findViewById(R.id.tipo_establecimiento);
        imagenes = (LinearLayout) findViewById(R.id.imagenes);
        //Acciones de mapa
        mapActions();
        //Inicia google api client
        buildGoogleApiClient();
        //Inicia Pantalla
        initScreen();
        //Boton de envio
        findViewById(R.id.b1).setOnClickListener(this);
        findViewById(R.id.b2).setOnClickListener(this);
    }


    /**
     * @param serviceId Id del servicio
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    @Override
    public void receiveServerCallResult(final int serviceId, final String service,
                                        final List<?> result) {
        //Super
        super.receiveServerCallResult(serviceId, service, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Revisa id de servicio ejecutado
                switch (serviceId) {
                    case 121: /*Llega nombre del pais*/
                        List<Csptpais> lsPais = (List<Csptpais>) result;
                        if (lsPais.size() > 0) {
                            //Asigna nombre al campo
                            pais.setText(lsPais.get(0).getPaidpaiaf());
                        }
                        break;
                    case 122: /*Llega nombre de ciudad*/
                        List<Cspciuda> lsCiudad = (List<Cspciuda>) result;
                        if (lsCiudad.size() > 0) {
                            //Asigna nombre al campo
                            ciudad.setText(lsCiudad.get(0).getCiunciuaf());
                        }
                        break;
                    case 123: /*Llega Tipo de establecimiento*/
                        List<Csptiest> lsTiest = (List<Csptiest>) result;
                        if (lsTiest.size() > 0) {
                            //Asigna nombre al campo
                            tipo_establecimiento.setText(lsTiest.get(0).getTesntesaf());
                        }
                        break;
                }

            }
        });
    }

    /**
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    @Override
    public void receiveServerCallResult(final int serviceId, final String service, final Bitmap result) {
        //Super
        super.receiveServerCallResult(serviceId, service, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Revisa que se tenga imagen
                if (result != null) {
                    //Revisa id de servicio ejecutado
                    switch (serviceId) {
                        case 124: /*Llega imagen*/
                            //Crea nueva image view
                            ImageView image = new ImageView(getApplicationContext());
                            Bitmap scaled = Bitmap.createScaledBitmap(result, 200, 200, true);
                            //Asigna bitmap
                            image.setImageBitmap(scaled);
                            //Padding
                            image.setPadding(5, 5, 5, 5);
                            //Ingresa imagen al list view de imagenes
                            imagenes.addView(image, 0);
                    }
                }
            }
        });
    }

    /**
     * Inicia elementos de pantalla
     */
    public void initScreen() {
        //Obtiene establecimiento
        Esmestab estab = SessionData.getInstance().getUserEstab();
        nombreEstab.setText(estab.getEstnestaf());
        descripcion.setText(estab.getEstdestaf());
        direccion.setText(estab.getEstdireaf());
        telefono.setText(estab.getEstteleaf());
        apertura.setText(estab.getEsthoratf());
        cierre.setText(estab.getEsthorctf());
        //Pais
        SessionData.getInstance().executeServiceList(121, getResources().getString(R.string.basic_getCountries),
                createParametersMap("countryCode", estab.getPaicpaiak()), new TypeToken<List<Csptpais>>() {
                }.getType());
        //Ciudad
        SessionData.getInstance().executeServiceList(122, getResources().getString(R.string.basic_getCities),
                createParametersMap("countryCode", estab.getPaicpaiak(), "cityCode", estab.getCiucciuak()),
                new TypeToken<List<Cspciuda>>() {
                }.getType());
        //Tipo establecimiento
        SessionData.getInstance().executeServiceList(123, getResources().getString(R.string.basic_getEstablishmentTypes),
                createParametersMap("establishmentTypeId", Constants.BLANKS + estab.getTesctesnk()), new TypeToken<List<Csptiest>>() {
                }.getType());
        //Solicita imagenes
        for (Esdimaes imaes : estab.getImages()) {
            //Solicita imagen
            SessionData.getInstance().executeServiceImage(124, getResources().getString(R.string.image_downloadImage),
                    createParametersMap("imagePath", imaes.getIesrimaaf()));
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
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.ubicacion);
        //Obtiene mapa
        googleMap = mapFragment.getMap();
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (mGoogleApiClient != null) {
            //Conecta
            mGoogleApiClient.connect();
        }
    }

    /**
     * Cuando se conecta con el Api de google para mapas
     *
     * @param bundle Bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        //Establecimiento
        Esmestab estab = SessionData.getInstance().getUserEstab();
        //Posiciona el mapa segun la ubicacion del establecimiento
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(estab.getEstlatinf(), estab.getEstlongnf()), 15.0f));
            //Adiciona nuevo marker
            googleMap.addMarker(new MarkerOptions().
                    position(new LatLng(estab.getEstlatinf(), estab.getEstlongnf())).
                    title(estab.getEstnestaf()).snippet(estab.getEstdestaf()));
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

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.b1: /*Editar establecimiento*/
                //Navega hacia editar establecimiento
                intent = new Intent(DetalleEstabActivity.this, EditarEstabActivity.class);
                startActivity(intent);
                break;
            case R.id.b2: /*Consultar estadisticas*/
                //Navega hacia estadisticas establecimiento
                intent = new Intent(DetalleEstabActivity.this, EstadisticasEstabActivity.class);
                startActivity(intent);
                break;
        }
    }
}
