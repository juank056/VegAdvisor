package com.vegadvisor.client;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import com.vegadvisor.client.bo.Esmestab;
import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.bo.Usmusuar;
import com.vegadvisor.client.util.ChatMessage;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.DateUtils;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.Date;
import java.util.List;

public class ConversacionActivity extends VegAdvisorActivity implements View.OnClickListener {

    /**
     * Nombre del usuario peer
     */
    private TextView userName;

    /**
     * Imagen del usuario
     */
    private ImageView userImage;

    /**
     * Lista de mensajes
     */
    private ListView listaMensajes;

    /**
     * Mensaje
     */
    private EditText mensaje;

    /**
     * Usuario from
     */
    private String userFrom;

    /**
     * Usuario to
     */
    private String userTo;

    /**
     * Texto a enviar
     */
    private String text;

    /**
     * Lista de mensajes de conversacion
     */
    private List<ChatMessage> conversation;

    /**
     * Numero de mensajes a obtener
     */
    private static final int MAX_MESSAGES = 50;


    /**
     * @param savedInstanceState Instancia
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversacion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Obtiene campos de pantalla
        userName = (TextView) findViewById(R.id.userName);
        userImage = (ImageView) findViewById(R.id.userImage);
        listaMensajes = (ListView) findViewById(R.id.listaMensajes);
        mensaje = (EditText) findViewById(R.id.mensaje);
        findViewById(R.id.b1).setOnClickListener(this);
        findViewById(R.id.b2).setOnClickListener(this);
        //Inicia pantalla
        initScreen();
    }

    /**
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    @Override
    public void receiveServerCallResult(final int serviceId, final String service, final ReturnValidation result) {
        //Super
        super.receiveServerCallResult(serviceId, service, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Muestra mensaje recibido
                Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                //Revisa de acuerdo a lo ejecutado
                switch (serviceId) {
                    case 481: /*Envio de mensaje*/
                        //Revisa si fue exitoso el envio
                        if (Constants.ONE.equals(result.getValidationInd())) {/*Exitoso*/
                            //Texto a blancos
                            mensaje.setText(Constants.BLANKS);
                            //Ingresa mensaje en la base de datos local
                            SessionData.getInstance().getDatabaseHandler().saveMessage(userFrom,
                                    userTo, text, Constants.ZERO, userName.getText().toString().trim());
                            //Fecha actual
                            Date current = DateUtils.getCurrentUtilDate();
                            //Ingresa mensaje a la lista de mensajes
                            ChatMessage message = new ChatMessage(userFrom, userTo, DateUtils.getDateString(current),
                                    DateUtils.getTimeString(current), text, Constants.ZERO, userName.getText().toString().trim());
                            conversation.add(message);
                            //Notifica a server para que le llegue el mensaje al peer
                            SessionData.getInstance().getMessages().offer(userTo);
                            //Refresca mensajes
                            refreshMessages();
                        }
                        break;
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
        switch (v.getId()) {
            case R.id.b1: /*Enviar mensaje*/
                sendMessage();
                break;
            case R.id.b2: /*Eliminar conversacion*/
                deleteConversation();
                break;
        }
    }

    /**
     * Para eliminar la conversación
     */
    private void deleteConversation() {
        //Pide confirmacion
        this.showConfirmDialog(1, getResources().getString(R.string.confirmacion),
                getResources().getString(R.string.confirma_eliminar), getResources().getString(R.string.si),
                getResources().getString(R.string.no));
    }

    /**
     * Ejecuta acción al confirmar un dialogo
     *
     * @param dialogId Id del dialogo de confirmación
     * @param positive Si la respuesta fue positiva o no
     */
    protected void executeConfirmDialogAction(int dialogId, boolean positive) {
        if (dialogId == 1 && positive) {/*Eliminar conversación*/
            //Usuario from
            userFrom = SessionData.getInstance().getUserId();
            //Usuario To
            userTo = SessionData.getInstance().getChatPeer()[0];
            //Elimina conversación
            SessionData.getInstance().getDatabaseHandler().deleteConversation(userFrom, userTo);
            //Limpia lista de conversacion
            conversation.clear();
            //Notifica cambios
            refreshMessages();
        }
    }

    /**
     * Envia mensaje por el chat
     */
    private void sendMessage() {
        //Usuario from
        userFrom = SessionData.getInstance().getUserId();
        //Usuario To
        userTo = SessionData.getInstance().getChatPeer()[0];
        //Obtiene texto a enviar
        text = mensaje.getText().toString().trim();
        if (!Constants.BLANKS.equals(text)) {
            //Envia mensaje
            SessionData.getInstance().executeServiceRV(481, getResources().getString(R.string.chat_registerChatMessage),
                    this.createParametersMap("userIdFrom", userFrom, "userIdTo", userTo, "content", text));
        }
    }

    /**
     * Inicia pantalla
     */
    private void initScreen() {
        //Usuario en sesion
        userFrom = SessionData.getInstance().getUserId();
        //Obtiene usuario Peer
        String[] peer = SessionData.getInstance().getChatPeer();
        //Asigna id y nombre
        userTo = peer[0];
        //Nombre del usuario
        userName.setText(peer[1]);
        //Conversacion
        conversation = SessionData.getInstance().getDatabaseHandler().getMessages(userFrom, userTo, MAX_MESSAGES);
        //Refresca mensajes
        refreshMessages();
    }

    /**
     * Refresca mensajes en pantalla
     */
    @SuppressWarnings("unchecked")
    private void refreshMessages() {
        //Crea adapter
        ArrayAdapter adapter = new ArrayAdapter(ConversacionActivity.this,
                android.R.layout.simple_list_item_2, conversation) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TwoLineListItem row;
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_2, null);
                } else {
                    row = (TwoLineListItem) convertView;
                }
                ChatMessage data = conversation.get(position);
                String title = "(" + data.getMessageDate() + " " + data.getMessageTime() + ") ";
                if (Constants.ONE.equals(data.getMessageReceived())) {/*Recibido*/
                    row.getText1().setTextColor(Color.MAGENTA);
                    title += data.getUserName();
                } else {/*Enviado*/
                    row.getText1().setTextColor(Color.BLUE);
                    title += SessionData.getInstance().getUsuarObject().getUsunusuaf();
                }
                row.getText1().setText(title);
                row.getText2().setText(data.getMessageContent());
                row.getText2().setTextColor(Color.DKGRAY);
                row.getText1().setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                row.getText2().setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                return row;
            }
        };
        //Incluye nuevos peers
        listaMensajes.setAdapter(adapter);
        //Notifica
        adapter.notifyDataSetChanged();
    }
}
