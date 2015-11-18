package com.vegadvisor.client;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends VegAdvisorActivity implements View.OnClickListener {

    private ImageView ivImage;

    /**
     * File a enviar al servidor
     */
    private File file;

    /**
     * @param savedInstanceState OnCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //imagen de prueba
        ivImage = (ImageView) findViewById(R.id.image);
        findViewById(R.id.b1).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Se engarga de procesar el resultado de cargar una imagen
     *
     * @param imageBitmap Bitmap de la imagen cargada
     * @param imagePath   Ruta de la imagen cargada
     */
    @Override
    public void processImageSelectedResponse(Bitmap imageBitmap, String imagePath) {
        super.processImageSelectedResponse(imageBitmap, imagePath);
        //Asigna imagen
        ivImage.setImageBitmap(imageBitmap);
        //Crea file para verificar la existencia
        File file = new File(imagePath);
        //Envia imagen al servidor
        Map<String, String> params = new HashMap<>();
        params.put("userId", "clau");
        SessionData.getInstance().executeServiceRV(1, getResources().getString(R.string.image_uploadUserImage), params, file);
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        this.launchSelectImageDialog();
    }

}
