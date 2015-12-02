package com.vegadvisor.client;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.vegadvisor.client.bo.Esdimope;
import com.vegadvisor.client.bo.Esdopies;
import com.vegadvisor.client.bo.Esmestab;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

public class DetalleOpinionActivity extends VegAdvisorActivity {

    /**
     * Detalle de la opinion
     */
    private TextView detalle_opinion;

    /**
     * Establecimiento
     */
    private TextView establecimiento;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_opinion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Estrellas
        star1 = (ImageView) findViewById(R.id.star1);
        star2 = (ImageView) findViewById(R.id.star2);
        star3 = (ImageView) findViewById(R.id.star3);
        star4 = (ImageView) findViewById(R.id.star4);
        star5 = (ImageView) findViewById(R.id.star5);
        //Establecimiento
        establecimiento = (TextView) findViewById(R.id.establecimiento);
        //Detalle Opinion
        detalle_opinion = (TextView) findViewById(R.id.detalle_opinion);
        //Imagenes
        imagenes = (ViewFlipper) findViewById(R.id.imagenes);
        //Obtiene datos
        initScreen();
        //Inicia view Flipper
        initViewFlipper();
    }

    /**
     * @param serviceId Id del servicio ejecutado
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
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
     * Asigna datos en pantalla
     */
    private void initScreen() {
        //Establecimiento
        Esmestab estab = SessionData.getInstance().getUserEstab();
        //Opinion
        Esdopies opies = SessionData.getInstance().getOpinion();
        //Asigna Establecimiento
        establecimiento.setText(estab.getEstnestaf());
        //Detalle de la opinion
        detalle_opinion.setText(opies.getOesdetoaf());
        //Estrellas
        star1.setVisibility(View.GONE);
        star2.setVisibility(View.GONE);
        star3.setVisibility(View.GONE);
        star4.setVisibility(View.GONE);
        star5.setVisibility(View.GONE);
        switch (opies.getOesnestnf()) {
            case 1:/*1 estrellas*/
                star1.setVisibility(View.VISIBLE);
                break;
            case 2:/*2 estrellas*/
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.VISIBLE);
                break;
            case 3:/*3 estrellas*/
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.VISIBLE);
                star3.setVisibility(View.VISIBLE);
                break;
            case 4:/*4 estrellas*/
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.VISIBLE);
                star3.setVisibility(View.VISIBLE);
                star4.setVisibility(View.VISIBLE);
                break;
            case 5:/*5 estrellas*/
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.VISIBLE);
                star3.setVisibility(View.VISIBLE);
                star4.setVisibility(View.VISIBLE);
                star5.setVisibility(View.VISIBLE);
                break;
        }
        //Carga imagenes
        for (Esdimope imope : opies.getImages()) {
            //Envia petición para cargar imagen
            SessionData.getInstance().executeServiceImage(261,
                    getResources().getString(R.string.image_downloadImage),
                    DetalleOpinionActivity.this.createParametersMap("imagePath", imope.getIoerimaaf()));
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
     * Detector de gestos
     */
    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    imagenes.setInAnimation(AnimationUtils.loadAnimation(DetalleOpinionActivity.this.getApplicationContext()
                            , R.anim.left_in));
                    imagenes.setOutAnimation(AnimationUtils.loadAnimation(DetalleOpinionActivity.this.getApplicationContext(),
                            R.anim.left_out));
                    imagenes.showNext();
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    imagenes.setInAnimation(AnimationUtils.loadAnimation
                            (DetalleOpinionActivity.this.getApplicationContext(), R.anim.right_in));
                    imagenes.setOutAnimation(
                            AnimationUtils.loadAnimation(DetalleOpinionActivity.this.getApplicationContext(), R.anim.right_out));
                    imagenes.showPrevious();
                    return true;
                }
            } catch (Exception e) {/*Error*/
                e.printStackTrace();
            }

            return false;
        }
    }

}
