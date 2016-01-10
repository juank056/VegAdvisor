package com.vegadvisor.client;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import com.google.gson.reflect.TypeToken;
import com.vegadvisor.client.bo.Fomhilfo;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.List;

public class ForoActivity extends VegAdvisorActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    /**
     * Texto de busqueda del hilo
     */
    private EditText busqueda;
    // Lista Foro
    private ListView listaForo;

    //Lista objetos de foro
    private List<Fomhilfo> listHilos;

    /**
     * Boton crear hilo
     */
    private Button btn_crearHilo;

    /**
     * @param savedInstanceState Instancia
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Lista Foro del layout
        listaForo = (ListView) findViewById(R.id.listaForo);
        listaForo.setOnItemClickListener(this);
        //Botón crear nuevo hilo del foro
        findViewById(R.id.b1).setOnClickListener(this);
        btn_crearHilo = (Button) findViewById(R.id.btn_crearHilo);
        btn_crearHilo.setOnClickListener(this);
        busqueda = (EditText) findViewById(R.id.busqueda);
        //Si no hay usuario esconde boton de adicionar hilo foro
        if (!SessionData.getInstance().isUser()) {
            btn_crearHilo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Obtiene hilo seleccionado
        Fomhilfo hilo = listHilos.get(position);
        //Si el hilo existe
        if (hilo != null) {
            //Asigna los datos del objeto hilo a la sesión
            SessionData.getInstance().setForumThread(hilo);
            //Navega hacia la actividad detalle de dicho hilo
            Intent intent = new Intent(ForoActivity.this, DetalleHiloActivity.class);
            //Inicia la navegación
            startActivity(intent);
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            // Navega a actividad de creación de hilos del foro
            case R.id.btn_crearHilo:
                intent = new Intent(ForoActivity.this, CrearHiloActivity.class);
                startActivity(intent);
                break;
            // Actividad de búsquedad de hilo
            case R.id.b1:
                SessionData.getInstance().executeServiceList(381,
                        getResources().getString(R.string.forum_findForumThreads),
                        this.createParametersMap("clue", busqueda.getText().toString().trim()), new TypeToken<List<Fomhilfo>>() {
                        }.getType());
                break;
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
                    case 381:/*Lista de hilos del foro*/
                        //Lista de hilos
                        listHilos = (List<Fomhilfo>) result;
                        //Para incluir elementos de la lista
                        ArrayAdapter adapter = new ArrayAdapter(ForoActivity.this,
                                android.R.layout.simple_list_item_2, listHilos) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                TwoLineListItem row;
                                if (convertView == null) {
                                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    row = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_2, null);
                                } else {
                                    row = (TwoLineListItem) convertView;
                                }
                                Fomhilfo hilo = listHilos.get(position);
                                //Titulo del foro
                                String title = hilo.getHiftituaf() + Constants.BLANK_SPACE + Constants.LEFT_PARENTHESIS +
                                        hilo.getHiffregff() + Constants.BLANK_SPACE + hilo.getUserName()
                                        + Constants.RIGHT_PARENTHESIS;
                                row.getText1().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                row.getText2().setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                                row.getText1().setText(title);
                                row.getText2().setText(hilo.getHifdetaaf());
                                row.getText1().setTextColor(Color.BLUE);
                                row.getText2().setTextColor(Color.DKGRAY);
                                return row;
                            }
                        };
                        //Incluye nuevos registros
                        listaForo.setAdapter(adapter);
                        //Notifica
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        });
    }
}
