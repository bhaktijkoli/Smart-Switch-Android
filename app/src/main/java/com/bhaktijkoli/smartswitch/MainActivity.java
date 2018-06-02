package com.bhaktijkoli.smartswitch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.bhaktijkoli.smartswitch.utils.NsdUtils;


public class MainActivity extends AppCompatActivity {

    private NsdUtils mNsdUtils;
    private String ipaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NsdUtils mNsdUtils = new NsdUtils(this);
        mNsdUtils.setNsdUtilListner(new NsdUtils.NsdUtilListner() {
            @Override
            public void onScanStart() {

            }

            @Override
            public void onScanStop() {

            }

            @Override
            public void onDeviceFound(String ipaddress) {
                MainActivity.this.ipaddress = ipaddress;
                Toast.makeText(MainActivity.this, "IP: " + ipaddress, Toast.LENGTH_LONG).show();
            }
        });
        mNsdUtils.scan();
    }
}
