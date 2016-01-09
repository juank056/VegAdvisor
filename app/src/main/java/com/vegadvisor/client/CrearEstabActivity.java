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
import com.vegadvisor.client.bo.Csptiest;
import com.vegadvisor.client.bo.Csptpais;
import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.bo.Usmusuar;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.DateUtils;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrearEstabActivity extends VegAdvisorActivity implements DialogInterface.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

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
    private LatLng estabLocation;

    /**
     * Google api client
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Campos edit text de la pantalla
     */
    private EditText nombreEstab, descripcion, direccion, telefono, apertura, cierre;

    /**
     * Campos de autocomplete
     */
    private AutoCompleteTextView pais, ciudad, tipo_establecimiento;

    /**
     * Dialog para seleccionar hora de apertura
     */
    private TimePickerDialog timePickerDialogOpen;

    /**
     * Dialog para seleccionar hora de cierre
     */
    private TimePickerDialog timePickerDialogClose;

    /**
     * Lista de paises de selección
     */
    private List<Csptpais> lsPais;

    /**
     * Lista de ciudades de seleccion
     */
    private List<Cspciuda> lsCiudad;

    /**
     * Lista de tipos de establecimiento
     */
    private List<Csptiest> lsTiest;

    /**
     * Pais seleccionado
     */
    private Csptpais selectedPais;

    /**
     * Ciudad seleccionada
     */
    private Cspciuda selectedCiudad;

    /**
     * Tipo Establecimiento seleccionado
     */
    private Csptiest selectedTiest;

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


    /**
     * @param savedInstanceState Instancia
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_estab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Set loading icon false
        this.setShowLoadingIcon(false);
        //Lista de imagenes a enviar
        estabImagesFiles = new ArrayList<>();
        //Upload responses en cero
        totalUploadResponses = 0;
        //Obtiene elementos de la pantalla
        nombreEstab = (EditText) findViewById(R.id.nombreEstab);
        descripcion = (EditText) findViewById(R.id.descripcion);
        direccion = (EditText) findViewById(R.id.direccion);
        telefono = (EditText) findViewById(R.id.telefono);
        apertura = (EditText) findViewById(R.id.apertura);
        cierre = (EditText) findViewById(R.id.cierre);
        imagenes = (LinearLayout) findViewById(R.id.imagenes);
        //Texto por defecto de apertura y cierre
        apertura.setText(Constants.DEF_OPEN_TIME);
        cierre.setText(Constants.DEF_CLOSE_TIME);
        apertura.setOnClickListener(this);
        cierre.setOnClickListener(this);
        pais = (AutoCompleteTextView) findViewById(R.id.pais);
        ciudad = (AutoCompleteTextView) findViewById(R.id.ciudad);
        tipo_establecimiento = (AutoCompleteTextView) findViewById(R.id.tipo_establecimiento);
        //Inicia time picker dialog open
        timePickerDialogOpen = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hours, int minutes) {
                apertura.setText(DateUtils.getTimeString(hours, minutes));
            }
        }, Integer.valueOf(apertura.getText().toString().split(Constants.TWO_POINTS)[0]),
                Integer.valueOf(apertura.getText().toString().split(Constants.TWO_POINTS)[1]),
                false);
        //Inicia time picker dialog close
        timePickerDialogClose = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hours, int minutes) {
                cierre.setText(DateUtils.getTimeString(hours, minutes));
            }
        }, Integer.valueOf(cierre.getText().toString().split(Constants.TWO_POINTS)[0]),
                Integer.valueOf(cierre.getText().toString().split(Constants.TWO_POINTS)[1]),
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
        estabImagesFiles.add(new File(imagePath));
    }

    /**
     *
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
                    case 106: /*Creacion de establecimiento*/
                        //Revisa si fue exitosa la actualización
                        if (Constants.ONE.equals(result.getValidationInd())) {/*Exitoso*/
                            //Revisa si no habían imagenes
                            if (estabImagesFiles.size() == 0) {/*Sin imagenes*/
                                //Navega hacia el menú principal de nuevo
                                Intent intent = new Intent(CrearEstabActivity.this, MenuPrincipalActivity.class);
                                //Navega
                                startActivity(intent);
                                //Finaliza Actividad
                                finish();
                            } else {/*Hay Imagenes*/
                                //Obtiene datos del response
                                String establishmentId = result.getParams().get("establishmentId");
                                //Envía imágenes al servidor
                                for (File image : estabImagesFiles) {
                                    SessionData.getInstance().executeServiceRV(107,
                                            getResources().getString(R.string.image_uploadEstablishmentImage),
                                            CrearEstabActivity.this.createParametersMap("establishmentId",
                                                    establishmentId),
                                            image);
                                }
                            }
                        }
                        break;
                    case 107: /*Respuesta de upload de imagen*/
                        //Incrementa contador
                        totalUploadResponses++;
                        if (totalUploadResponses == estabImagesFiles.size()) {/*Todos*/
                            //Navega hacia el menú principal de nuevo
                            Intent intent = new Intent(CrearEstabActivity.this, MenuPrincipalActivity.class);
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
                    case 101: /*Llega nombre del pais*/
                        lsPais = (List<Csptpais>) result;
                        if (lsPais.size() > 0) {
                            //Asigna pais
                            selectedPais = lsPais.get(0);
                            //Asigna nombre al campo
                            pais.setText(selectedPais.getPaidpaiaf());
                        }
                        break;
                    case 102: /*Llega nombre de ciudad*/
                        lsCiudad = (List<Cspciuda>) result;
                        if (lsCiudad.size() > 0) {
                            //Asigna ciudad
                            selectedCiudad = lsCiudad.get(0);
                            //Asigna nombre al campo
                            ciudad.setText(selectedCiudad.getCiunciuaf());
                        }
                        break;
                    case 103:/*Busqueda de paises*/
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
                        adapter = new ArrayAdapter<Object>(CrearEstabActivity.this,
                                android.R.layout.simple_dropdown_item_1line, data);
                        pais.setAdapter(adapter);
                        //Umbral
                        pais.setThreshold(1);
                        //Notifica
                        adapter.notifyDataSetChanged();
                        break;
                    case 104:/*Busqueda de ciudades*/
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
                        adapter = new ArrayAdapter<Object>(CrearEstabActivity.this,
                                android.R.layout.simple_dropdown_item_1line, data);
                        ciudad.setAdapter(adapter);
                        //Umbral
                        ciudad.setThreshold(1);
                        //Notifica
                        adapter.notifyDataSetChanged();
                        break;
                    case 105:/*Busqueda de tipos de establecimiento*/
                        //Asigna lista de respuesta
                        lsTiest = (List<Csptiest>) result;
                        //Datos
                        data = new String[result.size()];
                        //Recorre lista para asignar a datos
                        for (int i = 0; i < result.size(); i++) {
                            Csptiest tiest = (Csptiest) result.get(i);
                            data[i] = tiest.getTesntesaf();
                        }
                        //Array Adapter
                        adapter = new ArrayAdapter<Object>(CrearEstabActivity.this,
                                android.R.layout.simple_dropdown_item_1line, data);
                        tipo_establecimiento.setAdapter(adapter);
                        //Umbral
                        tipo_establecimiento.setThreshold(1);
                        //Notifica
                        adapter.notifyDataSetChanged();
                        break;
                }

            }
        });
    }

    /**
     * Inicia la pantalla
     */
    private void initScreen() {
        //Obtiene usuario
        Usmusuar usuar = SessionData.getInstance().getUsuarObject();
        //Pais
        if (!Constants.BLANKS.equals(usuar.getPaicpaiak())) {
            //Obtiene Nombre del pais
            SessionData.getInstance().executeServiceList(101,
                    getResources().getString(R.string.basic_getCountries),
                    this.createParametersMap("countryCode", usuar.getPaicpaiak()),
                    new TypeToken<List<Csptpais>>() {
                    }.getType()
            );
        }
        //Ciudad
        if (!Constants.BLANKS.equals(usuar.getCiucciuak())) {
            //Obtiene Nombre del pais
            SessionData.getInstance().executeServiceList(102,
                    getResources().getString(R.string.basic_getCities),
                    this.createParametersMap("countryCode", usuar.getPaicpaiak(), "cityCode", usuar.getCiucciuak()),
                    new TypeToken<List<Cspciuda>>() {
                    }.getType()
            );
        }
    }

    /**
     * Asigna Listeners a los campos de autocomplete
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
        //Para Tipo Establecimiento
        tipo_establecimiento.addTextChangedListener(new TextWatcher() {
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
        tipo_establecimiento.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Obtiene tipo establecimiento seleccionado
                selectedTiest = lsTiest.get(position);
            }
        });
    }

    /**
     * Obtiene Lista de nombres de c
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
                SessionData.getInstance().executeServiceList(104, getResources().getString(R.string.basic_getCities),
                        params, new TypeToken<List<Cspciuda>>() {
                        }.getType());
            }
        }


    }

    /**
     * Obtiene Lista de nombres de países
     */
    private void getCsptpaisData() {
        //Obtiene clue de pais
        String clue = pais.getText().toString().trim();
        if (clue.length() > Constants.MIN_AUTOCOMPLETE_CHARS) {/*Que haya el minimo de caracteres autocomplete*/
            Map<String, String> params = new HashMap<>();
            params.put("clue", clue);
            //Ejecuta servicio
            SessionData.getInstance().executeServiceList(103, getResources().getString(R.string.basic_getCountries),
                    params, new TypeToken<List<Csptpais>>() {
                    }.getType());
        }
    }

    /**
     * Obtiene Lista de nombres de tipos de establecimiento
     */
    private void getCsptiestData() {
        //Obtiene clue de pais
        String clue = tipo_establecimiento.getText().toString().trim();
        if (clue.length() > Constants.MIN_AUTOCOMPLETE_CHARS) {/*Que haya el minimo de caracteres autocomplete*/
            Map<String, String> params = new HashMap<>();
            params.put("clue", clue);
            //Ejecuta servicio
            SessionData.getInstance().executeServiceList(105, getResources().getString(R.string.basic_getEstablishmentTypes),
                    params, new TypeToken<List<Csptiest>>() {
                    }.getType());
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
            case R.id.b1:/*Enviar datos*/
                //Valida campos y envia solicitud para guardar
                validateFieldsAndSend();
                break;
            case R.id.apertura: /*Hora apertura*/
                timePickerDialogOpen.show();
                break;
            case R.id.cierre: /*Hora cierre*/
                timePickerDialogClose.show();
                break;
            case R.id.b2: /*Adicionar imagen*/
                //Lanza dialogo de selección de imagen
                this.launchSelectImageDialog();
                break;
        }
    }

    /**
     * Se encarga de validar los campos ingresados y enviarlos al servidor
     */
    private boolean validateFieldsAndSend() {
        //Nombre establecimiento
        if (Constants.BLANKS.equals(nombreEstab.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.nombre_establecimiento).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Pais
        if (selectedPais == null) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.pais).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Ciudad
        if (selectedCiudad == null) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.ciudad).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Tipo Establecimiento
        if (selectedTiest == null) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.tipo_establecimiento).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Direccion
        if (Constants.BLANKS.equals(direccion.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.direccion).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Telefono
        if (Constants.BLANKS.equals(telefono.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.telefono).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Horario Apertura
        if (Constants.BLANKS.equals(apertura.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.horario).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Horario Cierre
        if (Constants.BLANKS.equals(cierre.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.horario).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Ubicación
        if (estabLocation == null) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.ubicacion).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Set loading icon true
        this.setShowLoadingIcon(true);
        //Obtiene objeto de usuario
        Usmusuar usuar = SessionData.getInstance().getUsuarObject();
        //Envía actualización al servidor
        SessionData.getInstance().executeServiceRV(106,
                getResources().getString(R.string.establishment_createOrUpdateEstablishment),
                this.createParametersMap(
                        "establishmentId", Constants.ZERO,
                        "userId", SessionData.getInstance().getUserId(),
                        "companyName", nombreEstab.getText().toString().trim(),
                        "establishmentType", Constants.BLANKS + selectedTiest.getTesctesnk(),
                        "address", direccion.getText().toString().trim(),
                        "phones", telefono.getText().toString().trim(),
                        "openingTime", apertura.getText().toString().trim().replace(":", ""),
                        "closingTime", cierre.getText().toString().trim().replace(":", ""),
                        "country", selectedPais.getPaicpaiak(),
                        "city", selectedCiudad.getId().getCiucciuak(),
                        "latitud", Constants.BLANKS + estabLocation.latitude,
                        "longitud", Constants.BLANKS + estabLocation.longitude,
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
                estabLocation = latLng;
                new AlertDialog.Builder(CrearEstabActivity.this).setTitle(R.string.seleccionar_ubicacion)
                        .setPositiveButton(R.string.aceptar, CrearEstabActivity.this)
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
        googleMap.addMarker(new MarkerOptions().position(estabLocation));
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
