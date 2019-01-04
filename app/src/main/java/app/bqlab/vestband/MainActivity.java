package app.bqlab.vestband;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class MainActivity extends AppCompatActivity {

    BluetoothSPP bluetoothSPP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        connectDevice();
    }

    private void init() {
        bluetoothSPP = new BluetoothSPP(MainActivity.this);
        bluetoothSPP.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                BluetoothService.degree = Integer.parseInt(message);
            }
        });
        if (!BluetoothService.isConnected)
            Toast.makeText(this, "장치와 연결되어 있지 않습니다.", Toast.LENGTH_LONG).show();
    }

    private void connectDevice() {
        bluetoothSPP.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                BluetoothService.degree = Integer.parseInt(message);
            }
        });
        bluetoothSPP.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                BluetoothService.isConnected = true;
                startService(new Intent(MainActivity.this, BluetoothService.class));
            }

            @Override
            public void onDeviceDisconnected() {

            }

            @Override
            public void onDeviceConnectionFailed() {

            }
        });
    }
}
