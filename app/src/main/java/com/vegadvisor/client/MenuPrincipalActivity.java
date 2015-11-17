package com.vegadvisor.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class MenuPrincipalActivity extends VegAdvisorActivity {

    //Imagen de prueba
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Imagen de prueba
        image = (ImageView) findViewById(R.id.myimage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Busca imagen en el servidor
        Map<String, String> params = new HashMap<>();
        params.put(Constants.IMAGE_KEY, "VEG-USER-IMAGE-jc.mesa.jpeg");
        SessionData.getInstance().executeServiceImage(1, "/userImage", params);
    }
}
