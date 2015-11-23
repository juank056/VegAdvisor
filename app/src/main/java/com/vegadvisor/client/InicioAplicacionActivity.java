package com.vegadvisor.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.Random;

public class InicioAplicacionActivity extends VegAdvisorActivity implements View.OnClickListener {


    /**
     * On create
     *
     * @param savedInstanceState Instancia
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_aplicacion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*Inicia Session Data y connectores*/
        SessionData.getInstance().initConnectors(getResources().getString(R.string.server_path),
                Boolean.parseBoolean(getResources().getString(R.string.use_cloud_front)),
                getResources().getString(R.string.cloud_front_path));
        //Obtiene imagen superior de la pantalla
        ImageView image = (ImageView) findViewById(R.id.intro);
        //Botones
        findViewById(R.id.b1).setOnClickListener(this);
        findViewById(R.id.b2).setOnClickListener(this);
        findViewById(R.id.t1).setOnClickListener(this);
        //Random para imagen a desplegar
        Random rand = new Random();
        int random = rand.nextInt(7);
        switch (random) {
            case 0:/*Intro 1*/
                image.setImageResource(R.drawable.intro_1);
                break;
            case 1: /*Intro 2*/
                image.setImageResource(R.drawable.intro_2);
                break;
            case 2: /*Intro 3*/
                image.setImageResource(R.drawable.intro_3);
                break;
            case 3: /*Intro 4*/
                image.setImageResource(R.drawable.intro_4);
                break;
            case 4: /*Intro 5*/
                image.setImageResource(R.drawable.intro_5);
                break;
            case 5: /*Intro 6*/
                image.setImageResource(R.drawable.intro_6);
                break;
            case 6: /*Intro 7*/
                image.setImageResource(R.drawable.intro_7);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Limpia datos
        SessionData.getInstance().cleanData();
        /*Revisa si ya se encontraba un usuario autenticado*/
        checkForUser();
    }

    /**
     * Función para revisar si ya había un usuario autenticado en el sistema.
     * Si hay algun usuario, se navega directamente al menu principal
     */
    private void checkForUser() {
        //Obtiene shared Preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Obtiene nombre de usuario
        String userId = sharedPref.getString(Constants.USERID_PREFERENCE, Constants.BLANKS);
        Log.d(Constants.DEBUG, "USER ID PREFERENCIAS: " + userId);
        //Revisa si lo encontro
        if (!Constants.BLANKS.equals(userId)) {
            //Obtiene contraseña
            String passwd = sharedPref.getString(Constants.PASSWD_PREFERENCE, Constants.BLANKS);
            Log.d(Constants.DEBUG, "PASSWORD PREFERENCIAS: " + passwd);
            //Asigna usuario y contraseña en los datos de sesión
            SessionData.getInstance().setUser(true);
            SessionData.getInstance().setUserId(userId);
            //Valida usuario y contraseña en el servidor
            SessionData.getInstance().executeServiceRV(1, getResources().getString(R.string.user_validateUser),
                    this.createParametersMap("userId", userId, "password", passwd));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inicio_aplicacion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        //Intent de navegación
        Intent intent;
        switch (v.getId()) {
            case R.id.b1: /*Iniciar sesión*/
                //Crea intent para ir al inicio de la sesion
                intent = new Intent(InicioAplicacionActivity.this, InicioSesionActivity.class);
                //Navega
                startActivity(intent);
                break;
            case R.id.b2: /*Registrarse*/
                //Crea intent para ir al registro de un nuevo usuario
                intent = new Intent(InicioAplicacionActivity.this, RegistroActivity.class);
                //Navega
                startActivity(intent);
                break;
            case R.id.t1: /*Saltar inicio de sesión*/
                //Crea intent para ir directamente al menu como invitado
                intent = new Intent(InicioAplicacionActivity.this, MenuPrincipalActivity.class);
                //Navega
                startActivity(intent);
                break;
        }
    }

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    @Override
    public void receiveServerCallResult(final int serviceId, final String service, final ReturnValidation result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Revisa el resultado obtenido
                if (result == null) {/*No llegó respuesta*/
                    //Muestra mensaje de error de conexión
                    Toast.makeText(getApplicationContext(), R.string.error_conexion, Toast.LENGTH_SHORT).show();
                } else {/*Llegó resultado*/
                    //Pregunta por servicio ejecutado
                    if (service.equals(getResources().getString(R.string.user_validateUser))) {
                        //Revisa respuesta
                        if (Constants.ZERO.equals(result.getValidationInd())) {/*Error iniciando sesión*/
                            Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {/*Sesión iniciada correctamente*/
                            //Existe un usuario en sesion
                            SessionData.getInstance().setUser(true);
                            //Crea intent para ir al menú principal
                            Intent intent = new Intent(InicioAplicacionActivity.this, MenuPrincipalActivity.class);
                            //Flags para limpiar stack
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            //Navega
                            startActivity(intent);
                            //Finaliza Actividad
                            finish();
                        }
                    }
                }
            }
        });
    }
}
