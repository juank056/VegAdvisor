package com.vegadvisor.client;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuPrincipalActivity extends VegAdvisorActivity implements View.OnClickListener {


    /**
     * @param savedInstanceState Instancia salvada
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Botones
        findViewById(R.id.b1).setOnClickListener(this);
        findViewById(R.id.b2).setOnClickListener(this);
        findViewById(R.id.b3).setOnClickListener(this);
        findViewById(R.id.b4).setOnClickListener(this);
        findViewById(R.id.b5).setOnClickListener(this);
        findViewById(R.id.b6).setOnClickListener(this);
        findViewById(R.id.b7).setOnClickListener(this);
        findViewById(R.id.b8).setOnClickListener(this);
        findViewById(R.id.b9).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        //Intent
        Intent intent = null;
        switch (v.getId()) {
            case R.id.b1: /*Localizador*/
                Toast.makeText(getApplicationContext(), "No Implementado!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.b2: /*Cercano a mi*/
                Toast.makeText(getApplicationContext(), "No Implementado!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.b3: /*Perfil*/
                Toast.makeText(getApplicationContext(), "No Implementado!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.b4: /*Eventos*/
                Toast.makeText(getApplicationContext(), "No Implementado!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.b5: /*Foro*/
                Toast.makeText(getApplicationContext(), "No Implementado!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.b6: /*Chat*/
                Toast.makeText(getApplicationContext(), "No Implementado!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.b7: /*Establecimientos*/
                //Navegar hacia
                intent = new Intent(MenuPrincipalActivity.this, EstablecimientosActivity.class);
                break;
            case R.id.b8: /*Contacto*/
                Toast.makeText(getApplicationContext(), "No Implementado!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.b9: /*Salir*/
                //Limpia datos de sesion
                SessionData.getInstance().cleanData();
                //Navegar a la pantalla principal
                intent = new Intent(MenuPrincipalActivity.this, InicioAplicacionActivity.class);
                //Flags para limpiar stack
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
        }
        //Realiza navegacion
        if (intent != null) { /*Revisa si hay un intent*/
            //navega
            startActivity(intent);
        }

    }
}
