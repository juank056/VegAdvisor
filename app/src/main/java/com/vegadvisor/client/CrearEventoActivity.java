package com.vegadvisor.client;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.vegadvisor.client.bo.Csptieve;
import com.vegadvisor.client.bo.Csptpais;
import com.vegadvisor.client.bo.Esmestab;
import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.bo.Usmusuar;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.DateUtils;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrearEventoActivity extends VegAdvisorActivity implements DialogInterface.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

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

    //Lista tipos eventos seleccion
    private List<Csptieve> lsTieve;

    //Lista de establecimientos
    private List<Esmestab> lsEstab;

    //País seleccionado
    private Csptpais selectedPais;

    //Ciudad seleccionada
    private Cspciuda selectedCiudad;

    //Tipo evento seleccionado
    private Csptieve selectedTieve;

    //Establecimiento seleccionado
    private Esmestab selectedEstab;

    //Campos de texto de la pantalla
    private EditText fecha, descripcion, hora, localizacion;

    //Campos de texto autocomplete
    private AutoCompleteTextView pais, ciudad, tipo_evento, establecimiento;

    /**
     * Dialogos de seleccion de fecha
     */
    private DatePickerDialog datePickerDialog;

    /**
     * Dialog para seleccionar hora de apertura
     */
    private TimePickerDialog timePickerDialog;

    /**
     * Date formater
     */
    private SimpleDateFormat dateFormat;

    /**
     * Objetos de fecha de evento
     */
    private Date oFecha;

    /**
     * Latitud y longitud
     */
    private double latitud, longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);
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
        fecha = (EditText) findViewById(R.id.fecha_evento);
        hora = (EditText) findViewById(R.id.hora_evento);
        localizacion = (EditText) findViewById(R.id.localizacion);
        descripcion = (EditText) findViewById(R.id.descripcion_evento);
        //Texto por defecto de apertura y cierre
        //Fecha actual
        oFecha = DateUtils.getCurrentUtilDate();
        //DatePicker
        //Para date
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        fecha.setText(dateFormat.format(oFecha.getTime()));
        //Texto por defecto de hora de evento
        hora.setText(Constants.DEF_TIME);
        //Creacion del dialogo de fecha
        //Dialog de Date Picker
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                //Asigna fecha
                oFecha = newDate.getTime();
                fecha.setText(dateFormat.format(newDate.getTime()));
            }
        }, oFecha.getYear() + 1900, oFecha.getMonth(), oFecha.getDate());
        tipo_evento = (AutoCompleteTextView) findViewById(R.id.tipo_evento);
        pais = (AutoCompleteTextView) findViewById(R.id.pais);
        ciudad = (AutoCompleteTextView) findViewById(R.id.ciudad);
        establecimiento = (AutoCompleteTextView) findViewById(R.id.establecimiento);
        //Hora de evento
        //Inicia time picker dialog open
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hours, int minutes) {
                hora.setText(DateUtils.getTimeString(hours, minutes));
            }
        }, Integer.valueOf(hora.getText().toString().split(Constants.TWO_POINTS)[0]),
                Integer.valueOf(hora.getText().toString().split(Constants.TWO_POINTS)[1]),
                false);
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
        //Click Listener de fecha y hora
        fecha.setOnClickListener(this);
        hora.setOnClickListener(this);
        //Obtiene layout de imagenes
        imagenes = (LinearLayout) findViewById(R.id.imagenes);
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
                    case 323:/*Busqueda de paises*/
                        //Asigna lista de respuesta
                        lsPais = (List<Csptpais>) result;
                        //Datos
                        data = new String[result.size()];
                        //Recorre lista para asignar a datos
                        for (int i = 0; i < result.size(); i++) {
                            Csptpais pais = (Csptpais) result.get(i);
                            data[i] = pais.getPaidpaiaf();
                        }
                        //Array Adapter
                        adapter = new ArrayAdapter<Object>(CrearEventoActivity.this,
                                android.R.layout.simple_dropdown_item_1line, data);
                        pais.setAdapter(adapter);
                        //Umbral
                        pais.setThreshold(1);
                        //Notifica
                        adapter.notifyDataSetChanged();
                        break;
                    case 324:/*Busqueda de ciudades*/
                        //Asigna lista de respuesta
                        lsCiudad = (List<Cspciuda>) result;
                        //Datos
                        data = new String[result.size()];
                        //Recorre lista para asignar a datos
                        for (int i = 0; i < result.size(); i++) {
                            Cspciuda ciudad = (Cspciuda) result.get(i);
                            data[i] = ciudad.getCiunciuaf();
                        }
                        //Array Adapter
                        adapter = new ArrayAdapter<Object>(CrearEventoActivity.this,
                                android.R.layout.simple_dropdown_item_1line, data);
                        ciudad.setAdapter(adapter);
                        //Umbral
                        ciudad.setThreshold(1);
                        //Notifica
                        adapter.notifyDataSetChanged();
                        break;
                    case 321: /*Llega nombre del pais*/
                        lsPais = (List<Csptpais>) result;
                        if (lsPais.size() > 0) {
                            //Asigna pais
                            selectedPais = lsPais.get(0);
                            //Asigna nombre al campo
                            pais.setText(selectedPais.getPaidpaiaf());
                        }
                        break;
                    case 322: /*Llega nombre de ciudad*/
                        lsCiudad = (List<Cspciuda>) result;
                        if (lsCiudad.size() > 0) {
                            //Asigna ciudad
                            selectedCiudad = lsCiudad.get(0);
                            //Asigna nombre al campo
                            ciudad.setText(selectedCiudad.getCiunciuaf());
                        }
                        break;
                    case 325:/*Busqueda de tipos de evento*/
                        //Asigna lista de respuesta
                        lsTieve = (List<Csptieve>) result;
                        //Datos
                        data = new String[result.size()];
                        //Recorre lista para asignar a datos
                        for (int i = 0; i < result.size(); i++) {
                            Csptieve tieve = (Csptieve) result.get(i);
                            data[i] = tieve.getTevntevaf();
                        }
                        //Array Adapter
                        adapter = new ArrayAdapter<Object>(CrearEventoActivity.this,
                                android.R.layout.simple_dropdown_item_1line, data);
                        tipo_evento.setAdapter(adapter);
                        //Umbral
                        tipo_evento.setThreshold(1);
                        //Notifica
                        adapter.notifyDataSetChanged();
                        break;
                    case 326:/*Busqueda de Establecimientos*/
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
                        adapter = new ArrayAdapter<Object>(CrearEventoActivity.this,
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
        Log.d(Constants.DEBUG, "Respuesta Recibida: " + serviceId + ". Service: " + service + " Res: " + result);
        //Super
        super.receiveServerCallResult(serviceId, service, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Muestra mensaje recibido
                Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                //Revisa de acuerdo a lo ejecutado
                switch (serviceId) {
                    case 337: /*Creacion de Evento*/
                        //Revisa si fue exitosa la actualización
                        if (Constants.ONE.equals(result.getValidationInd())) {/*Exitoso*/
                            //Revisa si no habían imagenes
                            if (eventImagesFiles.size() == 0) {/*Sin imagenes*/
                                //Navega hacia el menú principal de nuevo
                                Intent intent = new Intent(CrearEventoActivity.this, EventosActivity.class);
                                //Navega
                                startActivity(intent);
                                //Finaliza Actividad
                                finish();
                            } else {/*Hay Imagenes*/
                                //Envía imágenes al servidor
                                for (File image : eventImagesFiles) {
                                    SessionData.getInstance().executeServiceRV(338,
                                            getResources().getString(R.string.image_uploadEventImage),
                                            CrearEventoActivity.this.createParametersMap(
                                                    "countryCode", result.getParams().get("paicpaiak"),
                                                    "cityCode", result.getParams().get("ciucciuak"),
                                                    "eventDate", result.getParams().get("evefevefk"),
                                                    "eventSecuence", result.getParams().get("evecevenk")),
                                            image);
                                }
                            }
                        }
                        break;
                    case 338: /*Respuesta de upload de imagen*/
                        //Incrementa contador
                        totalUploadResponses++;
                        if (totalUploadResponses == eventImagesFiles.size()) {/*Todos*/
                            //Navega hacia el menú principal de nuevo
                            Intent intent = new Intent(CrearEventoActivity.this, EventosActivity.class);
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
     * Obtiene Lista de nombres de tipos de evento
     */
    private void getCsptieveData() {
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

    /**
     * Obtiene Lista de establecimientos
     */
    private void getEsmestabData() {
        //Obtiene clue de establecimiento
        String clue = establecimiento.getText().toString().trim();
        if (clue.length() > Constants.MIN_AUTOCOMPLETE_CHARS) {/*Que haya el minimo de caracteres autocomplete*/
            //Obtiene establecimientos
            SessionData.getInstance().executeServiceList(326,
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
                getCsptieveData();
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
                SessionData.getInstance().executeServiceList(321,
                        getResources().getString(R.string.basic_getCountries),
                        createParametersMap("countryCode", selectedEstab.getPaicpaiak()),
                        new TypeToken<List<Csptpais>>() {
                        }.getType()
                );
                //Ciudad
                SessionData.getInstance().executeServiceList(322,
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
            case R.id.fecha_evento: /*Seleccionar fecha evento*/
                datePickerDialog.show();
                break;
            case R.id.hora_evento: /*Seleccionar fecha evento*/
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
        //Tipo Establecimiento
        if (selectedTieve == null) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.tipo_evento).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
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
        //Set loading icon true
        this.setShowLoadingIcon(true);
        //Envía actualización al servidor
        SessionData.getInstance().executeServiceRV(337,
                getResources().getString(R.string.event_createEvent),
                this.createParametersMap(
                        "userId", SessionData.getInstance().getUserId(),
                        "countryCode", selectedPais.getPaicpaiak(),
                        "cityCode", selectedCiudad.getId().getCiucciuak(),
                        "eventName", descripcion.getText().toString().trim(),
                        "dateEvent", fecha.getText().toString().trim().replace(Constants.MINUS, Constants.BLANKS),
                        "timeEvent", hora.getText().toString().trim().replace(Constants.TWO_POINTS, Constants.BLANKS),
                        "establishmentId", selectedEstab != null ? Constants.BLANKS + selectedEstab.getEstcestnk() : Constants.ZERO,
                        "latitud", Constants.BLANKS + eventLocation.latitude,
                        "longitud", Constants.BLANKS + eventLocation.longitude,
                        "placeName", localizacion.getText().toString().trim(),
                        "eventType", Constants.BLANKS + selectedTieve.getTevctevnk()));
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
        //Posiciona el mapa segun la localizacion si ya esta
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 18.0f));
        }
        //Asigna latitud y longitud
        latitud = loc.getLatitude();
        longitud = loc.getLongitude();
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
