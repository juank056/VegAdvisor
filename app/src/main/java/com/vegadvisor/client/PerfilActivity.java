package com.vegadvisor.client;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.vegadvisor.client.bo.Cspciuda;
import com.vegadvisor.client.bo.Csptpais;
import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.bo.Usmusuar;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.DateUtils;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerfilActivity extends VegAdvisorActivity implements View.OnClickListener {


    /**
     * Campos de la pantalla
     */
    private EditText name, lastname, fecha_nacimiento, hobbies;

    /**
     * Campos de tipo Autocomplete
     */
    private AutoCompleteTextView pais, ciudad;

    /**
     * Radio buttons
     */
    private RadioButton hombre, mujer;

    /**
     * Indicador de Vegano
     */
    private CheckBox soy_vegano;

    /**
     * Imagen de foto de perfil
     */
    private ImageView userImage;

    /**
     * Lista de paises de selección
     */
    private List<Csptpais> lsPais;

    /**
     * Lista de ciudades de seleccion
     */
    private List<Cspciuda> lsCiudad;

    /**
     * Pais seleccionado
     */
    private Csptpais selectedPais;

    /**
     * Ciudad seleccionada
     */
    private Cspciuda selectedCiudad;

    /**
     * Imagen seleccionada
     */
    private File selectedImageFile;

    /**
     * Dialogo de seleccion de fecha
     */
    private DatePickerDialog datePickerDialog;

    /**
     * Date formater
     */
    private SimpleDateFormat dateFormat;

    /**
     * @param savedInstanceState OnCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Set loading icon false
        this.setShowLoadingIcon(false);
        //Obtiene campos de texto
        name = (EditText) findViewById(R.id.name);
        lastname = (EditText) findViewById(R.id.lastname);
        fecha_nacimiento = (EditText) findViewById(R.id.fecha_nacimiento);
        hobbies = (EditText) findViewById(R.id.hobbies);
        //Campos de autocomplete
        pais = (AutoCompleteTextView) findViewById(R.id.pais);
        ciudad = (AutoCompleteTextView) findViewById(R.id.ciudad);
        //Asigna Listeners a campos Autocomplete
        setListenerToAutocompleteFields();
        //Imagen del perfil de usuario
        userImage = (ImageView) findViewById(R.id.userImage);
        //Hombre y mujer
        hombre = (RadioButton) findViewById(R.id.hombre);
        mujer = (RadioButton) findViewById(R.id.mujer);
        //Soy Vegano
        soy_vegano = (CheckBox) findViewById(R.id.soy_vegano);
        //Botones
        findViewById(R.id.b1).setOnClickListener(this);
        findViewById(R.id.b2).setOnClickListener(this);
        fecha_nacimiento.setOnClickListener(this);
        //Para date
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Dialog de Date Picker
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                //Asigna fecha al usuario
                SessionData.getInstance().getUsuarObject().setUsufnacff(newDate.getTime());
                fecha_nacimiento.setText(dateFormat.format(newDate.getTime()));
            }
        }, SessionData.getInstance().getUsuarObject().getUsufnacff().getYear() + 1900,
                SessionData.getInstance().getUsuarObject().getUsufnacff().getMonth(),
                SessionData.getInstance().getUsuarObject().getUsufnacff().getDate());
        //Inicia pantalla
        initScreen();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Se engarga de procesar el resultado de cargar una imagen
     *
     * @param imageBitmap Bitmap de la imagen cargada
     * @param imagePath   Ruta de la imagen cargada
     */
    @Override
    public void processImageSelectedResponse(Bitmap imageBitmap, String imagePath) {
        //Asigna imagen
        userImage.setImageBitmap(imageBitmap);
        //Crea file para verificar la existencia
        selectedImageFile = new File(imagePath);
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        //Revisa el boton que se dio click
        switch (v.getId()) {
            case R.id.b1:/*Seleccionar imagen*/
                //Lanza dialogo de selección de imagen
                this.launchSelectImageDialog();
                break;
            case R.id.b2:/*Enviar datos al Servidor*/
                //Valida datos y envia al servidor
                this.validateDataAndUpdate();
                break;
            case R.id.fecha_nacimiento: /*Campo de fecha nacimiento*/
                datePickerDialog.show();
                break;
        }

    }

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
                    case 1:/*Actualización de todos los datos*/
                        //Revisa si fue exitosa la actualización
                        if (Constants.ONE.equals(result.getValidationInd())) {/*Exitoso*/
                            //Revisa si no había imágen para actualización
                            if (selectedImageFile != null) {/*Hay imagen*/
                                //Si hay imagen seleccionada, la actualiza
                                SessionData.getInstance().executeServiceRV(2,
                                        getResources().getString(R.string.image_uploadUserImage),
                                        PerfilActivity.this.createParametersMap("userId",
                                                SessionData.getInstance().getUserId()),
                                        selectedImageFile);
                            } else {/*No hay imagen*/
                                //Navega hacia el menú principal de nuevo
                                Intent intent = new Intent(PerfilActivity.this, MenuPrincipalActivity.class);
                                //Navega
                                startActivity(intent);
                                //Finaliza Actividad
                                finish();
                            }
                        }
                        break;
                    case 2: /*Actualización de la imagen del usuario*/
                        //Navega hacia el menú principal de nuevo
                        Intent intent = new Intent(PerfilActivity.this, MenuPrincipalActivity.class);
                        //Navega
                        startActivity(intent);
                        //Finaliza Actividad
                        finish();
                        break;
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
                    case 1:/*Busqueda de paises*/
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
                        adapter = new ArrayAdapter<Object>(PerfilActivity.this,
                                android.R.layout.simple_dropdown_item_1line, data);
                        pais.setAdapter(adapter);
                        //Umbral
                        pais.setThreshold(1);
                        //Notifica
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:/*Busqueda de ciudades*/
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
                        adapter = new ArrayAdapter<Object>(PerfilActivity.this,
                                android.R.layout.simple_dropdown_item_1line, data);
                        ciudad.setAdapter(adapter);
                        //Umbral
                        ciudad.setThreshold(1);
                        //Notifica
                        adapter.notifyDataSetChanged();
                        break;
                    case 3: /*Llega nombre del pais*/
                        lsPais = (List<Csptpais>) result;
                        if (lsPais.size() > 0) {
                            //Asigna pais
                            selectedPais = lsPais.get(0);
                            //Asigna nombre al campo
                            pais.setText(selectedPais.getPaidpaiaf());
                        }
                        break;
                    case 4: /*Llega nombre de ciudad*/
                        lsCiudad = (List<Cspciuda>) result;
                        if (lsCiudad.size() > 0) {
                            //Asigna ciudad
                            selectedCiudad = lsCiudad.get(0);
                            //Asigna nombre al campo
                            ciudad.setText(selectedCiudad.getCiunciuaf());
                        }
                        break;
                }

            }
        });
    }

    @Override
    public void receiveServerCallResult(final int serviceId, final String service,
                                        final Bitmap result) {
        //Super
        super.receiveServerCallResult(serviceId, service, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (serviceId) {
                    case 5:/*Llega imagen del usuario*/
                        //Asigna
                        if (result != null) {
                            //Asigna bitmap a la imagen
                            userImage.setImageBitmap(result);
                        }
                        break;
                }
            }
        });
    }

    /**
     * Inicia campos de la pantalla
     */

    private void initScreen() {
        //Obtiene usuario
        Usmusuar usuar = SessionData.getInstance().getUsuarObject();
        //nombre
        name.setText(usuar.getUsunusuaf());
        //Apellido
        lastname.setText(usuar.getUsuapelaf());
        //Fecha de nacimiento
        fecha_nacimiento.setText(DateUtils.getDateString(usuar.getUsufnacff()));
        //Pais
        if (!Constants.BLANKS.equals(usuar.getPaicpaiak())) {
            //Obtiene Nombre del pais
            SessionData.getInstance().executeServiceList(3,
                    getResources().getString(R.string.basic_getCountries),
                    this.createParametersMap("countryCode", usuar.getPaicpaiak()),
                    new TypeToken<List<Csptpais>>() {
                    }.getType()
            );
        }
        //Ciudad
        if (!Constants.BLANKS.equals(usuar.getCiucciuak())) {
            //Obtiene Nombre del pais
            SessionData.getInstance().executeServiceList(4,
                    getResources().getString(R.string.basic_getCities),
                    this.createParametersMap("countryCode", usuar.getPaicpaiak(), "cityCode", usuar.getCiucciuak()),
                    new TypeToken<List<Cspciuda>>() {
                    }.getType()
            );
        }
        //Genero
        if (Constants.ONE.equals(usuar.getUsugenpvf())) {/*Mujer*/
            mujer.setChecked(true);
        } else if (Constants.TWO.equals(usuar.getUsugenpvf())) {/*Hombre*/
            hombre.setChecked(true);
        }
        //Soy Vegano
        if (Constants.ONE.equals(usuar.getUsuvegasf())) {
            soy_vegano.setChecked(true);
        }
        //Hobbies
        hobbies.setText(usuar.getUsuaficaf());
        //Imagen usuario
        if (!Constants.BLANKS.equals(usuar.getUsufotoaf())) {/*Hay imagen*/
            //Obtiene la imagen del usuario
            SessionData.getInstance().executeServiceImage(5,
                    getResources().getString(R.string.image_downloadImage),
                    this.createParametersMap("imagePath", usuar.getUsufotoaf()));
        }
    }

    /**
     * Valida los datos ingresados y actualiza
     */
    private boolean validateDataAndUpdate() {
        //Valida los campos
        //Nombre
        if (Constants.BLANKS.equals(name.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.nombre).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Apellido
        if (Constants.BLANKS.equals(lastname.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.apellido).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Pais
        if (selectedPais == null) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.pais).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Ciuddad
        if (selectedCiudad == null) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.ciudad).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Genero
        if (!hombre.isChecked() && !mujer.isChecked()) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.genero).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Set loading icon true
        this.setShowLoadingIcon(true);
        //Obtiene objeto de usuario
        Usmusuar usuar = SessionData.getInstance().getUsuarObject();
        //Envía actualización al servidor
        SessionData.getInstance().executeServiceRV(1,
                getResources().getString(R.string.user_updateUser),
                this.createParametersMap("userId", SessionData.getInstance().getUserId(),
                        "userId", SessionData.getInstance().getUserId(),
                        "userName", name.getText().toString().trim(),
                        "userLastName", lastname.getText().toString().trim(),
                        "email", usuar.getUsuemaiaf(),
                        "password", usuar.getUsupassaf(),
                        "dateOfBirth", DateUtils.getDateStringYYYYMMDD(usuar.getUsufnacff()),
                        "countryCode", selectedPais.getPaicpaiak(),
                        "cityCode", selectedCiudad.getId().getCiucciuak(),
                        "isVegan", soy_vegano.isChecked() ? Constants.ONE : Constants.ZERO,
                        "hobbies", hobbies.getText().toString().trim(),
                        "gender", mujer.isChecked() ? Constants.ONE : Constants.TWO));
        //Finaliza ok
        return true;
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
    }

    /**
     * Obtiene Lista de nombres de paises
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
                SessionData.getInstance().executeServiceList(2, getResources().getString(R.string.basic_getCities),
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
            SessionData.getInstance().executeServiceList(1, getResources().getString(R.string.basic_getCountries),
                    params, new TypeToken<List<Csptpais>>() {
                    }.getType());
        }
    }
}

