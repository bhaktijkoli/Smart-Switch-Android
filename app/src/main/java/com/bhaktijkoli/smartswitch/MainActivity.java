package com.bhaktijkoli.smartswitch;

import android.app.ProgressDialog;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bhaktijkoli.smartswitch.utils.NsdUtils;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private OkHttpClient okHttpClient = new OkHttpClient();
    private final String TAG = "TAG";

    private NsdUtils mNsdUtils;
    private String ipaddress;
    private ProgressDialog progressDialog;
    private ImageButton btnLight1;
    private ImageButton btnLight2;
    private boolean stateLight1;
    private boolean stateLight2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NsdUtils mNsdUtils = new NsdUtils(this);
        mNsdUtils.setNsdUtilListner(new NsdUtils.NsdUtilListner() {
            @Override
            public void onScanStart() {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Searching device...");
                progressDialog.show();
            }

            @Override
            public void onScanStop() {
                progressDialog.dismiss();
            }

            @Override
            public void onDeviceFound(String ipaddress) {
                MainActivity.this.ipaddress = ipaddress;
                Request request = new Request.Builder().url("ws://"+ipaddress+":81").build();
                EchoWebSocketListener echoWebSocketListener = new EchoWebSocketListener();
                WebSocket webSocket = okHttpClient.newWebSocket(request, echoWebSocketListener);
                okHttpClient.dispatcher().executorService().shutdown();
            }
        });
        mNsdUtils.scan();

        btnLight1 = (ImageButton) findViewById(R.id.btnLight1);
        btnLight1.setOnClickListener(this);
        btnLight2 = (ImageButton) findViewById(R.id.btnLight2);
        btnLight2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view==btnLight1) {
            stateLight1 = !stateLight1;
        }
        if(view==btnLight2) {
            stateLight2 = !stateLight2;
        }
        updateUI();
    }

    private void updateUI() {
        if(stateLight1 == true) {
            btnLight1.setImageResource(R.drawable.lights_on);
        } else {
            btnLight1.setImageResource(R.drawable.lights_off);
        }
        if(stateLight2 == true) {
            btnLight2.setImageResource(R.drawable.lights_on);
        } else {
            btnLight2.setImageResource(R.drawable.lights_off);
        }
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            Log.i(TAG, "onOpen: ");

        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            Log.i(TAG, "onMessage: " + text);
            try {
                JSONObject rootJson = new JSONObject(text);
                if(rootJson.getString("type").equals("STATUS")) {
                    JSONObject jsonObject = rootJson.getJSONObject("data");
                    Log.i(TAG, "onMessage: " + jsonObject.getString("1"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
        }
    }

}
