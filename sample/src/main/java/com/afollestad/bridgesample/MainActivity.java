package com.afollestad.bridgesample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.Callback;
import com.afollestad.bridge.Request;
import com.afollestad.bridge.RequestException;
import com.afollestad.bridge.Response;

import org.json.JSONObject;

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

        Bridge.client().host("https://demoapi.salesfitness.net");
        JSONObject body;
        try {
            body = new JSONObject("{\"password\":\"IamAidan18\",\"userName\":\"Young\",\"deviceToken\":\"DefaultDeviceToken\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Bridge.client().post("/users/auth")
                .body(body)
                .request(new Callback() {
                    @Override
                    public void response(Request request, Response response, RequestException e) {
                        Log.v("Done", "Done");
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bridge.client().cancelAll();
    }
}
