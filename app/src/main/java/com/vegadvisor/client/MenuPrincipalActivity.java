package com.vegadvisor.client;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.io.InputStream;
import java.util.List;

public class MenuPrincipalActivity extends VegAdvisorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param service Servicio que se ha llamado
     * @param result  Resultado de la ejecución
     */
    @Override
    public void receiveServerCallResult(final int serviceId, final String service, final ReturnValidation result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Respuesta Recibida: " + service + Constants.BLANK_SPACE + result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param service Servicio que se ha llamado
     * @param result  Resultado de la ejecución
     */
    @Override
    public void receiveServerCallResult(final int serviceId, final String service, final List<?> result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Respuesta Recibida: " + service + Constants.BLANK_SPACE + result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    @Override
    public void receiveServerCallResult(final int serviceId, final String service, final InputStream result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Respuesta Recibida: " + service + Constants.BLANK_SPACE + result, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
