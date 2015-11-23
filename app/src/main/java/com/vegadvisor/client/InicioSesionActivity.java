package com.vegadvisor.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
                            //Crea intent para ir al menú principal
                            Intent intent = new Intent(InicioSesionActivity.this, MenuPrincipalActivity.class);
                            //Flags para limpiar stack
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                //Valida usuario y contraseña en el servidor
                SessionData.getInstance().executeServiceRV(1, getResources().getString(R.string.user_validateUser),
                        this.createParametersMap("userId", userId.getText().toString(),
                                "password", PasswordManager.encryptPassword(passwd.getText().toString())));
                break;
        }
    }
}
