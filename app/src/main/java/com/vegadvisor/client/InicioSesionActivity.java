package com.vegadvisor.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.PasswordManager;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InicioSesionActivity extends VegAdvisorActivity implements View.OnClickListener {

    /**
     * Campos de texto
     */
    private EditText userId, passwd;


    /**
     * Inicia actividad
     *
     * @param savedInstanceState Instancia salvada
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Botones
        findViewById(R.id.b1).setOnClickListener(this);
        //Obtiene campos de texto
        userId = (EditText) findViewById(R.id.userid);
        passwd = (EditText) findViewById(R.id.passwd);
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
                //Revisa el resultado obtenido
                if (result == null) {/*No llegó respuesta*/
                    //Muestra mensaje de error de conexión
                    Toast.makeText(getApplicationContext(), R.string.error_conexion, Toast.LENGTH_SHORT).show();
                } else {/*Llegó resultado*/
                    //Pregunta por servicio ejecutado
                    if (service.equals(getResources().getString(R.string.user_validateUser))) {
                        //Revisa respuesta
                        if (Constants.ZERO.equals(result.getValidationInd())) {/*Error iniciando sesión*/
                            Toast.makeText(getApplicationContext(), R.string.error_inicio_sesion, Toast.LENGTH_SHORT).show();
                        } else {/*Sesión iniciada correctamente*/
                            //Existe un usuario en sesion
                            SessionData.getInstance().setUser(true);
                            //Nombre de usuario y contraseña
                            SessionData.getInstance().setUserId(userId.getText().toString());
                            SessionData.getInstance().setUserId(passwd.getText().toString());
                            //Asigna pais y ciudad del usuario
                            SessionData.getInstance().setUserCountry(result.getParams().get(Constants.USER_COUNTRY));
                            SessionData.getInstance().setUserCity(result.getParams().get(Constants.USER_CITY));
                            //Guarda datos del usuario en las preferencias del sistema
                            //Obtiene shared Preferences
                            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                            //Obtiene editor
                            SharedPreferences.Editor editor = sharedPref.edit();
                            //Ingresa nuevo valor
                            editor.putString(Constants.USERID_PREFERENCE, userId.getText().toString());
                            editor.putString(Constants.PASSWD_PREFERENCE, passwd.getText().toString());
                            //Commit del valor nuevo
                            editor.commit();
                            //Crea intent para ir al menú principal
                            Intent intent = new Intent(InicioSesionActivity.this, MenuPrincipalActivity.class);
                            //Navega
                            startActivity(intent);
                            //Finaliza
                            finish();
                        }
                    }
                }
            }
        });
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        //Revisa caso
        switch (v.getId()) {
            case R.id.b1: /*Enviar para iniciar sesión*/
                //Ejecuta llamado a inicio de sesion
                //Parámetros de llamado al servicio
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", userId.getText().toString());
                params.put("password", PasswordManager.encryptPassword(passwd.getText().toString()));
                //Valida usuario y contraseña en el servidor
                SessionData.getInstance().executeServiceRV(1, getResources().getString(R.string.user_validateUser), params);
                break;
        }
    }
}
