package com.vegadvisor.client;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.vegadvisor.client.bo.Csptieve;
import com.vegadvisor.client.bo.Csptpais;
import com.vegadvisor.client.bo.Usmusuar;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrearEventoActivity extends VegAdvisorActivity  implements DialogInterface.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    /**
     * Mapa
     */
    private GoogleMap googleMap;

    /**
     * Fragmento del mapa
     */
    private MapFragment mapFragment;

    /**
     * Posicion click
     */
    private LatLng eventLocation;

    /**
     * Google api client
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Layout de imagenes
     */
    private LinearLayout imagenes;

    /**
     * Lista de imágenes a enviar
     */
    private List<File> estabImagesFiles;

    /**
     * Boton de imagen
     */
    private ImageButton b2;

    /**
     * Total de respuestas recibidas en upload de imagenes
     */
    private int totalUploadResponses;

    //Lista países selección
    private List<Csptpais> lsPais;

    //Lista ciudades selección
    private List<Cspciuda> lsCiudad;

    //Lista tipos eventos seleccion
    private List<Csptieve> lsTieve;

    //País seleccionado
    private Csptpais selectedPais;

    //Ciudad seleccionada
    private Cspciuda selectedCiudad;

    private Csptieve selectedTieve;

    private EditText fecha,descripcion;

    private AutoCompleteTextView pais, ciudad, tipo_evento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Lista de imagenes a enviar
        estabImagesFiles = new ArrayList<>();
        //Upload responses en cero
        totalUploadResponses = 0;
        //Obtiene elementos de la pantalla
        fecha=(EditText)findViewById(R.id.fecha_evento);
        descripcion=(EditText)findViewById(R.id.descripcion_evento);
        //Texto por defecto de apertura y cierre
        fecha.setText((CharSequence) Constants.DEFAULT_DATE);
        tipo_evento = (AutoCompleteTextView) findViewById(R.id.tipo_evento);
        pais = (AutoCompleteTextView) findViewById(R.id.pais);
        ciudad = (AutoCompleteTextView) findViewById(R.id.ciudad);

        //Listener a campos de autocomplete
        setListenerToAutocompleteFields();
        //Acciones de mapa
        mapActions();
        //Inicia google api client
        buildGoogleApiClient();
        //Inicia Pantalla
        initScreen();
        //Boton de envio
        findViewById(R.id.b1).setOnClickListener(this);
        //Ingresar otra imagen
        b2 = (ImageButton) findViewById(R.id.b2);
        b2.setOnClickListener(this);
        //CONTINUARRRRRRRRRR!!
    }

    private void initScreen() {
        //Obtiene usuario
        Usmusuar usuar = SessionData.getInstance().getUsuarObject();
        //Pais
        if (!Constants.BLANKS.equals(usuar.getPaicpaiak())) {
            //Obtiene Nombre del pais
            SessionData.getInstance().executeServiceList(321,
                    getResources().getString(R.string.basic_getCountries),
                    this.createParametersMap("countryCode", usuar.getPaicpaiak()),
                    new TypeToken<List<Csptpais>>() {
                    }.getType()
            );
        }
        //Ciudad
        if (!Constants.BLANKS.equals(usuar.getCiucciuak())) {
            //Obtiene Nombre del pais
            SessionData.getInstance().executeServiceList(322,
                    getResources().getString(R.string.basic_getCities),
                    this.createParametersMap("countryCode", usuar.getPaicpaiak(), "cityCode", usuar.getCiucciuak()),
                    new TypeToken<List<Cspciuda>>() {
                    }.getType()
            );
        }
    }


    /**
     * Obtiene Lista de nombres de ciudades
     */
    private void getCspCiudaData() {
        //Revisa que haya un pais seleccionado
        if (selectedPais != null) {/*Hay pais*/
            //Id de pais
            String countryCode = selectedPais.getPaicpaiak();
            //Obtiene clue de ciudad
            String clue = ciudad.getText().toString().trim();
            if (clue.length() > Constants.MIN_AUTOCOMPLETE_CHARS) {/*Que haya el minimo de caracteres autocomplete*/
                Map<String, String> params = new HashMap<>();
                params.put("countryCode", countryCode);
                params.put("clue", clue);
                //Ejecuta servicio
                SessionData.getInstance().executeServiceList(324, getResources().getString(R.string.basic_getCities),
                        params, new TypeToken<List<Cspciuda>>() {
                        }.getType());
            }
        }


    }

    /**
     * Obtiene Lista de nombres de ciudades
     */
    private void getCsptpaisData() {
        //Obtiene clue de pais
        String clue = pais.getText().toString().trim();
        if (clue.length() > Constants.MIN_AUTOCOMPLETE_CHARS) {/*Que haya el minimo de caracteres autocomplete*/
            Map<String, String> params = new HashMap<>();
            params.put("clue", clue);
            //Ejecuta servicio
            SessionData.getInstance().executeServiceList(323, getResources().getString(R.string.basic_getCountries),
                    params, new TypeToken<List<Csptpais>>() {
                    }.getType());
        }
    }

    /**
     * Obtiene Lista de nombres de tipos de establecimiento
     */
    private void getCsptiestData() {
        //Obtiene clue de tipo evento
        String clue = tipo_evento.getText().toString().trim();
        if (clue.length() > Constants.MIN_AUTOCOMPLETE_CHARS) {/*Que haya el minimo de caracteres autocomplete*/
            Map<String, String> params = new HashMap<>();
            params.put("clue", clue);
            //Ejecuta servicio
            SessionData.getInstance().executeServiceList(325, getResources().getString(R.string.basic_getEventTypes),
                    params, new TypeToken<List<Csptieve>>() {
                    }.getType());
        }
    }



    private void setListenerToAutocompleteFields() {
        //Para Pais
        pais.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Ejecuta metodo para obtener opciones
                getCsptpaisData();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        pais.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Obtiene pais seleccionado
                selectedPais = lsPais.get(position);
            }
        });
        //Para Ciudad
        ciudad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Ejecuta metodo para obtener opciones
                getCspCiudaData();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ciudad.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Obtiene pais seleccionado
                selectedCiudad = lsCiudad.get(position);
            }
        });
        //Para Tipo Evento
        tipo_evento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Ejecuta metodo para obtener opciones
                getCsptiestData();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        tipo_evento.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Obtiene tipo evento seleccionado
                selectedTieve = lsTieve.get(position);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.b1:/*Enviar datos*/
                //Valida campos y envia solicitud para guardar
                validateFieldsAndSend();
                break;
            case R.id.b2:/*Adicionar imagen*/
                //Lanza dialogo de selección de imagen
                this.launchSelectImageDialog();
                break;
        }
    }

    private boolean validateFieldsAndSend() {
        //Tipo Establecimiento
        if (selectedTieve == null) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.tipo_evento).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //descripcion
        if(descripcion==null){
            Toast.makeText(getApplicationContext(),getResources().getText(R.string.campoInvalido).toString() +
                getResources().getText(R.string.pais).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //fecha
        if(fecha==null){
            Toast.makeText(getApplicationContext(),getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.pais).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //pais
        if (selectedPais == null) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.pais).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //ciudad
        if (selectedCiudad == null) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.ciudad).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //ubicacion
        if (eventLocation == null) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.ubicacion).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Set loading icon true
        this.setShowLoadingIcon(true);
        //Obtiene objeto de usuario
        Usmusuar usuar = SessionData.getInstance().getUsuarObject();
        //Envía actualización al servidor
        SessionData.getInstance().executeServiceRV(326,
                getResources().getString(R.string.event_createEvent),
                this.createParametersMap(
                        "establishmentId", Constants.ZERO,
                        "userId", SessionData.getInstance().getUserId(),
                        "eventType", Constants.BLANKS + selectedTieve.getTevntevaf(),
                        //"country", selectedPais.getPaicpaiak(),
                        //"city", selectedCiudad.getId().getCiucciuak(),
                        "latitud", Constants.BLANKS + eventLocation.latitude,
                        "longitud", Constants.BLANKS + eventLocation.longitude,
                        "description", descripcion.getText().toString().trim(),
                        "active", Constants.ONE));
        //Finaliza
        return true;
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
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                eventLocation = latLng;
                new AlertDialog.Builder(CrearEventoActivity.this).setTitle(R.string.seleccionar_ubicacion)
                        .setPositiveButton(R.string.aceptar, CrearEventoActivity.this)
                        .setNegativeButton(
                                R.string.cancelar, null)
                        .show();
            }
        });
    }


    /**
     * Evento onclick para seleccionar ubicación en el mapa
     *
     * @param dialog Dialogo
     * @param which  Opcion
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        //Quita todos los markers que hayan
        googleMap.clear();
        //Adiciona nuevo marker
        googleMap.addMarker(new MarkerOptions().position(eventLocation));
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
        //Obtiene localizacion
        Location loc = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        //Posiciona el mapa segun la localizacion si ya esta
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 18.0f));
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
