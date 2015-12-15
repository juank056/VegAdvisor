package com.vegadvisor.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vegadvisor.client.bo.Evmevent;
import com.vegadvisor.client.bo.Fomhilfo;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.ArrayList;
import java.util.List;

public class EventosActivity extends VegAdvisorActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    //Lista eventos
    private ListView listEventos;

    //Lista eventos base datos
    private List<Evmevent> listEvBD;

    //Elemento de la listEventosScreen
    private Object EventoScreen = new Object[3];

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

        //Lanzador de evento de boton crear evento
        findViewById(R.id.b1).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        //Navega hacia actividad de creación de evento
        Intent intent = new Intent(EventosActivity.this, CrearEventoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

  /*  @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Obtiene evento seleccionado

        //REVISAAAAR GEEEET!!!

       Evmevent evento = listEventos.get(position);
        //Si existe
        if(evento!=null){
            //Guarda datos de evento en sesión
            SessionData.getInstance().setEvent(evento);
            //Navega a actividad de detalle de dicho evento
            Intent intent = new Intent(EventosActivity.this,DetalleEventoActivity.class);
            //Inicia la navegación
            startActivity(intent);
        }
    }
/*
    //Recibe respuesta de un llamado al server
    public void receiveServerCallResult(final int serviceId, final String service, final List<?> result) {
        //Super
        super.receiveServerCallResult(serviceId, service, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Revisa que haya lista de resultado
                if (result != null) {
                    //Lista de establecimientos
                   listEvBD = (List<Evmevent>) result;
                    //Lista de hilos (pantalla)
                    List<Object> listEventoScreen = new ArrayList<Object>();
                    //Recorre lista de establecimientos recibida
                    for (Evmevent evento : listEvBD) {
                        //Obtiene el usuario creador del evento
                        EventoScreen[0]=evento.getUsucusuak();
                        //Obtiene fecha de creación
                       // EventoScreen[1]=evento.getE();
                        //Obtiene hora de creación
                      //  EventoScreen[2]=evento.getHifhoratf();
                        //Obtiene titulo Hilo
                       // EventoScreen[3]=evento.getHiftituaf();
                        //Obtiene descripción del evento
                        EventoScreen[4]=evento.getEvedeveaf();
                        //Introduce el objeto en la lista
                        listEventoScreen.add(EventoScreen);
                    }
                    //Para incluir elementos de la lista
                   /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(ForoActivity.this,
                            android.R.layout.simple_list_item_1, listForoScreen);
                    //Incluye nuevos peers
                    listaForo.setAdapter(adapter);
                    //Notifica
                    adapter.notifyDataSetChanged();
                }
            }
        });*/
}
