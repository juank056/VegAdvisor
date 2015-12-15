package com.vegadvisor.client;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CrearEventoActivity extends VegAdvisorActivity implements View.OnClickListener {

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
     * Campos de autocomplete
     */
    private AutoCompleteTextView tipo_evento;

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

    private EditText nombreEvento,fecha,comienzo,finalizacion,direccion,telefono,descripcion;

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
        nombreEvento=(EditText)findViewById(R.id.nombreEvento);
        fecha=(EditText)findViewById(R.id.fecha_evento);
        comienzo=(EditText)findViewById(R.id.comienzo_evento);
        finalizacion=(EditText)findViewById(R.id.finalizacion_evento);
        direccion=(EditText)findViewById(R.id.direccion);
        telefono=(EditText)findViewById(R.id.telefono);
        descripcion=(EditText)findViewById(R.id.descripcion_evento);
        //Texto por defecto de apertura y cierre
        comienzo.setText(Constants.DEF_OPEN_TIME);
        finalizacion.setText(Constants.DEF_CLOSE_TIME);
        comienzo.setOnClickListener(this);
        finalizacion.setOnClickListener(this);

        tipo_evento = (AutoCompleteTextView) findViewById(R.id.tipo_evento);

        //CONTINUARRRRRRRRRR!!
    }

    @Override
    public void onClick(View v) {

    }
}
