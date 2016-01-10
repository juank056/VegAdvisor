package com.vegadvisor.client;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.vegadvisor.client.bo.Fodreshi;
import com.vegadvisor.client.bo.Fomhilfo;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.DateUtils;
import com.vegadvisor.client.util.SessionData;
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

    /**
     * Boton crear evento
     */
    private Button foro_btn_responder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_hilo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listaRespuestas = (ListView) findViewById(R.id.listaRespuesta);
        nombreHilo = (TextView) findViewById(R.id.foro_newthread_name);
        descripcion = (TextView) findViewById(R.id.detalle_hilo_descripcion_content);
        foro_btn_responder = (Button) findViewById(R.id.foro_btn_responder);
        foro_btn_responder.setOnClickListener(this);
        //Si no hay usuario esconde boton de responder
        if (!SessionData.getInstance().isUser()) {
            foro_btn_responder.setVisibility(View.GONE);
        }
        //Asigna respuestas
        createResponseList();
    }

    /**
     * Asigna lista de respuestas de hilo de foro
     */
    @SuppressWarnings("unchecked")
    private void createResponseList() {
        //Objeto Fomhilfo
        Fomhilfo hilfo = SessionData.getInstance().getForumThread();
        //Nombre hilo
        nombreHilo.setText(hilfo.getHiftituaf());
        //Detalle
        descripcion.setText(hilfo.getHifdetaaf());
        //Lista de hilos
        respuestas_hilo_BD = hilfo.getResponses();
        //Para incluir elementos de la lista
        ArrayAdapter adapter = new ArrayAdapter(DetalleHiloActivity.this,
                android.R.layout.simple_list_item_2, respuestas_hilo_BD) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TwoLineListItem row;
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_2, null);
                } else {
                    row = (TwoLineListItem) convertView;
                }
                Fodreshi resp = respuestas_hilo_BD.get(position);
                //Titulo del foro
                String title = resp.getUserName() + Constants.BLANK_SPACE + Constants.LEFT_PARENTHESIS +
                        DateUtils.getDateString(resp.getRhffregff()) + Constants.BLANK_SPACE + resp.getRhfhoratf()
                        + Constants.RIGHT_PARENTHESIS;
                row.getText1().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                row.getText2().setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                row.getText1().setText(title);
                row.getText2().setText(resp.getRhfdetaaf());
                row.getText1().setTextColor(Color.BLACK);
                row.getText2().setTextColor(Color.DKGRAY);
                return row;
            }
        };
        //Incluye nuevos registros
        listaRespuestas.setAdapter(adapter);
        //Notifica
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(DetalleHiloActivity.this, RespuestaHiloActivity.class);
        startActivity(intent);
    }

}
