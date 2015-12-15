package com.vegadvisor.client;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.VegAdvisorActivity;

public class ContactoActivity extends VegAdvisorActivity implements View.OnClickListener {

    // Text Views de la pantalla
    private TextView telefonoTitle, telefonoUno, telefonoDos, emailTitle, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        telefonoTitle = (TextView)findViewById(R.id.phone_title);
        telefonoUno = (TextView)findViewById(R.id.phone1);
        telefonoDos = (TextView)findViewById(R.id.phone2);
        emailTitle = (TextView)findViewById(R.id.email_title);
        email = (TextView)findViewById(R.id.email_contact);

        //Teléfono Uno y Teléfono Dos son clickables
        telefonoUno.setOnClickListener(this);
        telefonoDos.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        //Comprobación de View clickada
        switch(v.getId()){
            //Si clicó teléfono 1
            case R.id.phone1:
                //Intent para lanzar aplicación nativa de llamada
                intent = new Intent(Intent.ACTION_CALL, Uri.parse(Constants.TEL + telefonoUno.getText().toString().trim()));
                //Intenta llamada al primer núm de contacto
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                }
                break;
            //Si clicó teléfono 2
            case R.id.phone2:
                //Intent para lanzar aplicación nativa de llamada
                intent = new Intent(Intent.ACTION_CALL, Uri.parse(Constants.TEL + telefonoDos.getText().toString().trim()));
                //Intenta llamada al primer núm de contacto
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                }
                break;
            //Si clickó teléfono 2
            case R.id.email_contact:
                //Intent para lanzar aplicación cliente email
                intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, email.getText());
                //Lanza el intent
                startActivity(intent);
                break;
        }
    }
}
