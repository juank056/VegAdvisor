package com.vegadvisor.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.vegadvisor.client.bo.Fodreshi;
import com.vegadvisor.client.bo.Fomhilfo;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.List;

public class DetalleHiloActivity extends VegAdvisorActivity implements View.OnClickListener {

    //Datos del hilo
    private TextView nombreHilo, descripcion;

    //Lista de interacciones de los usuarios sobre un hilo
    private ListView listaRespuestas;

    //Lista respuestas de la BD
    private List<Fodreshi> respuestas_hilo_BD;

    //Objeto hilo de foro
    private Fomhilfo hilo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_hilo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listaRespuestas = (ListView)findViewById(R.id.listaRespuesta);

        nombreHilo = (TextView)findViewById(R.id.foro_newthread_name);
        descripcion = (TextView)findViewById(R.id.detalle_hilo_descripcion_content);
        findViewById(R.id.foro_btn_responder).setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        Intent intent = new Intent(DetalleHiloActivity.this, RespuestaHiloActivity.class);
        startActivity(intent);
    }

}
