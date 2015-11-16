package com.vegadvisor.client.util;


import android.support.v7.app.AppCompatActivity;

import com.vegadvisor.client.bo.ReturnValidation;

import java.io.InputStream;
import java.util.List;

/**
 * Clase que representa una actividad de VegAdvisor
 * Created by JuanCamilo on 12/11/2015.
 */
public abstract class VegAdvisorActivity extends AppCompatActivity {

    /**
     * Sobre-escribe método on resume
     */
    @Override
    protected void onResume() {
        super.onResume();
        //Asigna actividad a los datos de sesión
        SessionData.getInstance().setActivity(this);
    }

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    public abstract void receiveServerCallResult(final int serviceId, final String service, final ReturnValidation result);

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    public abstract void receiveServerCallResult(final int serviceId, final String service, final List<?> result);

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    public abstract void receiveServerCallResult(final int serviceId, final String service, final InputStream result);
}
