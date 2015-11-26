package com.vegadvisor.client;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.vegadvisor.client.util.VegAdvisorActivity;

public class LocalizadorActivity extends VegAdvisorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizador);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
