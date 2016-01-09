package com.vegadvisor.client;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TwoLineListItem;

import com.google.gson.reflect.TypeToken;
import com.vegadvisor.client.bo.Evmevent;
import com.vegadvisor.client.bo.Fomhilfo;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.ArrayList;
import java.util.List;

public class EventosActivity extends VegAdvisorActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    // Texto de busqueda del hilo
    private EditText busqueda;

    //Lista eventos
    private ListView listEventos;

    //Lista eventos base datos
    private List<Evmevent> listEvBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Selecciona en pantalla evento de la lista
        listEventos=(ListView)findViewById(R.id.listaEvento);
        listEventos.setOnItemClickListener(this);

        //Botón crear nuevo evento y busqueda
        findViewById(R.id.b1).setOnClickListener(this);
        findViewById(R.id.btn_CrearEvento).setOnClickListener(this);
        busqueda = (EditText) findViewById(R.id.busqueda);

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
                        this.createParametersMap("clue", busqueda.getText().toString().trim()), new TypeToken<List<Evmevent>>() {
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
                                //Titulo del foro: Tipo evento (Fecha? Hora Ciudad?)
                                String title = evento.getTevctevnk() + Constants.BLANK_SPACE + Constants.LEFT_PARENTHESIS +
                                        /*evento.get + */ Constants.BLANK_SPACE + evento.getEvehoratf() + /*evento.get
                                        + */Constants.RIGHT_PARENTHESIS;
                                row.getText1().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                row.getText2().setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                                row.getText1().setText(title);
                                row.getText2().setText(evento.getEvedeveaf());
                                row.getText1().setTextColor(Color.BLUE);
                                row.getText2().setTextColor(Color.DKGRAY);
                                return row;
                            }
                         };
                        if (listEventos == null)
                            Log.d(Constants.DEBUG, "LISTA  ES NULLA");
                        if (adapter == null)
                            Log.d(Constants.DEBUG, "ADAPTER ES NULL");
                        //Incluye nuevos registros
                        listEventos.setAdapter(adapter);
                        //Notifica
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        });
    }

}
