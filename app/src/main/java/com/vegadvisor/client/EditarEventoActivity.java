package com.vegadvisor.client;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.vegadvisor.client.bo.Csptpais;
import com.vegadvisor.client.bo.Esmestab;
import com.vegadvisor.client.bo.Evdimaev;
import com.vegadvisor.client.bo.Evmevent;
import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.DateUtils;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditarEventoActivity extends VegAdvisorActivity implements DialogInterface.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

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
    private List<File> eventImagesFiles;

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

    //Lista de establecimientos
    private List<Esmestab> lsEstab;

    //País seleccionado
    private Csptpais selectedPais;

    //Ciudad seleccionada
    private Cspciuda selectedCiudad;

    //Establecimiento seleccionado
    private Esmestab selectedEstab;

    /**
     * Fecha evento y tipo Evento
     */
    private TextView fecha, tipo_evento, pais, ciudad;

    //Campos de texto de la pantalla
    private EditText descripcion, hora, localizacion;

    //Campos de texto autocomplete
    private AutoCompleteTextView establecimiento;

    /**
     * Dialog para seleccionar hora de apertura
     */
    private TimePickerDialog timePickerDialog;

    /**
     * Evento activo
     */
    private CheckBox activo;

    /**
     * Evento a editar
     */
    private Evmevent evento;


    /**
     * Latitud y longitud
     */
    private double latitud, longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_evento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Set loading icon false
        this.setShowLoadingIcon(false);
        //Lista de imagenes a enviar
        eventImagesFiles = new ArrayList<>();
        //Upload responses en cero
        totalUploadResponses = 0;
        //Obtiene elementos de la pantalla
        fecha = (TextView) findViewById(R.id.fecha_evento);
        hora = (EditText) findViewById(R.id.hora_evento);
        localizacion = (EditText) findViewById(R.id.localizacion);
        descripcion = (EditText) findViewById(R.id.descripcion_evento);
        tipo_evento = (TextView) findViewById(R.id.tipo_evento);
        pais = (TextView) findViewById(R.id.pais);
        ciudad = (TextView) findViewById(R.id.ciudad);
        establecimiento = (AutoCompleteTextView) findViewById(R.id.establecimiento);
        activo = (CheckBox) findViewById(R.id.activo);
        //Boton de envio
        findViewById(R.id.b1).setOnClickListener(this);
        //Ingresar otra imagen
        b2 = (ImageButton) findViewById(R.id.b2);
        b2.setOnClickListener(this);
        //Click Listener de fecha y hora
        hora.setOnClickListener(this);
        //Obtiene layout de imagenes
        imagenes = (LinearLayout) findViewById(R.id.imagenes);
        //Listener a campos de autocomplete
        setListenerToAutocompleteFields();
        //Inicia Pantalla
        initScreen();
        //Hora de evento
        //Inicia time picker dialog open
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hours, int minutes) {
                hora.setText(DateUtils.getTimeString(hours, minutes));
            }
        }, Integer.valueOf(hora.getText().toString().split(Constants.TWO_POINTS)[0]),
                Integer.valueOf(hora.getText().toString().split(Constants.TWO_POINTS)[1]),
                false);
        //Acciones de mapa
        mapActions();
        //Inicia google api client
        buildGoogleApiClient();
    }

    /**
     * Inicia pantalla con valores del evento
     */
    private void initScreen() {
        //Asigna campos
        //Obtiene evento
        evento = SessionData.getInstance().getEvent();
        //Fecha evento
        fecha.setText(DateUtils.getDateString(evento.getId().getEvefevefk()));
        //Tipo evento
        tipo_evento.setText(evento.getEventTypeName());
        //Pais
        SessionData.getInstance().executeServiceList(361,
                getResources().getString(R.string.basic_getCountries),
                this.createParametersMap("countryCode", evento.getId().getPaicpaiak()),
                new TypeToken<List<Csptpais>>() {
                }.getType()
        );

        //Ciudad
        SessionData.getInstance().executeServiceList(362,
                getResources().getString(R.string.basic_getCities),
                this.createParametersMap("countryCode", evento.getId().getPaicpaiak(),
                        "cityCode", evento.getId().getCiucciuak()),
                new TypeToken<List<Cspciuda>>() {
                }.getType()
        );
        //Descripcion evento
        descripcion.setText(evento.getEvedeveaf());
        //Hora evento
        hora.setText(evento.getEvehoratf());
        //Establecimiento
        if (evento.getEstcestnk() != 0) {
            establecimiento.setText(evento.getEstablishmentName());
        }
        //Localizacion
        localizacion.setText(evento.getEstloceaf());
        //Indicador de evento activo
        activo.setChecked(evento.getEveiactsf().equals(Constants.ONE));
        //Imagenes del evento
        for (Evdimaev imaev : evento.getImages()) {
            //Envia petición para cargar imagen
            SessionData.getInstance().executeServiceImage(372,
                    getResources().getString(R.string.image_downloadImage),
                    EditarEventoActivity.this.createParametersMap("imagePath", imaev.getImerimaaf()));
        }
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
                //Datos
                String[] data;
                //Array Adapter
                ArrayAdapter<?> adapter;
                //Revisa id de servicio ejecutado
                switch (serviceId) {
                    case 361: /*Llega nombre del pais*/
                        lsPais = (List<Csptpais>) result;
                        if (lsPais.size() > 0) {
                            //Asigna pais
                            selectedPais = lsPais.get(0);
                            //Asigna nombre al campo
                            pais.setText(selectedPais.getPaidpaiaf());
                        }
                        break;
                    case 362: /*Llega nombre de ciudad*/
                        lsCiudad = (List<Cspciuda>) result;
                        if (lsCiudad.size() > 0) {
                            //Asigna ciudad
                            selectedCiudad = lsCiudad.get(0);
                            //Asigna nombre al campo
                            ciudad.setText(selectedCiudad.getCiunciuaf());
                        }
                        break;
                    case 366:/*Busqueda de Establecimientos*/
                        //Asigna lista de respuesta
                        lsEstab = (List<Esmestab>) result;
                        //Datos
                        data = new String[result.size()];
                        //Recorre lista para asignar a datos
                        for (int i = 0; i < result.size(); i++) {
                            Esmestab estab = (Esmestab) result.get(i);
                            data[i] = estab.getEstnestaf();
                        }
                        //Array Adapter
                        adapter = new ArrayAdapter<Object>(EditarEventoActivity.this,
                                android.R.layout.simple_dropdown_item_1line, data);
                        establecimiento.setAdapter(adapter);
                        //Umbral
                        establecimiento.setThreshold(1);
                        //Notifica
                        adapter.notifyDataSetChanged();
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
                    case 367: /*Actualizacion de evento de Evento*/
                        //Revisa si fue exitosa la actualización
                        if (Constants.ONE.equals(result.getValidationInd())) {/*Exitoso*/
                            //Revisa si no habían imagenes
                            if (eventImagesFiles.size() == 0) {/*Sin imagenes*/
                                //Navega hacia el menú principal de nuevo
                                Intent intent = new Intent(EditarEventoActivity.this, DetalleEventoActivity.class);
                                //Navega
                                startActivity(intent);
                                //Finaliza Actividad
                                finish();
                            } else {/*Hay Imagenes*/
                                //Envía imágenes al servidor
                                for (File image : eventImagesFiles) {
                                    SessionData.getInstance().executeServiceRV(368,
                                            getResources().getString(R.string.image_uploadEventImage),
                                            EditarEventoActivity.this.createParametersMap(
                                                    "countryCode", result.getParams().get("paicpaiak"),
                                                    "cityCode", result.getParams().get("ciucciuak"),
                                                    "eventDate", result.getParams().get("evefevefk"),
                                                    "eventSecuence", result.getParams().get("evecevenk")),
                                            image);
                                }
                            }
                        }
                        break;
                    case 368: /*Respuesta de upload de imagen*/
                        //Incrementa contador
                        totalUploadResponses++;
                        if (totalUploadResponses == eventImagesFiles.size()) {/*Todos*/
                            //Navega hacia el menú principal de nuevo
                            Intent intent = new Intent(EditarEventoActivity.this, EventosActivity.class);
                            //Navega
                            startActivity(intent);
                            //Finaliza Actividad
                            finish();
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
                    //Crea nueva image view
                    ImageView image = new ImageView(getApplicationContext());
                    Bitmap scaled = Bitmap.createScaledBitmap(result, b2.getWidth(), b2.getHeight(), true);
                    //Asigna bitmap
                    image.setImageBitmap(scaled);
                    //Padding
                    image.setPadding(5, 5, 5, 5);
                    //Ingresa imagen al list view de imagenes
                    imagenes.addView(image, 0);
                }
            }
        });
    }

    /**
     * Obtiene Lista de establecimientos
     */
    private void getEsmestabData() {
        //Obtiene clue de establecimiento
        String clue = establecimiento.getText().toString().trim();
        if (clue.length() > Constants.MIN_AUTOCOMPLETE_CHARS) {/*Que haya el minimo de caracteres autocomplete*/
            //Obtiene establecimientos
            SessionData.getInstance().executeServiceList(366,
                    getResources().getString(R.string.establishment_findEstablishments),
                    this.createParametersMap("clue", clue,
                            "ratio", Constants.DEF_SEARCH_RATIO,
                            "latitud", Constants.BLANKS + latitud,
                            "longitud", Constants.BLANKS + longitud), new TypeToken<List<Esmestab>>() {
                    }.getType());
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
        eventImagesFiles.add(new File(imagePath));
    }


    /**
     * Asigna listeners de autocomplete a los campos de autocomplete
     */
    private void setListenerToAutocompleteFields() {
        //Para Establecimiento
        establecimiento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Ejecuta metodo para obtener opciones
                getEsmestabData();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        establecimiento.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Obtiene tipo evento seleccionado
                selectedEstab = lsEstab.get(position);
                //Asigna pais y ciudad
                //Pais
                //Obtiene Nombre del pais
                SessionData.getInstance().executeServiceList(361,
                        getResources().getString(R.string.basic_getCountries),
                        createParametersMap("countryCode", selectedEstab.getPaicpaiak()),
                        new TypeToken<List<Csptpais>>() {
                        }.getType()
                );
                //Ciudad
                SessionData.getInstance().executeServiceList(362,
                        getResources().getString(R.string.basic_getCities),
                        createParametersMap("countryCode", selectedEstab.getPaicpaiak(), "cityCode", selectedEstab.getCiucciuak()),
                        new TypeToken<List<Cspciuda>>() {
                        }.getType()
                );
                //Localizacion
                localizacion.setText(selectedEstab.getEstdireaf());
                //Asigna marcador de ubicacion en el mapa
                eventLocation = new LatLng(selectedEstab.getEstlatinf(), selectedEstab.getEstlongnf());
                //Quita todos los markers que hayan
                googleMap.clear();
                //Adiciona nuevo marker
                googleMap.addMarker(new MarkerOptions().position(eventLocation));
                //Posiciona camara
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        selectedEstab.getEstlatinf(), selectedEstab.getEstlongnf()), 18.0f));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b1:/*Enviar datos*/
                //Valida campos y envia solicitud para guardar
                validateFieldsAndSend();
                break;
            case R.id.b2:/*Adicionar imagen*/
                //Lanza dialogo de selección de imagen
                this.launchSelectImageDialog();
                break;
            case R.id.hora_evento: /*Seleccionar hora evento*/
                timePickerDialog.show();
                break;
        }
    }

    /**
     * Valida campos y envia formulario para registro en el servidor
     *
     * @return Indicador de ejecucion
     */
    private boolean validateFieldsAndSend() {
        //descripcion
        if (descripcion == null) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.descripcion).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //fecha
        if (fecha == null) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.fecha_evento).toString(), Toast.LENGTH_SHORT).show();
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
        //Obtiene evento nuevamente
        evento = SessionData.getInstance().getEvent();
        //Set loading icon true
        this.setShowLoadingIcon(true);
        //Envía actualización al servidor
        SessionData.getInstance().executeServiceRV(367,
                getResources().getString(R.string.event_updateEvent),
                this.createParametersMap(
                        "userId", SessionData.getInstance().getUserId(),
                        "countryCode", selectedPais.getPaicpaiak(),
                        "cityCode", selectedCiudad.getId().getCiucciuak(),
                        "dateEvent", fecha.getText().toString().trim().replace(Constants.MINUS, Constants.BLANKS),
                        "eventSec", Constants.BLANKS + evento.getId().getEvecevenk(),
                        "timeEvent", hora.getText().toString().trim().replace(Constants.TWO_POINTS, Constants.BLANKS),
                        "eventName", descripcion.getText().toString().trim(),
                        "establishmentId", selectedEstab != null ? Constants.BLANKS + selectedEstab.getEstcestnk() : Constants.ZERO,
                        "latitud", Constants.BLANKS + eventLocation.latitude,
                        "longitud", Constants.BLANKS + eventLocation.longitude,
                        "placeName", localizacion.getText().toString().trim(),
                        "eventType", Constants.BLANKS + evento.getTevctevnk(),
                        "isActive", activo.isChecked() ? Constants.ONE : Constants.ZERO));
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
                new AlertDialog.Builder(EditarEventoActivity.this).setTitle(R.string.seleccionar_ubicacion)
                        .setPositiveButton(R.string.aceptar, EditarEventoActivity.this)
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
        //Asigna marcador de ubicacion en el mapa
        eventLocation = new LatLng(evento.getEvelatinf(), evento.getEvelongnf());
        //Quita todos los markers que hayan
        googleMap.clear();
        //Adiciona nuevo marker
        googleMap.addMarker(new MarkerOptions().position(eventLocation));
        //Posiciona camara
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                evento.getEvelatinf(), evento.getEvelongnf()), 18.0f));
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
