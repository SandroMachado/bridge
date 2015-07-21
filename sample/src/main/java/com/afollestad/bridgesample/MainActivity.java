package com.afollestad.bridgesample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.afollestad.bridge.Bridge;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setLayoutManager(new GridLayoutManager(this, 8));
        list.setAdapter(new MainAdapter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bridge.client().cancelAll();
    }
}
