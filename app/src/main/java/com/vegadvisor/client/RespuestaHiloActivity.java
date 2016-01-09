package com.vegadvisor.client;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vegadvisor.client.bo.Fodreshi;
import com.vegadvisor.client.bo.FodreshiId;
import com.vegadvisor.client.bo.Fomhilfo;
import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.DateUtils;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.io.File;
import java.util.Date;
import java.util.List;

public class RespuestaHiloActivity extends VegAdvisorActivity implements View.OnClickListener {

    // Encabezado campo respuesta
    private TextView respuestaHeader, hilo;

    // Campo respuesta
    private EditText respuestaContent;

    // Hilo del foro
    private Fomhilfo hiloForo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respuesta_hilo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Encabezado campo hilo y campo respuesta
        hilo = (TextView) findViewById(R.id.foro_hilo);
        //Respuesta
        respuestaContent = (EditText) findViewById(R.id.foro_respuesta);
        //Boton enviar respuesta y botón imagen ejecuta método onClick()
        findViewById(R.id.foro_btn_envRespuesta).setOnClickListener(this);

    }

    /**
     * Actualiza la pantalla con el contenido del hilo especificado sobre él que se va a responder
     */
    @Override
    protected void onResume() {
        super.onResume();
        //Asigna hilo
        hiloForo = SessionData.getInstance().getForumThread();
        //Detalle del hilo
        hilo.setText(hiloForo.getHifdetaaf());
    }

    @Override
    public void receiveServerCallResult(final int serviceId, final String service, final ReturnValidation result) {
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
                        //Fecha actual
                        Date current = DateUtils.getCurrentUtilDate();
                        //Registro de respuesta
                        Fodreshi reshi = new Fodreshi(null, SessionData.getInstance().getUserId(), current,
                                DateUtils.getTimeString(current), respuestaContent.getText().toString());
                        //Nombre de usuario
                        reshi.setUserName(SessionData.getInstance().getUsuarObject().getUsunusuaf()
                                + Constants.BLANK_SPACE + SessionData.getInstance().getUsuarObject().getUsuapelaf());
                        //Ingresa nuevo registro a la lista de respuestas
                        hiloForo.getResponses().add(reshi);
                        //Crea intent para ir al foro
                        Intent intent = new Intent(RespuestaHiloActivity.this, DetalleHiloActivity.class);
                        //Navega
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    /**
     * Recoge evento click sobre un botón y lleva a cabo una acción en función de cual haya sido
     *
     * @param v, botón clicado
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.foro_btn_envRespuesta:
                saveReplicaHilo();
                break;
        }
    }

    /**
     * Metodo para validar los datos de respuesta y registrarla en el sistema
     */
    private boolean saveReplicaHilo() {
        //Detalle Opinión
        if (Constants.BLANKS.equals(respuestaContent.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.campoInvalido).toString() +
                    getResources().getText(R.string.detalle_respuesta).toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        //Hilo
        hiloForo = SessionData.getInstance().getForumThread();
        //Envía actualización al servidor
        SessionData.getInstance().executeServiceRV(441,
                getResources().getString(R.string.forum_createForumThreadResponse),
                this.createParametersMap(
                        "userId", SessionData.getInstance().getUserId(),
                        "threadId", Constants.BLANKS + hiloForo.getHifchifnk(),
                        "responseDetail", respuestaContent.getText().toString().trim()));
        //Finaliza Ok
        return true;
    }

}
