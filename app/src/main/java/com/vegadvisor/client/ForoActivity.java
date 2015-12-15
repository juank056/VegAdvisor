package com.vegadvisor.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.vegadvisor.client.bo.Esmestab;
import com.vegadvisor.client.bo.Fomhilfo;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.ArrayList;
import java.util.List;

public class ForoActivity extends VegAdvisorActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    // Lista Foro
    private ListView listaForo;

    //Lista objetos de foro
    private List<Fomhilfo> listHilos;

    //Los 4 atributos del hilo que se van a mostrar en la pantalla: título, fecha, usuario y detalle
    private Object[] HiloScreen = new Object[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Lista Foro del layout
        listaForo = (ListView)findViewById(R.id.listaForo);
        listaForo.setOnItemClickListener(this);
        //Botón crear nuevo hilo del foro
        findViewById(R.id.b1).setOnClickListener(this);
        //Trae hilos de foro de la base de datos

        // PENDIENTEEEEEEEEEEEEEEEEEEEEEE
        //SessionData.getInstance().executeServiceList(381,);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Obtiene hilo seleccionado
        Fomhilfo hilo = listHilos.get(position);
        //Si el hilo existe
        if(hilo!=null){
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
        // Navega a actividad de creación de hilos del foro
        Intent intent = new Intent(ForoActivity.this, CrearHiloActivity.class);
        //Inicia la navegación
        startActivity(intent);
    }

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
                    listHilos = (List<Fomhilfo>) result;
                    //Lista de hilos (pantalla)
                    List<Object> listForoScreen = new ArrayList<Object>();
                    //Recorre lista de establecimientos recibida
                    for (Fomhilfo hilo : listHilos) {
                        //Obtiene el usuario creador del hilo
                        HiloScreen[0]=hilo.getUsucusuak();
                        //Obtiene fecha de creación
                        HiloScreen[1]=hilo.getHiffregff();
                        //Obtiene hora de creación
                        HiloScreen[2]=hilo.getHifhoratf();
                        //Obtiene titulo Hilo
                        HiloScreen[3]=hilo.getHiftituaf();
                        //Obtiene detalle del hilo
                        HiloScreen[4]=hilo.getHifdetaaf();
                        //Introduce el objeto en la lista
                        listForoScreen.add(HiloScreen);
                    }

                    /* Cambio Git ...

                     */
                    //Para incluir elementos de la lista
                   /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(ForoActivity.this,
                            android.R.layout.simple_list_item_1, listForoScreen);
                    //Incluye nuevos peers
                    listaForo.setAdapter(adapter);
                    //Notifica
                    adapter.notifyDataSetChanged();*/
                }
            }
        });


}}
