package com.vegadvisor.client;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TwoLineListItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.reflect.TypeToken;
import com.vegadvisor.client.bo.Esmestab;
import com.vegadvisor.client.bo.Evmevent;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.DateUtils;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.List;

public class EventosActivity extends VegAdvisorActivity implements View.OnClickListener, AdapterView.OnItemClickListener
        , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Texto de busqueda de evento
    private EditText busqueda;

    //Lista eventos
    private ListView listEventos;

    //Lista eventos base datos
    private List<Evmevent> listEvBD;

    /**
     * Google api client
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Latitud y longitud
     */
    private double latitud, longitud;

    /**
     * Boton crear evento
     */
    private Button btn_CrearEvento;

    /**
     * @param savedInstanceState Instancia
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Selecciona en pantalla evento de la lista
        listEventos = (ListView) findViewById(R.id.listaEvento);
        listEventos.setOnItemClickListener(this);

        //Botón crear nuevo evento y busqueda
        findViewById(R.id.b1).setOnClickListener(this);
        btn_CrearEvento = (Button) findViewById(R.id.btn_CrearEvento);
        btn_CrearEvento.setOnClickListener(this);
        busqueda = (EditText) findViewById(R.id.busqueda);
        //Inicia google api client
        buildGoogleApiClient();
        //Si no hay usuario esconde boton de adicionar evento
        if (!SessionData.getInstance().isUser()) {
            btn_CrearEvento.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            // Navega a actividad de creación de evento
            case R.id.btn_CrearEvento:
                intent = new Intent(EventosActivity.this, CrearEventoActivity.class);
                startActivity(intent);
                break;
            // Actividad de búsquedad de evento
            case R.id.b1:
                SessionData.getInstance().executeServiceList(301,
                        getResources().getString(R.string.event_findEvents),
                        this.createParametersMap("userId", SessionData.getInstance().isUser() ?
                                        SessionData.getInstance().getUserId() : Constants.BLANKS,
                                "clue", busqueda.getText().toString().trim(),
                                "ratio", Constants.DEF_SEARCH_RATIO,
                                "latitud", Constants.BLANKS + latitud,
                                "longitud", Constants.BLANKS + longitud), new TypeToken<List<Evmevent>>() {
                        }.getType());
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Obtiene evento seleccionado
        Evmevent evento = listEvBD.get(position);
        //Si el evento existe
        if (evento != null) {
            //Asigna los datos del objeto evento a la sesión
            SessionData.getInstance().setEvent(evento);
            //Navega hacia la actividad detalle de dicho evento
            Intent intent = new Intent(EventosActivity.this, DetalleEventoActivity.class);
            //Inicia la navegación
            startActivity(intent);
        }

    }

    @SuppressWarnings("unchecked")
    public void receiveServerCallResult(final int serviceId, final String service,
                                        final List<?> result) {
        //Super
        super.receiveServerCallResult(serviceId, service, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Revisa id de servicio ejecutado
                switch (serviceId) {
                    case 301:/*Lista de eventos*/
                        //Lista de eventos
                        listEvBD = (List<Evmevent>) result;
                        //Para incluir elementos de la lista
                        ArrayAdapter adapter = new ArrayAdapter(EventosActivity.this,
                                android.R.layout.simple_list_item_2, listEvBD) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                TwoLineListItem row;
                                if (convertView == null) {
                                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    row = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_2, null);
                                } else {
                                    row = (TwoLineListItem) convertView;
                                }
                                Evmevent evento = listEvBD.get(position);
                                //Titulo del evento: T
                                String title = evento.getEventTypeName() + Constants.BLANK_SPACE + Constants.LEFT_PARENTHESIS +
                                        DateUtils.getDateString(evento.getId().getEvefevefk()) +
                                        Constants.BLANK_SPACE + evento.getEvehoratf() +
                                        Constants.BLANK_SPACE + getResources().getString(R.string.participantes) + evento.getEvenparnf() +
                                        Constants.RIGHT_PARENTHESIS;
                                row.getText1().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                row.getText2().setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                                row.getText1().setText(title);
                                row.getText2().setText(evento.getEvedeveaf());
                                row.getText1().setTextColor(Color.BLUE);
                                row.getText2().setTextColor(Color.DKGRAY);
                                return row;
                            }
                        };
                        //Incluye nuevos registros
                        listEventos.setAdapter(adapter);
                        //Notifica
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        });
    }

    /*******************************
     * GPS
     * ===============================
     */

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
