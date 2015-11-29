package com.vegadvisor.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.vegadvisor.client.bo.Esmestab;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.ArrayList;
import java.util.List;

public class EstablecimientosActivity extends VegAdvisorActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    /**
     * Lista de establecimientos
     */
    private ListView listaEstab;

    /**
     * Lista de objetos de establecimientos
     */
    private List<Esmestab> lsEstab;


    /**
     * Create
     *
     * @param savedInstanceState Instancia
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establecimientos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Lista establecimientos
        listaEstab = (ListView) findViewById(R.id.listaEstab);
        listaEstab.setOnItemClickListener(this);
        //Boton
        findViewById(R.id.b1).setOnClickListener(this);
        //Obtiene id de usuario
        String userId = SessionData.getInstance().getUserId();
        //Obtiene establecimientos
        SessionData.getInstance().executeServiceList(1,
                getResources().getString(R.string.establishment_getUserEstablishments),
                this.createParametersMap("userId", userId), new TypeToken<List<Esmestab>>() {
                }.getType());
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        //Intent hacia crear establecimiento
        Intent intent = new Intent(EstablecimientosActivity.this, CrearEstabActivity.class);
        //inicia
        startActivity(intent);
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
                    List<String> listEstabScreen = new ArrayList<String>();
                    //Recorre lista de establecimientos recibida
                    for (Esmestab estab : lsEstab) {
                        listEstabScreen.add(estab.getEstnestaf());
                    }
                    //Para incluir elementos de la lista
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EstablecimientosActivity.this,
                            android.R.layout.simple_list_item_1, listEstabScreen);
                    //Incluye nuevos peers
                    listaEstab.setAdapter(adapter);
                    //Notifica
                    adapter.notifyDataSetChanged();
                }
            }
        });
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
            Intent intent = new Intent(EstablecimientosActivity.this, DetalleEstabActivity.class);
            //Inicia
            startActivity(intent);
        }
    }
}
