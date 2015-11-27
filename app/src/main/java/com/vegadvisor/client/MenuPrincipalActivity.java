package com.vegadvisor.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.vegadvisor.client.bo.Usmusuar;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

public class MenuPrincipalActivity extends VegAdvisorActivity implements View.OnClickListener {

    /**
     * Nombre del usuario
     */
    private TextView userName;

    /**
     * Imágen del usuario
     */
    private ImageView userImage;

    /**
     * @param savedInstanceState Instancia salvada
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Botones
        findViewById(R.id.b0).setOnClickListener(this);
        findViewById(R.id.b1).setOnClickListener(this);
        findViewById(R.id.b2).setOnClickListener(this);
        findViewById(R.id.b3).setOnClickListener(this);
        findViewById(R.id.b4).setOnClickListener(this);
        findViewById(R.id.b5).setOnClickListener(this);
        findViewById(R.id.b6).setOnClickListener(this);
        findViewById(R.id.b7).setOnClickListener(this);
        findViewById(R.id.b8).setOnClickListener(this);
        findViewById(R.id.b9).setOnClickListener(this);
        //Nombre del usuario e imagen
        userName = (TextView) findViewById(R.id.userName);
        userImage = (ImageView) findViewById(R.id.userImage);
    }

    @Override
    protected void onResume() {
        //On Resume Padre
        super.onResume();
        //Revisa si hay un usuario en sesion
        if (SessionData.getInstance().isUser()) {
            //Boton de registro deshabilitado
            findViewById(R.id.b0).setVisibility(View.INVISIBLE);
            findViewById(R.id.b0).setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            //Obtiene datos del usuario que se encuentra en sesion
            SessionData.getInstance().executeServiceObject(1,
                    getResources().getString(R.string.user_findUserById),
                    this.createParametersMap("userId", SessionData.getInstance().getUserId()),
                    new TypeToken<Usmusuar>() {
                    }.getType());
        } else {/*No hay usuario en sesion*/
            //Esconde botones (mi perfil, chat, mis establecimientos)
            findViewById(R.id.b3).setVisibility(View.INVISIBLE);
            findViewById(R.id.b6).setVisibility(View.INVISIBLE);
            findViewById(R.id.b7).setVisibility(View.INVISIBLE);
            findViewById(R.id.b3).setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            findViewById(R.id.b6).setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            findViewById(R.id.b7).setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            //Asigna texto de usuario invitado
            userName.setText(R.string.usuario_invitado);
            //Imagen
            userImage.setImageResource(R.drawable.ic_launcher);
        }

    }

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    public void receiveServerCallResult(final int serviceId, final String service, final Object result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (result != null) {/*Llego usuario de resultado*/
                    //Usuario recibido
                    Usmusuar usuar = (Usmusuar) result;
                    //Asigna el usuario a los datos de sesion
                    SessionData.getInstance().setUsuarObject(usuar);
                    //Nombre de imagen en la pantalla
                    userName.setText(usuar.getUsunusuaf() + Constants.BLANK_SPACE + usuar.getUsuapelaf());
                    //Guarda datos del usuario en las preferencias del sistema
                    //Obtiene shared Preferences
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    //Obtiene editor
                    SharedPreferences.Editor editor = sharedPref.edit();
                    //Ingresa nuevo valor
                    editor.putString(Constants.USERID_PREFERENCE, usuar.getUsucusuak());
                    editor.putString(Constants.PASSWD_PREFERENCE, usuar.getUsupassaf());
                    //Commit del valor nuevo
                    editor.commit();
                    //Revisa si el usuario tiene una imagen
                    if (!Constants.BLANKS.equals(usuar.getUsufotoaf())) {/*Hay imagen*/
                        //Obtiene la imagen del usuario
                        SessionData.getInstance().executeServiceImage(2,
                                getResources().getString(R.string.image_downloadImage),
                                MenuPrincipalActivity.this.createParametersMap("imagePath", usuar.getUsufotoaf()));
                    } else {/*No hay imagen*/
                        //Asigna logo de la aplicación
                        userImage.setImageResource(R.drawable.ic_launcher);
                    }
                }
            }
        });
    }

    @Override
    public void receiveServerCallResult(final int serviceId, final String service, final Bitmap result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Revisa que se tenga imagen
                if (result != null) {
                    //Asigna bitmap a la imagen
                    userImage.setImageBitmap(result);
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
        //Intent
        Intent intent = null;
        switch (v.getId()) {
            case R.id.b0: /*Registrarse*/
                //Navega hacia el registro de un nuevo usuario
                intent = new Intent(MenuPrincipalActivity.this, RegistroActivity.class);
                break;
            case R.id.b1: /*Localizador*/
                //Navega a localizador
                intent = new Intent(MenuPrincipalActivity.this, LocalizadorActivity.class);
                break;
            case R.id.b2: /*Cercano a mi*/
                //Navega a cercano a mi
                intent = new Intent(MenuPrincipalActivity.this, CercanoaMiActivity.class);
                break;
            case R.id.b3: /*Perfil*/
                //Navega hacia actividad de perfil
                intent = new Intent(MenuPrincipalActivity.this, PerfilActivity.class);
                break;
            case R.id.b4: /*Eventos*/
                //Navega hacia actividad de Eventos
                intent = new Intent(MenuPrincipalActivity.this, EventosActivity.class);
                break;
            case R.id.b5: /*Foro*/
                //Navega hacia actividad de foro
                intent = new Intent(MenuPrincipalActivity.this, ForoActivity.class);
                break;
            case R.id.b6: /*Chat*/
                //Navega hacia actividad de chat
                intent = new Intent(MenuPrincipalActivity.this, ChatActivity.class);
                break;
            case R.id.b7: /*Establecimientos*/
                //Navegar hacia Establecimientos
                intent = new Intent(MenuPrincipalActivity.this, EstablecimientosActivity.class);
                break;
            case R.id.b8: /*Contacto*/
                //Navega hacia actividad de Contacto
                intent = new Intent(MenuPrincipalActivity.this, ContactoActivity.class);
                break;
            case R.id.b9: /*Salir*/
                //Limpia datos de sesion
                SessionData.getInstance().cleanData();
                //Elimina shared preferences
                //Obtiene shared Preferences
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                //Obtiene editor
                SharedPreferences.Editor editor = sharedPref.edit();
                //Ingresa nuevo valor
                editor.putString(Constants.USERID_PREFERENCE, Constants.BLANKS);
                editor.putString(Constants.PASSWD_PREFERENCE, Constants.BLANKS);
                editor.commit();
                //Navegar a la pantalla principal
                intent = new Intent(MenuPrincipalActivity.this, InicioAplicacionActivity.class);
                //Flags para limpiar stack
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
        }
        //Realiza navegacion
        if (intent != null) { /*Revisa si hay un intent*/
            //navega
            startActivity(intent);
        }

    }
}
