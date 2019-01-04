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
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BLUETOOTH = 0;
    private static final int REQUEST_DISCOVERABLE = 1;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSPP bluetoothSPP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        connectDevice();
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
            }
        } else {
            switch (requestCode) {
                case REQUEST_ENABLE_BLUETOOTH:
                    searchDevice();
                    break;
                case REQUEST_DISCOVERABLE:
                    searchDevice();
                    break;
                case BluetoothState.REQUEST_CONNECT_DEVICE:
                    bluetoothSPP.connect(data);
                    break;
            }
        }
    }

    private void init() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothSPP = new BluetoothSPP(MainActivity.this);
        findViewById(R.id.main_bar_dashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutByIndex(0);
            }
        });
        findViewById(R.id.main_bar_analisys).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutByIndex(1);
            }
        });
        findViewById(R.id.main_bar_stretch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutByIndex(2);
            }
        });
        findViewById(R.id.main_bar_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutByIndex(3);
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
                Toast.makeText(MainActivity.this, "연결되었습니다.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDeviceDisconnected() {
                BluetoothService.isConnected = false;
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("디바이스와 연결할 수 없습니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }

            @Override
            public void onDeviceConnectionFailed() {
                BluetoothService.isConnected = false;
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("디바이스와 연결할 수 없습니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        if (!BluetoothService.isConnected)
            searchDevice();
    }

    private void searchDevice() {
        if (!bluetoothSPP.isBluetoothEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BLUETOOTH);
        } else if (!bluetoothSPP.isServiceAvailable()) {
            bluetoothSPP.setupService();
            bluetoothSPP.startService(BluetoothState.DEVICE_OTHER);
            searchDevice();
        } else if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), REQUEST_DISCOVERABLE);
        } else if (!BluetoothService.isConnected) {
            registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
            if (bluetoothAdapter.isDiscovering())
                bluetoothAdapter.cancelDiscovery();
            bluetoothAdapter.startDiscovery();
        }
    }

    private void getLayoutByIndex(int idx) {
        FrameLayout main = findViewById(R.id.main);
        LinearLayout mainBar = findViewById(R.id.main_bar);
        for (int i = 0; i < main.getChildCount(); i++)
            main.getChildAt(i).setVisibility(View.GONE);
        main.getChildAt(idx).setVisibility(View.VISIBLE);
        if (idx < 4) {
            switch (idx) {
                case 0:
                    ((LinearLayout)mainBar.getChildAt(0)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_dashboard_p));
                    ((LinearLayout)mainBar.getChildAt(1)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_analisys_np));
                    ((LinearLayout)mainBar.getChildAt(2)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_stretch_np));
                    ((LinearLayout)mainBar.getChildAt(3)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_setting_np));
                    break;
                case 1:
                    ((LinearLayout)mainBar.getChildAt(0)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_dashboard_np));
                    ((LinearLayout)mainBar.getChildAt(1)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_analisys_p));
                    ((LinearLayout)mainBar.getChildAt(2)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_stretch_np));
                    ((LinearLayout)mainBar.getChildAt(3)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_setting_np));
                    break;
                case 2:
                    ((LinearLayout)mainBar.getChildAt(0)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_dashboard_np));
                    ((LinearLayout)mainBar.getChildAt(1)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_analisys_np));
                    ((LinearLayout)mainBar.getChildAt(2)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_stretch_p));
                    ((LinearLayout)mainBar.getChildAt(3)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_setting_np));
                    break;
                case 3:
                    ((LinearLayout)mainBar.getChildAt(0)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_dashboard_np));
                    ((LinearLayout)mainBar.getChildAt(1)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_analisys_np));
                    ((LinearLayout)mainBar.getChildAt(2)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_stretch_np));
                    ((LinearLayout)mainBar.getChildAt(3)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_setting_p));
                    break;
            }
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                try {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d("Discovery", device.getName());
                    if (device.getName().equals("Spine Up")) {
                        bluetoothAdapter.cancelDiscovery();
                        BluetoothService.device = device;
                        bluetoothSPP.connect(BluetoothService.device.getAddress());
                        MainActivity.this.unregisterReceiver(broadcastReceiver);
                    }
                } catch (Exception e) {
                    bluetoothAdapter.cancelDiscovery();
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
                                dialog.dismiss();
                            }
                        }).show();
            }
        }
    };
}
