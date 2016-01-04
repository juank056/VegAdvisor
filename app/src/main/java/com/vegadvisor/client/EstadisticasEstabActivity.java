package com.vegadvisor.client;

import android.app.DatePickerDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.reflect.TypeToken;
import com.vegadvisor.client.bo.EstablishmentStatistic;
import com.vegadvisor.client.util.Constants;
import com.vegadvisor.client.util.DateUtils;
import com.vegadvisor.client.util.SessionData;
import com.vegadvisor.client.util.VegAdvisorActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstadisticasEstabActivity extends VegAdvisorActivity implements View.OnClickListener {

    /**
     * Campos de la pantalla
     */
    private EditText fecha_inicio, fecha_fin;

    /**
     * Radio buttons de seleccion
     */
    private RadioButton graph1, graph2;

    /**
     * Dialogos de seleccion de fecha
     */
    private DatePickerDialog datePickerDialog_init, datePickerDialog_fin;

    /**
     * Date formater
     */
    private SimpleDateFormat dateFormat;

    /**
     * Objetos de fecha inicio y fecha fin
     */
    private Date oFechaInicio, oFechaFin;

    /**
     * Container de graficas
     */
    private LinearLayout container;

    /**
     * Lista de estadisticas
     */
    private List<EstablishmentStatistic> statistics;

    /**
     * @param savedInstanceState Instancia
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas_estab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Inicia pantalla
        initScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * @param serviceId Id del servicio
     * @param service   Servicio que se ha llamado
     * @param result    Resultado de la ejecución
     */
    @Override
    public void receiveServerCallResult(final int serviceId, final String service,
                                        final List<?> result) {
        //Super
        super.receiveServerCallResult(serviceId, service, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Revisa id de servicio ejecutado
                switch (serviceId) {
                    case 161:/*Busqueda de paises*/
                        //Asigna lista de respuesta
                        statistics = (List<EstablishmentStatistic>) result;
                        //Dibuja nuevamente grafica
                        reDrawGraphic();
                        break;
                }

            }
        });
    }

    /**
     * Inicia la pantalla
     */
    private void initScreen() {
        //Obtiene los campos de pantalla
        fecha_inicio = (EditText) findViewById(R.id.fecha_inicio);
        fecha_fin = (EditText) findViewById(R.id.fecha_fin);
        graph1 = (RadioButton) findViewById(R.id.graph1);
        graph2 = (RadioButton) findViewById(R.id.graph2);
        //Obtiene fecha actual
        oFechaFin = DateUtils.getCurrentUtilDate();
        //Fecha inicial de busqueda
        oFechaInicio = DateUtils.moveUtilDate(oFechaFin, (-1) * Constants.RANGE_SIZE);
        //DatePicker
        //Para date
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        fecha_inicio.setText(dateFormat.format(oFechaInicio.getTime()));
        fecha_fin.setText(dateFormat.format(oFechaFin.getTime()));
        //Dialog de Date Picker
        datePickerDialog_init = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                //Asigna fecha
                oFechaInicio = newDate.getTime();
                fecha_inicio.setText(dateFormat.format(newDate.getTime()));
                //Re-calcula graficas
                recalculateGraphics();
            }
        }, oFechaInicio.getYear() + 1900, oFechaInicio.getMonth(), oFechaInicio.getDate());
        datePickerDialog_fin = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                //Asigna fecha
                oFechaFin = newDate.getTime();
                fecha_fin.setText(dateFormat.format(newDate.getTime()));
                //Re-calcula graficas
                recalculateGraphics();
            }
        }, oFechaFin.getYear() + 1900, oFechaFin.getMonth(), oFechaFin.getDate());
        //Por defecto primero se visualizan checkins
        graph1.setChecked(true);
        //Container
        container = (LinearLayout) findViewById(R.id.container);
        //Listeners
        fecha_inicio.setOnClickListener(this);
        fecha_fin.setOnClickListener(this);
        graph1.setOnClickListener(this);
        graph2.setOnClickListener(this);
        //Calculo de graficas
        recalculateGraphics();
    }

    /**
     * Cambio de orientacion
     *
     * @param newConfig nueva configuracion
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Regenera graficas
        reDrawGraphic();
    }

    /**
     * Se engarga de recalcular las graficas del establecimiento
     */
    private void recalculateGraphics() {
        //Obtiene estadisticas del establecimiento para el rango dado
        Map<String, String> params = new HashMap<>();
        params.put("establishmentId", Constants.BLANKS + SessionData.getInstance().getUserEstab().getEstcestnk());
        params.put("sinceDate", DateUtils.getDateStringYYYYMMDD(oFechaInicio));
        params.put("untilDate", DateUtils.getDateStringYYYYMMDD(oFechaFin));
        //Ejecuta servicio
        SessionData.getInstance().executeServiceList(161,
                getResources().getString(R.string.establishment_getEstablishmentStatistics),
                params, new TypeToken<List<EstablishmentStatistic>>() {
                }.getType());
    }

    /**
     * Vuelve a pintar las graficas
     */
    private boolean reDrawGraphic() {
        //Verifica que se tengan
        if (statistics == null) {/*No hay statistics*/
            //Finaliza
            return false;
        }
        Log.d(Constants.DEBUG, "RE-DIBUJANDO GRAFICAS: " + statistics);
        //Inicia ArrayList de entradas
        ArrayList<BarEntry> entries = new ArrayList<>();
        //Inicia ArrayList de labels
        ArrayList<String> labels = new ArrayList<>();
        //Contador
        int cont = 0;
        //Recorre registros obtenidos
        for (EstablishmentStatistic statistic : statistics) {
            //Label del dia
            labels.add(DateUtils.getDateString(new Date(Long.valueOf(statistic.getDay()))));
            //Revisa de acuerdo a la grafica a dibujar
            if (graph1.isChecked()) {/*Numero de Check-ins*/
                //Pone checkins
                entries.add(new BarEntry((float) statistic.getCheckins(), cont));
            } else {/*Promedio de estrellas*/
                //Calcula promedio del dia de estrellas
                entries.add(new BarEntry(statistic.getAverageStars(), cont));
            }
            //Incrementa contador
            cont++;
        }
        //Titulo del dataset
        String title = graph1.isChecked() ? getResources().getString(R.string.prom_checkin) :
                getResources().getString(R.string.prom_estrellas);
        //Inicia dataset
        BarDataSet dataset = new BarDataSet(entries, title);
        dataset.setColors(ColorTemplate.JOYFUL_COLORS);
        //Barchart
        BarChart chart = new BarChart(this);
        //Datos
        BarData data = new BarData(labels, dataset);
        //Los asigna
        chart.setData(data);
        chart.setDescription(title);
        //Ingresa la grafica en el container
        container.removeAllViews();
        container.addView(chart, 0);
        //Dimensiones del chart
        chart.getLayoutParams().width = container.getWidth();
        chart.getLayoutParams().height = container.getHeight();
        //Finaliza
        return true;
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    public void onClick(View v) {
        //Revisa el boton que se dio click
        switch (v.getId()) {
            case R.id.fecha_inicio:/*Seleccionar imagen*/
                //Fecha inicio
                datePickerDialog_init.show();
                break;
            case R.id.fecha_fin:/*Enviar datos al Servidor*/
                //Fecha fin
                datePickerDialog_fin.show();
                break;
            case R.id.graph1: /*Graph 1*/
                //Re-calcula graficas
                recalculateGraphics();
                break;
            case R.id.graph2: /*Graph 2*/
                //Re-calcula graficas
                recalculateGraphics();
                break;
        }
    }
}
