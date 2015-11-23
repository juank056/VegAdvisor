package com.vegadvisor.client;

import android.content.Intent;
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
import java.util.Map;

public class RegistroActivity extends VegAdvisorActivity implements View.OnClickListener {

    /**
     * Campos del formulario
     */
    private EditText userId, name, lastName, email, passwd, passwdCon;

    /**
     * @param savedInstanceState Instancia salvada
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Obtiene campos de la pantalla
        userId = (EditText) findViewById(R.id.userid);
        name = (EditText) findViewById(R.id.name);
        lastName = (EditText) findViewById(R.id.lastname);
        email = (EditText) findViewById(R.id.email);
        passwd = (EditText) findViewById(R.id.passwd);
        passwdCon = (EditText) findViewById(R.id.passwd_con);
        //Botones
        findViewById(R.id.b1).setOnClickListener(this);
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        //Ejecuta validación de campos
        if (validateFields()) {/*Campos validados Ok*/
            //Envia datos para el registro del nuevo usuario
            //Mapa de parámetros
            Map<String, String> parameters = new HashMap<>();
            //Asigna parametros
            parameters.put("userId", userId.getText().toString().trim());
            parameters.put("userName", name.getText().toString().trim());
            parameters.put("userLastName", lastName.getText().toString().trim());
            parameters.put("email", email.getText().toString().trim());
            parameters.put("password", PasswordManager.encryptPassword(passwd.getText().toString().trim()));
            //Llama servicio para registrar al nuevo usuario
            SessionData.getInstance().executeServiceRV(1, getResources().getString(R.string.user_createUser), parameters);
        }
    }

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param service Servicio que se ha llamado
     * @param result  Resultado de la ejecución
     */
    public void receiveServerCallResult(final int serviceId, final String service, final ReturnValidation result) {
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
                    if (Constants.ONE.equals(result.getValidationInd())) {/*Registro OK*/
                        //Existe un usuario en sesion
                        SessionData.getInstance().setUser(true);
                        //Asigna pais y ciudad del usuario
                        SessionData.getInstance().setUserId(userId.getText().toString().trim());
                        //Crea intent para ir al menú principal
                        Intent intent = new Intent(RegistroActivity.this, MenuPrincipalActivity.class);
                        //Flags para limpiar stack
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //Navega
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    /**
     * Valida el ingreso de los campos en la pantalla
     *
     * @return True si la validación es correcta, false de lo contrario
     */
    private boolean validateFields() {
        //Id usuario
        if (Constants.BLANKS.equals(userId.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.id_usuario).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Nombre
        if (Constants.BLANKS.equals(name.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.nombre).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Apellido
        if (Constants.BLANKS.equals(lastName.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.apellido).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Email
        if (Constants.BLANKS.equals(email.getText().toString()) ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher((email.getText().toString())).matches()) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.email).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Contraseña
        if (Constants.BLANKS.equals(passwd.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.passwd).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Verifica que coincidan las contraseñas
        if (!passwdCon.getText().toString().trim().equals(passwd.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), R.string.passwd_not_match, Toast.LENGTH_SHORT).show();
            return false;
        }
        //Finaliza Ok
        return true;
    }

}
