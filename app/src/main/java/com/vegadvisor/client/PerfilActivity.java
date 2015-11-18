package com.vegadvisor.client;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.vegadvisor.client.util.VegAdvisorActivity;

public class PerfilActivity extends VegAdvisorActivity implements View.OnClickListener {

    private ImageView ivImage;

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
