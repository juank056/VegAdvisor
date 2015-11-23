package com.vegadvisor.client;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.vegadvisor.client.bo.Csptpais;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerfilActivity extends VegAdvisorActivity implements View.OnClickListener {

    /**
     * Imagen
     */
    private ImageView ivImage;


    /**
     * Texto de autocompletar
     */
    private AutoCompleteTextView texto;

    /**
     * Lista de paises de selección
     */
    private List<Csptpais> lsPais;

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
        //imagen de prueba
        ivImage = (ImageView) findViewById(R.id.image);
        findViewById(R.id.b1).setOnClickListener(this);
        //Autocomplete text
        texto = (AutoCompleteTextView) findViewById(R.id.texto);
        texto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*Nada*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Ejecuta metodo para obtener opciones
                sendDataToServer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*Nada*/
            }
        });
        texto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Obtiene pais seleccionado
                Csptpais pais = lsPais.get(position);
                Log.d(Constants.DEBUG, "PAIS: " + pais.getPaicpaiak() + " - " + pais.getPaidpaiaf());
            }
        });
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
        super.processImageSelectedResponse(imageBitmap, imagePath);
        //Asigna imagen
        ivImage.setImageBitmap(imageBitmap);
        //Crea file para verificar la existencia
        File file = new File(imagePath);
        //Envia imagen al servidor
        Map<String, String> params = new HashMap<>();
        params.put("userId", SessionData.getInstance().getUserId());
        SessionData.getInstance().executeServiceRV(1, getResources().getString(R.string.image_uploadUserImage), params, file);
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        this.launchSelectImageDialog();
    }


    private void sendDataToServer() {
        //Obtiene clue de pais
        String clue = texto.getText().toString().trim();
        Log.d(Constants.DEBUG, "KEY LISTENER: " + clue);
        if (clue.length() > 1) {
            Map<String, String> params = new HashMap<>();
            params.put("clue", clue);
            //Ejecuta servicio
            SessionData.getInstance().executeServiceList(1, getResources().getString(R.string.basic_getCountries),
                    params, new TypeToken<List<Csptpais>>() {
                    }.getType());
        }
    }

    /**
     * @param serviceId Id del servicio
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    @Override
    public void receiveServerCallResult(final int serviceId, final String service, final List<?> result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(Constants.DEBUG, "RESPUESTA A SERVICIO RECIBIDA: " + result);
                //Asigna lista de respuesta
                lsPais = (List<Csptpais>) result;
                String[] data = new String[result.size()];
                for (int i = 0; i < result.size(); i++) {
                    Csptpais pais = (Csptpais) result.get(i);
                    Log.d(Constants.DEBUG, "PAIS: " + pais);
                    data[i] = pais.getPaidpaiaf() + " (" + pais.getPaicpaiak() + ")";
                }

                ArrayAdapter<?> adapter = new ArrayAdapter<Object>(PerfilActivity.this,
                        android.R.layout.simple_dropdown_item_1line, data);
                texto.setAdapter(adapter);
                if (result.size() < 40)
                    texto.setThreshold(1);
                else
                    texto.setThreshold(2);
                adapter.notifyDataSetChanged();
            }
        });
    }
}

