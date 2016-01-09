package com.vegadvisor.client;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
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
import com.vegadvisor.client.bo.Csptiest;
import com.vegadvisor.client.bo.Csptieve;
import com.vegadvisor.client.bo.Esmestab;
import com.vegadvisor.client.bo.Evmevent;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.List;

public class DetalleEventoActivity extends VegAdvisorActivity {

    //Mapa
    private GoogleMap googleMap;

    //Fragmento del mapa
    private MapFragment mapFragment;

    //Google API Client
    private GoogleApiClient mGoogleApiClient;

    //Campos TextView de la pantalla
    private TextView direccion, horaComienzo, horaFin, fecha, descripcion;

    //Campos de autocomplete
    private AutoCompleteTextView tipoEvento;

    //Layout de imagenes
    private LinearLayout imagenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_evento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    // Inicia elementos de la pantalla
   /* public void InitScreen(){
        Evmevent evento = SessionData.getInstance().getEvent();
        //Tipo establecimiento
        SessionData.getInstance().executeServiceList(341, getResources().getString(R.string.basic_getEventTypes),
                createParametersMap("eventType", Constants.BLANKS + evento.getTevctevnk()), new TypeToken<List<Csptieve>>() {
                }.getType());
        descripcion = setText(evento.getEvedeveaf());
        horaComienzo = setText(evento.get);
        horaFin = ;


    }*/

}