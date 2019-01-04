package app.bqlab.vestband;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BLUETOOTH = 0;
    private static final int REQUEST_DISCOVERABLE = 1;
    private static final int ACCESS_COARSE_LOCATION = 2;
    String id;
    BluetoothSPP bluetoothSPP;
    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        startBluetoothService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_ENABLE_BLUETOOTH:
                    Log.d("Request", "Bluetooth denied");
                    break;
                case REQUEST_DISCOVERABLE:
                    Log.d("Request", "Discoverable denied");
                    break;
                case ACCESS_COARSE_LOCATION:
                    Log.d("Request", "Location permission denied");
            }
        } else {
            switch (requestCode) {
                case REQUEST_ENABLE_BLUETOOTH:
                    startBluetoothService();
                    break;
                case REQUEST_DISCOVERABLE:
                    Log.d("Discoverable", Integer.toString(bluetoothAdapter.getScanMode()));
                    startBluetoothService();
                    break;
                case BluetoothState.REQUEST_CONNECT_DEVICE:
                    bluetoothSPP.connect(data);
                    break;
            }
        }
    }

    private void init() {
        id = getIntent().getStringExtra("id");
        bluetoothSPP = new BluetoothSPP(MainActivity.this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void startBluetoothService() {
        bluetoothSPP.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                BluetoothService.connected = true;
            }

            @Override
            public void onDeviceDisconnected() {
                BluetoothService.connected = false;
                Log.d("Connection", "Disconnected");
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("디바이스와 연결할 수 없습니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                        }).show();
            }

            @Override
            public void onDeviceConnectionFailed() {
                Log.d("Connection", "Failed");
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("디바이스와 연결할 수 없습니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                        }).show();
            }
        });
        bluetoothSPP.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                BluetoothService.degree = Integer.parseInt(message);
            }
        });
        if (!bluetoothSPP.isBluetoothAvailable()) {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("지원하지 않는 기기입니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finishAffinity();
                        }
                    }).show();
        } else if (!bluetoothSPP.isBluetoothEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BLUETOOTH);
        } else if (!bluetoothSPP.isServiceAvailable()) {
            bluetoothSPP.setupService();
            bluetoothSPP.startService(BluetoothState.DEVICE_OTHER);
            startBluetoothService();
        } else if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), REQUEST_DISCOVERABLE);
        } else if (!BluetoothService.connected) {
            registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
            pairedDevices = bluetoothAdapter.getBondedDevices();
            if (bluetoothAdapter.isDiscovering())
                bluetoothAdapter.cancelDiscovery();
            bluetoothAdapter.startDiscovery();
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("Discovery", device.getName());
                if (device.getName().equals("Spine Up")) {
                    bluetoothAdapter.cancelDiscovery();
                    BluetoothService.device = device;
                    bluetoothSPP.connect(BluetoothService.device.getAddress());
                    MainActivity.this.unregisterReceiver(broadcastReceiver);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("Discovery", "Finished");
                new AlertDialog.Builder(context)
                        .setMessage("장치를 찾을 수 없습니다.")
                        .setCancelable(false)
                        .setPositiveButton("재시도", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (bluetoothAdapter.isDiscovering())
                                    bluetoothAdapter.cancelDiscovery();
                                bluetoothAdapter.startDiscovery();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.unregisterReceiver(broadcastReceiver);
                                dialog.dismiss();
                            }
                        }).show();
            }
        }
    };
}
