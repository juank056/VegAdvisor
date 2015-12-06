package com.vegadvisor.client;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TwoLineListItem;

import com.google.gson.reflect.TypeToken;
import com.vegadvisor.client.bo.Usmusuar;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.List;

public class ChatActivity extends VegAdvisorActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    /**
     * Texto de busqueda del usuario
     */
    private EditText busqueda;

    /**
     * Lista de usuarios
     */
    private ListView listaUsuarios;

    /**
     * Lista de objetos de usuarios
     */
    private List<Usmusuar> lsUsuar;

    /**
     * Lista de usuarios con los que ya ha conversado
     */
    private List<String[]> userConversations;

    /**
     * @param savedInstanceState Instancia
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Obtiene campos de pantalla
        busqueda = (EditText) findViewById(R.id.busqueda);
        listaUsuarios = (ListView) findViewById(R.id.listaUsuarios);
        listaUsuarios.setOnItemClickListener(this);
        //Obtiene conversaciones
        userConversations = SessionData.getInstance().getDatabaseHandler().
                getContacts(SessionData.getInstance().getUserId());
        //Modify Peers List
        modifyPeersList();
        //Boton de busqueda
        findViewById(R.id.b1).setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        //Se le ha dado a buscar
        searchChatUsers();
    }

    /**
     * Método para recibir y procesar la respuesta a un llamado al servidor
     *
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    public void receiveServerCallResult(final int serviceId, final String service, final List<?> result) {
        //Super
        super.receiveServerCallResult(serviceId, service, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Revisa que haya lista de resultado
                if (result != null) {
                    //Lista de usuarios
                    lsUsuar = (List<Usmusuar>) result;
                    //Recorre lista para asignar usuarios
                    for (Usmusuar usuar : lsUsuar) {
                        //Revisa si ya estaba en la lista de conversaciones
                        boolean contains = false;
                        for (int i = 0; i < userConversations.size(); i++) {
                            if (userConversations.get(i)[0].equals(usuar.getUsucusuak())) {
                                //Contenido
                                contains = true;
                                break;
                            }
                        }
                        //Revisa si no lo contenia
                        if (!contains) {/*No estaba*/
                            //Adiciona nuevo usuario a la lista
                            userConversations.add(new String[]{usuar.getUsucusuak(),
                                    usuar.getUsunusuaf() + Constants.BLANK_SPACE + usuar.getUsuapelaf()});
                        }
                    }
                    //Modifica lista de peers
                    modifyPeersList();
                }
            }
        });
    }


    /**
     * Modifica lista de peers
     */
    @SuppressWarnings("unchecked")
    private void modifyPeersList() {
        //Para incluir elementos de la lista
        ArrayAdapter adapter = new ArrayAdapter(ChatActivity.this,
                android.R.layout.simple_list_item_2, userConversations) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TwoLineListItem row;
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_2, null);
                } else {
                    row = (TwoLineListItem) convertView;
                }
                String[] data = userConversations.get(position);
                row.getText1().setText(data[0]);
                row.getText2().setText(data[1]);
                row.getText1().setTextColor(Color.BLUE);
                row.getText2().setTextColor(Color.DKGRAY);
                return row;
            }
        };
        //Incluye nuevos peers
        listaUsuarios.setAdapter(adapter);
        //Notifica
        adapter.notifyDataSetChanged();
    }


    /**
     * Busca usuarios de chat
     */
    private void searchChatUsers() {
        //Obtiene establecimientos
        SessionData.getInstance().executeServiceList(461,
                getResources().getString(R.string.chat_findChatUsers),
                this.createParametersMap("clue", busqueda.getText().toString().trim()), new TypeToken<List<Usmusuar>>() {
                }.getType());
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Obtiene usuario seleccionado
        String[] usuar = userConversations.get(position);
        if (usuar != null) {/*Hay usuario*/
            //Asina usuario chat a datos de sesion
            SessionData.getInstance().setChatPeer(usuar);
            //Crea intent para ir a conversación
            Intent intent = new Intent(ChatActivity.this, ConversacionActivity.class);
            //Inicia
            startActivity(intent);
        }
    }

}
