package com.vegadvisor.client;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TwoLineListItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.reflect.TypeToken;
import com.vegadvisor.client.bo.Esmestab;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.ArrayList;
import java.util.List;

public class LocalizadorActivity extends VegAdvisorActivity implements View.OnClickListener, AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /**
     * Texto de busqueda del establecimiento
     */
    private EditText busqueda;

    /**
     * Lista de establecimientos
     */
    private ListView listaEstab;

    /**
     * Lista de objetos de establecimiento
     */
    private List<Esmestab> lsEstab;

    /**
     * Google api client
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Latitud y longitud
     */
    private double latitud, longitud;

    /**
     * @param savedInstanceState Instancia
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizador);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Obtiene campos de pantalla
        busqueda = (EditText) findViewById(R.id.busqueda);
        listaEstab = (ListView) findViewById(R.id.listaEstab);
        listaEstab.setOnItemClickListener(this);
        //Boton de busqueda
        findViewById(R.id.b1).setOnClickListener(this);
        //Inicia google api client
        buildGoogleApiClient();
    }

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    public void receiveServerCallResult(final int serviceId, final String service, final List<?> result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Revisa que haya lista de resultado
                if (result != null) {
                    //Lista de establecimientos
                    lsEstab = (List<Esmestab>) result;
                    //Lista de establecimientos (pantalla)
                    final List<String[]> listEstabScreen = new ArrayList<>();
                    //Recorre lista de establecimientos recibida
                    for (Esmestab estab : lsEstab) {
                        listEstabScreen.add(new String[]{estab.getEstnestaf(), estab.getEstdestaf()});
                    }
                    //Para incluir elementos de la lista
                    ArrayAdapter adapter = new ArrayAdapter(LocalizadorActivity.this,
                            android.R.layout.simple_list_item_2, listEstabScreen) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            TwoLineListItem row;
                            if (convertView == null) {
                                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                row = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_2, null);
                            } else {
                                row = (TwoLineListItem) convertView;
                            }
                            String[] data = listEstabScreen.get(position);
                            row.getText1().setText(data[0]);
                            row.getText2().setText(data[1]);
                            row.getText1().setTextColor(Color.GREEN);
                            row.getText2().setTextColor(Color.DKGRAY);
                            return row;
                        }
                    };
                    //Incluye nuevos peers
                    listaEstab.setAdapter(adapter);
                    //Notifica
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        //Se le ha dado a buscar
        searchEstablishments();
    }

    /**
     * Busca establecimientos
     */
    private void searchEstablishments() {
        //Obtiene establecimientos
        SessionData.getInstance().executeServiceList(181,
                getResources().getString(R.string.establishment_findEstablishments),
                this.createParametersMap("clue", busqueda.getText().toString().trim(),
                        "ratio", Constants.DEF_SEARCH_RATIO,
                        "latitud", Constants.BLANKS + latitud,
                        "longitud", Constants.BLANKS + longitud), new TypeToken<List<Esmestab>>() {
                }.getType());
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Obtiene establecimiento seleccionado
        Esmestab estab = lsEstab.get(position);
        if (estab != null) {/*Hay establecimiento*/
            //Asina establecimiento a datos de sesion
            SessionData.getInstance().setUserEstab(estab);
            //Crea intent para ir a detalle establecimiento
            Intent intent = new Intent(LocalizadorActivity.this, DetalleEstabActivity.class);
            //Inicia
            startActivity(intent);
        }
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
