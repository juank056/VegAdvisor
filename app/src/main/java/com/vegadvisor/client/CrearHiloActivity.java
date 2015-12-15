package com.vegadvisor.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.PasswordManager;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.HashMap;
import java.util.Map;

public class CrearHiloActivity extends VegAdvisorActivity implements View.OnClickListener {

    /* Encabezados: Título del Nuevo Hilo y Descripción */
    private TextView titleHeader,descriptionHeader;

    /* Contenido: Título del Nuevo Hilo y Descripción */
    private EditText titleThread, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_hilo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recupera información de TextViews del layout
        titleHeader = (TextView)findViewById(R.id.foro_newthread_title);
        descriptionHeader = (TextView)findViewById(R.id.foro_description_title);

        // Recupera información de EditTexts del layout
        titleThread = (EditText)findViewById(R.id.foro_newthread_name);
        description = (EditText)findViewById(R.id.foro_newthread_description);

        findViewById(R.id.foro_btn_sendnew).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (ValidaCampos()) {
            //Mapa de parámetros
            Map<String, String> parameters = new HashMap<>();
            //Asigna parametros
            //Obtiene usuario de la sesión
            parameters.put("userId", SessionData.getInstance().getUserId());
            //parameters.put("Date", name.getText().toString().trim());
            //parameters.put("Time", lastName.getText().toString().trim());
            parameters.put("title", titleThread.getText().toString().trim());
            parameters.put("description", description.getText().toString().trim());
            //Llama servicio para registrar el nuevo hilo en el foro
            SessionData.getInstance().executeServiceRV(1, getResources().getString(R.string.user_createUser), parameters);

            Intent intent = new Intent (CrearHiloActivity.this, ForoActivity.class);
            startActivity(intent);
        }
    }

    private boolean ValidaCampos() {

        //Comprueba que campo título del hilo del formulario no esté en blanco
        if (Constants.BLANKS.equals(titleThread.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.detalle_titulothread).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Comprueba que campo descripción del hilo del formulario no esté en blanco
        if (Constants.BLANKS.equals(description.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.detalle_descriptionThread).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param service Servicio que se ha llamado
     * @param result  Resultado de la ejecución
     */
    /*public void receiveServerCallResult(final int serviceId, final String service, final ReturnValidation result) {
        //Super
        super.receiveServerCallResult(serviceId, service, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Revisa respuesta obtenida
                if (result == null) {
                    //Muestra mensaje de error de conexión
                    Toast.makeText(getApplicationContext(), R.string.error_conexion, Toast.LENGTH_SHORT).show();
                } else {
                    //Despliega mensaje recibido
                    Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                    //Revisa respuesta
                    if (Constants.ONE.equals(result.getValidationInd())) {//Registro OK
                        //Existe un usuario en sesion
                        SessionData.getInstance().setUser(true);
                        //Asigna pais y ciudad del usuario
                        SessionData.getInstance().setUserId(userId.getText().toString().trim());
                        //Crea intent para ir al foro
                        Intent intent = new Intent (CrearHiloActivity.this, ForoActivity.class);
                        //Flags para limpiar stack
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //Navega
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }*/
}