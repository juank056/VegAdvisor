package com.vegadvisor.client;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.reflect.TypeToken;
import com.vegadvisor.client.bo.Esmestab;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.List;

public class CercanoaMiActivity extends VegAdvisorActivity implements
        View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /**
     * Lista de objetos de establecimiento
     */
    private List<Esmestab> lsEstab;

    /**
     * Mapa
     */
    private GoogleMap googleMap;

    /**
     * Google api client
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Latitud y longitud
     */
    private double latitud, longitud;

    /**
     * Radios de búsqueda
     */
    private static final double[] ratios = new double[]{0.1, 0.5, 1, 5, 10, 15, 20, 50, 100};

    /**
     * Selected search ratio
     */
    private int selectedRatio;

    /**
     * Radio seleccionado
     */
    private TextView radio_busqueda;

    /**
     * Establecimiento seleccionado
     */
    private TextView selectedEstab;

    /**
     * @param savedInstanceState Instancia
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cercanoa_mi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Selected estab
        selectedEstab = (TextView) findViewById(R.id.selectedEstab);
        //Selected Ratio cero por defecto
        selectedRatio = 0;
        //Asigna texto de selected ratio
        radio_busqueda = (TextView) findViewById(R.id.radio_busqueda);
        String selectedRatioText = getResources().getString(R.string.radio_busqueda)
                + Constants.TWO_POINTS + Constants.BLANK_SPACE + ratios[selectedRatio] + Constants.KM;
        radio_busqueda.setText(selectedRatioText);
        //Acciones de mapa
        mapActions();
        //Inicia google api client
        buildGoogleApiClient();
        //Botones de pantalla
        findViewById(R.id.b1).setOnClickListener(this);
        findViewById(R.id.b2).setOnClickListener(this);
        selectedEstab.setOnClickListener(this);
    }

    /**
     * Busca establecimientos
     */
    private void searchEstablishments() {
        //Obtiene establecimientos
        SessionData.getInstance().executeServiceList(181,
                getResources().getString(R.string.establishment_findEstablishments),
                this.createParametersMap("clue", Constants.BLANKS,
                        "ratio", Constants.BLANKS + ratios[selectedRatio],
                        "latitud", Constants.BLANKS + latitud,
                        "longitud", Constants.BLANKS + longitud), new TypeToken<List<Esmestab>>() {
                }.getType());
    }

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    public void receiveServerCallResult(final int serviceId, final String service, final List<?> result) {
        //Super
        super.receiveServerCallResult(serviceId, service, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Revisa que haya lista de resultado
                if (result != null) {
                    //Lista de establecimientos
                    lsEstab = (List<Esmestab>) result;
                    //Limpia mapa
                    googleMap.clear();
                    //Adiciona Marcadores en el Mapa
                    for (Esmestab estab : lsEstab) {
                        googleMap.addMarker(new MarkerOptions().
                                position(new LatLng(estab.getEstlatinf(), estab.getEstlongnf())).
                                title(estab.getEstnestaf()).snippet(estab.getEstdestaf()));
                    }
                }
            }
        });
    }

    /*******************************
     * GPS
     * ===============================
     */

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
        //Listener de markes al mapa
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Busca establecimiento seleccionado
                for (Esmestab estab : lsEstab) {
                    //Revisa si le coinciden latitud y longitud
                    if (Double.compare(estab.getEstlatinf(), marker.getPosition().latitude) == 0 &&
                            Double.compare(estab.getEstlongnf(), marker.getPosition().longitude) == 0) {
                        //Asina establecimiento a datos de sesion
                        SessionData.getInstance().setUserEstab(estab);
                        //Texto de establecimiento seleccionado
                        selectedEstab.setText(estab.getEstnestaf());
                    }
                }
                return false;/*Evento consumido*/
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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
        //Obtiene localizacion
        Location loc = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        //Asigna latitud y longitud
        latitud = loc.getLatitude();
        longitud = loc.getLongitude();
        //Posiciona el mapa segun la localizacion si ya esta
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 16.0f));
        }
        //Busca establecimientos
        searchEstablishments();
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
        //Indicador de cambio
        boolean change = true;
        switch (v.getId()) {
            case R.id.b1: /*Disminuir radio radio*/
                selectedRatio--;
                if (selectedRatio < 0) {
                    selectedRatio = 0;
                    change = false;
                }
                break;
            case R.id.b2: /*Disminuir radio radio*/
                selectedRatio++;
                if (selectedRatio >= ratios.length) {
                    selectedRatio = ratios.length - 1;
                    change = false;
                }
                break;
            case R.id.selectedEstab:/*Establecimiento seleccionado*/
                //Revisa que haya algun establecimiento seleccionado
                if (!selectedEstab.getText().toString().equals(Constants.BLANKS)) {
                    //Crea intent para ir a detalle establecimiento
                    Intent intent = new Intent(CercanoaMiActivity.this, ConsultaEstabActivity.class);
                    //Inicia
                    startActivity(intent);
                }
                break;
        }
        if (change && (v.getId() == R.id.b1 || v.getId() == R.id.b2)) {/*Busqueda*/
            //Establecimiento seleccionado, ninguno
            selectedEstab.setText(Constants.BLANKS);
            //Asigna texto de selected ratio
            radio_busqueda = (TextView) findViewById(R.id.radio_busqueda);
            String selectedRatioText = getResources().getString(R.string.radio_busqueda)
                    + Constants.TWO_POINTS + Constants.BLANK_SPACE + ratios[selectedRatio] + Constants.KM;
            radio_busqueda.setText(selectedRatioText);
            //Realiza busqueda
            searchEstablishments();
        }
    }
}
