package com.vegadvisor.client;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.reflect.TypeToken;
import com.vegadvisor.client.bo.Esdimaes;
import com.vegadvisor.client.bo.Esdopies;
import com.vegadvisor.client.bo.Esmestab;
import com.vegadvisor.client.bo.ReturnValidation;
import com.vegadvisor.client.bo.Usmusuar;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.util.List;

public class ConsultaEstabActivity extends VegAdvisorActivity implements View.OnClickListener, AdapterView.OnItemClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /**
     * Establecimiento consultado
     */
    private Esmestab estab;

    /**
     * Mapa
     */
    private GoogleMap googleMap;

    /**
     * Campos de texto a desplegar
     */
    private TextView nombreEstab, descripcion, direccion, telefono, horario;

    /**
     * Estrellas del establecimiento
     */
    private ImageView star1, star2, star3, star4, star5;

    /**
     * Imagenes
     */
    private ViewFlipper imagenes;

    /**
     * Detector
     */
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());

    /**
     * Botones
     */
    private ImageButton b1;

    /**
     * Lista de opiniones
     */
    private List<Esdopies> lsOpinion;

    /**
     * Lista de opinion
     */
    private ListView listaOpinion;


    /**
     * @param savedInstanceState Instancia
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_estab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Establecimiento consultado
        estab = SessionData.getInstance().getUserEstab();
        //Campos de pantalla
        nombreEstab = (TextView) findViewById(R.id.nombreEstab);
        descripcion = (TextView) findViewById(R.id.descripcion);
        direccion = (TextView) findViewById(R.id.direccion);
        telefono = (TextView) findViewById(R.id.telefono);
        horario = (TextView) findViewById(R.id.horario);
        //Estrellas
        star1 = (ImageView) findViewById(R.id.star1);
        star2 = (ImageView) findViewById(R.id.star2);
        star3 = (ImageView) findViewById(R.id.star3);
        star4 = (ImageView) findViewById(R.id.star4);
        star5 = (ImageView) findViewById(R.id.star5);
        //Imagenes
        imagenes = (ViewFlipper) findViewById(R.id.imagenes);
        //Lista opinion
        listaOpinion = (ListView) findViewById(R.id.listaOpinion);
        listaOpinion.setOnItemClickListener(this);
        //Inicia view Flipper
        initViewFlipper();
        //Acciones de mapa
        mapActions();
        //Inicia google api client
        buildGoogleApiClient();
        //Inicia pantalla
        initScreen();
        //Objetos ok click
        telefono.setOnClickListener(this);
        //Boton check in
        b1 = (ImageButton) findViewById(R.id.b1);
        b1.setOnClickListener(this);
        //Boton registro opinión
        Button b2 = (Button) findViewById(R.id.b2);
        b2.setOnClickListener(this);
        //Compartir
        ImageButton b3 = (ImageButton) findViewById(R.id.b3);
        b3.setOnClickListener(this);
        //Revisa si no hay usuario para esconder botones
        if (!SessionData.getInstance().isUser()) {/*No hay usuario*/
            //Esconde botones check in y registro opinion y compartir
            b1.setVisibility(View.GONE);
            b2.setVisibility(View.GONE);
            b3.setVisibility(View.GONE);
        }
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
                    case 222: /*Check in de usuario*/
                        //Si el resultado fue ok esconde boton de check in
                        if (Constants.ONE.equals(result.getValidationInd())) {
                            //Esconde boton
                            b1.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void receiveServerCallResult(final int serviceId, final String service, final Bitmap result) {
        //Super
        super.receiveServerCallResult(serviceId, service, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Revisa que se tenga imagen
                if (result != null) {
                    //Crea nueva image view
                    ImageView image = new ImageView(getApplicationContext());
                    //Asigna bitmap
                    image.setImageBitmap(result);
                    //Ingresa imagen al list view de imagenes
                    imagenes.addView(image, 0);
                }
            }
        });
    }

    /**
     * @param serviceId Id del servicio
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    @Override
    @SuppressWarnings("unchecked")
    public void receiveServerCallResult(final int serviceId, final String service,
                                        final List<?> result) {
        //Super
        super.receiveServerCallResult(serviceId, service, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Revisa id de servicio ejecutado
                switch (serviceId) {
                    case 223:/*Lista de opiniones*/
                        //Lista de opiniones
                        lsOpinion = (List<Esdopies>) result;
                        //Para incluir elementos de la lista
                        ArrayAdapter adapter = new ArrayAdapter(ConsultaEstabActivity.this,
                                android.R.layout.simple_list_item_2, lsOpinion) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                TwoLineListItem row;
                                if (convertView == null) {
                                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    row = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_2, null);
                                } else {
                                    row = (TwoLineListItem) convertView;
                                }
                                Esdopies opies = lsOpinion.get(position);
                                //Nombre de usuario y estrellas
                                String title = opies.getUserName() + Constants.BLANK_SPACE + Constants.LEFT_PARENTHESIS +
                                        opies.getOesnestnf() + Constants.BLANK_SPACE + getResources().getString(R.string.estrellas)
                                        + Constants.RIGHT_PARENTHESIS;
                                row.getText1().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                row.getText2().setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                                row.getText1().setText(title);
                                row.getText2().setText(opies.getOesdetoaf());
                                row.getText1().setTextColor(Color.BLUE);
                                row.getText2().setTextColor(Color.DKGRAY);
                                return row;
                            }
                        };
                        //Incluye nuevos registros
                        listaOpinion.setAdapter(adapter);
                        //Notifica
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        });
    }

    /**
     * Inicia Pantalla
     */
    private void initScreen() {
        //Establecimiento consultado
        estab = SessionData.getInstance().getUserEstab();
        nombreEstab = (TextView) findViewById(R.id.nombreEstab);
        descripcion = (TextView) findViewById(R.id.descripcion);
        direccion = (TextView) findViewById(R.id.direccion);
        telefono = (TextView) findViewById(R.id.telefono);
        //Nombre establecimiento
        nombreEstab.setText(estab.getEstnestaf());
        //Descripción
        descripcion.setText(estab.getEstdestaf());
        //Dirección
        direccion.setText(estab.getEstdireaf());
        //Telefono
        telefono.setText(estab.getEstteleaf());
        //Horario
        String sHorario = estab.getEsthoratf() + " - " + estab.getEsthorctf();
        horario.setText(sHorario);
        //Sin visivilidad
        star1.setVisibility(View.INVISIBLE);
        star2.setVisibility(View.INVISIBLE);
        star3.setVisibility(View.INVISIBLE);
        star4.setVisibility(View.INVISIBLE);
        star5.setVisibility(View.INVISIBLE);
        if (estab.getEstpestnf() != 0) {/*Hay algo de estrellas*/
            //Obtiene parte fracion de las estrellas
            long iPart = (long) estab.getEstpestnf();
            double fPart = estab.getEstpestnf() - iPart;
            //Estrellas
            if (estab.getEstpestnf() > 0 && estab.getEstpestnf() < 1) {/*Entre cero y 1*/
                if (fPart > 0.1) {/*Hay escala*/
                    star1.getLayoutParams().width = (int) (star1.getLayoutParams().width * fPart);
                    star1.getLayoutParams().height = (int) (star1.getLayoutParams().height * fPart);
                    star1.setVisibility(View.VISIBLE);
                    star1.requestLayout();
                }
            } else if (estab.getEstpestnf() >= 1 && estab.getEstpestnf() < 2) {/*Entre 1 y 2*/
                //Visibilidad a estrellas
                star1.setVisibility(View.VISIBLE);
                if (fPart > 0.1) {/*Hay escala*/
                    star2.getLayoutParams().width = (int) (star2.getLayoutParams().width * fPart);
                    star2.getLayoutParams().height = (int) (star2.getLayoutParams().height * fPart);
                    star2.setVisibility(View.VISIBLE);
                    star2.requestLayout();
                }
            } else if (estab.getEstpestnf() >= 2 && estab.getEstpestnf() < 3) {/*Entre 2 y 3*/
                //Visibilidad a estrellas
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.VISIBLE);
                if (fPart > 0.1) {/*Hay escala*/
                    star3.getLayoutParams().width = (int) (star3.getLayoutParams().width * fPart);
                    star3.getLayoutParams().height = (int) (star3.getLayoutParams().height * fPart);
                    star3.setVisibility(View.VISIBLE);
                    star3.requestLayout();
                }
            } else if (estab.getEstpestnf() >= 3 && estab.getEstpestnf() < 4) {/*Entre 3 y 4*/
                //Visibilidad a estrellas
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.VISIBLE);
                star3.setVisibility(View.VISIBLE);
                if (fPart > 0.1) {/*Hay escala*/
                    star4.getLayoutParams().width = (int) (star4.getLayoutParams().width * fPart);
                    star4.getLayoutParams().height = (int) (star4.getLayoutParams().height * fPart);
                    star4.setVisibility(View.VISIBLE);
                    star4.requestLayout();
                }
            } else if (estab.getEstpestnf() >= 4 && estab.getEstpestnf() <= 5) {/*Entre 4 y 5*/
                //Visibilidad a estrellas
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.VISIBLE);
                star3.setVisibility(View.VISIBLE);
                star4.setVisibility(View.VISIBLE);
                if (fPart > 0.1) {/*Hay escala*/
                    star5.getLayoutParams().width = (int) (star5.getLayoutParams().width * fPart);
                    star5.getLayoutParams().height = (int) (star5.getLayoutParams().height * fPart);
                    star5.setVisibility(View.VISIBLE);
                    star5.requestLayout();
                }
            }
        }
        //Carga imagenes
        for (Esdimaes imaes : estab.getImages()) {
            //Envia petición para cargar imagen
            SessionData.getInstance().executeServiceImage(221,
                    getResources().getString(R.string.image_downloadImage),
                    ConsultaEstabActivity.this.createParametersMap("imagePath", imaes.getIesrimaaf()));
        }
        //Carga lista de opiniones
        SessionData.getInstance().executeServiceList(223,
                getResources().getString(R.string.opinion_findEstablishmentsOpinions),
                this.createParametersMap("establishmentId", Constants.BLANKS + estab.getEstcestnk(),
                        "maxOpinions", Constants.MAX_OPINION),
                new TypeToken<List<Esdopies>>() {
                }.getType()
        );
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.telefono:/*Numero de telefono*/
                //Intent para realizar llamada
                intent = new Intent(Intent.ACTION_CALL, Uri.parse(Constants.TEL + telefono.getText().toString().trim()));
                //Intenta llamada
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                }
                break;
            case R.id.b1: /*Registro de check in*/
                checkInAction();
                break;
            case R.id.b2: /*Registro de opinión*/
                //Crea intent para ir a registro de opinión
                intent = new Intent(ConsultaEstabActivity.this, RegistroOpinionActivity.class);
                //Inicia
                startActivity(intent);
                break;
            case R.id.b3: /*Compartir*/
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String textoSend = getResources().getString(R.string.texto_compartir);
                //Asigna parametros
                textoSend = textoSend.replace(Constants.URL_PARAM01, SessionData.getInstance().getUserEstab().getEstnestaf());
                textoSend = textoSend.replace(Constants.URL_PARAM02, SessionData.getInstance().getUserEstab().getEstdireaf());
                sendIntent.putExtra(Intent.EXTRA_TEXT, textoSend);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
        }
    }

    /**
     * Accion de check in del usuario en el establecimiento
     */
    private void checkInAction() {
        //Muestra dialogo de confirmación para realizar check in
        showConfirmDialog(1, getResources().getString(R.string.confirmacion),
                getResources().getString(R.string.confirm_check_in),
                getResources().getString(R.string.si), getResources().getString(R.string.no));
    }

    /**
     * Ejecuta acción al confirmar un dialogo
     *
     * @param dialogId Id del dialogo de confirmación
     * @param positive Si la respuesta fue positiva o no
     */
    protected void executeConfirmDialogAction(int dialogId, boolean positive) {
        if (dialogId == 1 && positive) {/*Realizar check in*/
            //User
            Usmusuar usar = SessionData.getInstance().getUsuarObject();
            //Establecimiento
            Esmestab estab = SessionData.getInstance().getUserEstab();
            //Ejecuta servicio para registrar check in del usuario en el establecimiento
            SessionData.getInstance().executeServiceRV(222,
                    getResources().getString(R.string.user_checkInUser),
                    createParametersMap("userId", usar.getUsucusuak(),
                            "establishmentId", Constants.BLANKS + estab.getEstcestnk()));
        }
    }

    /**************************************
     * PARA EL VIEW FLIPPER
     *************************************/

    /**
     * Inicia view Flipper
     */
    private void initViewFlipper() {
        imagenes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });
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
        //Obtiene opinion seleccionada
        Esdopies opinion = lsOpinion.get(position);
        if (opinion != null) {/*Hay establecimiento*/
            //Asina opinion en datos de sesion
            SessionData.getInstance().setOpinion(opinion);
            //Crea intent para ir a consultar la opinion
            Intent intent = new Intent(ConsultaEstabActivity.this, DetalleOpinionActivity.class);
            //Inicia
            startActivity(intent);
        }
    }

    /**
     * Detector de gestos
     */
    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    imagenes.setInAnimation(AnimationUtils.loadAnimation(ConsultaEstabActivity.this.getApplicationContext()
                            , R.anim.left_in));
                    imagenes.setOutAnimation(AnimationUtils.loadAnimation(ConsultaEstabActivity.this.getApplicationContext(),
                            R.anim.left_out));
                    imagenes.showNext();
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    imagenes.setInAnimation(AnimationUtils.loadAnimation
                            (ConsultaEstabActivity.this.getApplicationContext(), R.anim.right_in));
                    imagenes.setOutAnimation(
                            AnimationUtils.loadAnimation(ConsultaEstabActivity.this.getApplicationContext(), R.anim.right_out));
                    imagenes.showPrevious();
                    return true;
                }
            } catch (Exception e) {/*Error*/
                e.printStackTrace();
            }

            return false;
        }
    }

    /***************************************
     * PARA OBTENER LOCALIZACION CON EL GPS
     **************************************/

    /**
     * Acciones del mapa
     */
    private void mapActions() {
        //Obtiene fragmento del mapa
        /*
      Fragmento del mapa
     */
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.ubicacion);
        //Obtiene mapa
        googleMap = mapFragment.getMap();
    }


    protected synchronized void buildGoogleApiClient() {
        /*
      Google api client
     */
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //Conecta
        mGoogleApiClient.connect();
    }

    /**
     * Cuando se conecta con el Api de google para mapas
     *
     * @param bundle Bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        //Long lat
        LatLng latLng = new LatLng(estab.getEstlatinf(), estab.getEstlongnf());
        //Posiciona el mapa segun la localizacion si ya esta
        if (googleMap != null) {
            //Adiciona marker al mapa
            googleMap.addMarker(new MarkerOptions().position(latLng).title(estab.getEstnestaf()).snippet(estab.getEstdestaf()));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        /*Nada*/
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*Nada*/
    }
}
