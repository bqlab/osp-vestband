package app.bqlab.vestband;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class InitialActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BLUETOOTH = 0;
    private static final int REQUEST_DISCOVERABLE = 1;
    private static final int ACCESS_COARSE_LOCATION = 2;
    android.support.v7.app.ActionBar actionBar;
    BluetoothSPP bluetoothSPP;
    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        init();
        if (getIntent().getBooleanExtra("thirdProgress", false))
            thirdProgress();
        else
            firstProgress();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_ENABLE_BLUETOOTH:
                    Toast.makeText(InitialActivity.this, "준비가 끝나면 다시 시도하세요.", Toast.LENGTH_LONG).show();
                    firstProgress();
                    break;
                case REQUEST_DISCOVERABLE:
                    Toast.makeText(InitialActivity.this, "준비가 끝나면 다시 시도하세요.", Toast.LENGTH_LONG).show();
                    firstProgress();
                    break;
                case ACCESS_COARSE_LOCATION:
                    Toast.makeText(InitialActivity.this, "준비가 끝나면 다시 시도하세요.", Toast.LENGTH_LONG).show();
                    firstProgress();
            }
        } else {
            switch (requestCode) {
                case REQUEST_ENABLE_BLUETOOTH:
                    secondProgress();
                    break;
                case REQUEST_DISCOVERABLE:
                    Log.d("Discoverable", Integer.toString(bluetoothAdapter.getScanMode()));
                    secondProgress();
                    break;
                case BluetoothState.REQUEST_CONNECT_DEVICE:
                    bluetoothSPP.connect(data);
                    break;
            }
        }
    }

    private void init() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION);
        }
        actionBar = getSupportActionBar();
        bluetoothSPP = new BluetoothSPP(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void firstProgress() {
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.initial_actionbar);
        ((TextView) findViewById(R.id.initial_actionbar)).setText(getResources().getString(R.string.initial_first_title));
        findViewById(R.id.initial_first).setVisibility(View.VISIBLE);
        findViewById(R.id.initial_second).setVisibility(View.GONE);
        findViewById(R.id.initial_third).setVisibility(View.GONE);
        findViewById(R.id.initial_fourth).setVisibility(View.GONE);
        findViewById(R.id.initial_fifth).setVisibility(View.GONE);
        ((CheckBox) findViewById(R.id.initial_first_check1)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (((CheckBox) findViewById(R.id.initial_first_check2)).isChecked())
                        findViewById(R.id.initial_first_button).setBackground(getDrawable(R.drawable.app_button_black));
                } else {
                    if (((CheckBox) findViewById(R.id.initial_first_check2)).isChecked())
                        findViewById(R.id.initial_first_button).setBackground(getDrawable(R.drawable.app_button_gray));
                }
            }
        });
        ((CheckBox) findViewById(R.id.initial_first_check2)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (((CheckBox) findViewById(R.id.initial_first_check1)).isChecked())
                        findViewById(R.id.initial_first_button).setBackground(getDrawable(R.drawable.app_button_black));
                } else {
                    if (((CheckBox) findViewById(R.id.initial_first_check1)).isChecked())
                        findViewById(R.id.initial_first_button).setBackground(getDrawable(R.drawable.app_button_gray));
                }
            }
        });
        findViewById(R.id.initial_first_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) findViewById(R.id.initial_first_check1)).isChecked()
                        && ((CheckBox) findViewById(R.id.initial_first_check2)).isChecked())
                    secondProgress();
            }
        });
    }

    private void secondProgress() {
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.initial_actionbar);
        ((TextView) findViewById(R.id.initial_actionbar)).setText(getResources().getString(R.string.initial_second_title));
        findViewById(R.id.initial_first).setVisibility(View.GONE);
        findViewById(R.id.initial_second).setVisibility(View.VISIBLE);
        findViewById(R.id.initial_third).setVisibility(View.GONE);
        findViewById(R.id.initial_fourth).setVisibility(View.GONE);
        findViewById(R.id.initial_fifth).setVisibility(View.GONE);
        findViewById(R.id.initial_second_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BluetoothService.isConnected) {
                    bluetoothSPP.stopService();
                    thirdProgress();
                }
            }
        });
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
                startService(new Intent(InitialActivity.this, BluetoothService.class));
                ((Button) findViewById(R.id.initial_second_button)).setText(getResources().getString(R.string.initial_second_button3));
                ((Button) findViewById(R.id.initial_second_button)).setBackground(getResources().getDrawable(R.drawable.app_button_black));
                thirdProgress();
            }

            @Override
            public void onDeviceDisconnected() {
                BluetoothService.isConnected = false;
                Log.d("Connection", "Disconnected");
                new AlertDialog.Builder(InitialActivity.this)
                        .setMessage("디바이스와 연결할 수 없습니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                firstProgress();
                            }
                        }).show();
            }

            @Override
            public void onDeviceConnectionFailed() {
                Log.d("Connection", "Failed");
                new AlertDialog.Builder(InitialActivity.this)
                        .setMessage("디바이스와 연결할 수 없습니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                firstProgress();
                            }
                        }).show();
            }
        });
        if (!bluetoothSPP.isBluetoothAvailable()) {
            new AlertDialog.Builder(InitialActivity.this)
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
            secondProgress();
        } else if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), REQUEST_DISCOVERABLE);
        } else if (!BluetoothService.isConnected) {
            registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
            pairedDevices = bluetoothAdapter.getBondedDevices();
            if (bluetoothAdapter.isDiscovering())
                bluetoothAdapter.cancelDiscovery();
            bluetoothAdapter.startDiscovery();
        }
    }

    private void thirdProgress() {
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.initial_actionbar);
        ((TextView) findViewById(R.id.initial_actionbar)).setText(getResources().getString(R.string.initial_third_title));
        findViewById(R.id.initial_first).setVisibility(View.GONE);
        findViewById(R.id.initial_second).setVisibility(View.GONE);
        findViewById(R.id.initial_third).setVisibility(View.VISIBLE);
        findViewById(R.id.initial_fourth).setVisibility(View.GONE);
        findViewById(R.id.initial_fifth).setVisibility(View.GONE);
        findViewById(R.id.initial_third_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("setting", MODE_PRIVATE).edit().putInt("right", BluetoothService.degree).apply();
                Log.d("Right", Integer.toString(BluetoothService.degree));
                fourthProgress();
            }
        });
    }

    private void fourthProgress() {
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.initial_actionbar);
        ((TextView) findViewById(R.id.initial_actionbar)).setText(getResources().getString(R.string.initial_fourth_title));
        findViewById(R.id.initial_first).setVisibility(View.GONE);
        findViewById(R.id.initial_second).setVisibility(View.GONE);
        findViewById(R.id.initial_third).setVisibility(View.GONE);
        findViewById(R.id.initial_fourth).setVisibility(View.VISIBLE);
        findViewById(R.id.initial_fifth).setVisibility(View.GONE);
        findViewById(R.id.initial_fourth_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("setting", MODE_PRIVATE).edit().putInt("bad", BluetoothService.degree).apply();
                Log.d("Bad", Integer.toString(BluetoothService.degree));
                fifthProgress();
            }
        });
    }

    private void fifthProgress() {
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.initial_actionbar);
        ((TextView) findViewById(R.id.initial_actionbar)).setText(getResources().getString(R.string.initial_fifth_title));
        findViewById(R.id.initial_first).setVisibility(View.GONE);
        findViewById(R.id.initial_second).setVisibility(View.GONE);
        findViewById(R.id.initial_third).setVisibility(View.GONE);
        findViewById(R.id.initial_fourth).setVisibility(View.GONE);
        findViewById(R.id.initial_fifth).setVisibility(View.VISIBLE);
        final NumberPicker numberPicker = findViewById(R.id.initial_fifth_picker);
        numberPicker.setMaxValue(2);
        numberPicker.setMinValue(0);
        numberPicker.setDisplayedValues(new String[]{"즉시", "5초", "10초"});
        findViewById(R.id.initial_fifth_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(InitialActivity.this)
                        .setTitle("이 자세로 설정하시겠습니까?")
                        .setMessage("지금 설정한 내용을 기준으로 향후 자세를 분석합니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int notifyTime = numberPicker.getValue() * 5;
                                Log.d("notifyTime", Integer.toString(notifyTime));
                                getSharedPreferences("setting", MODE_PRIVATE).edit().putInt("notifyTime", notifyTime).apply();
                                if (!getIntent().getBooleanExtra("thirdProgress", false))
                                    startActivity(new Intent(InitialActivity.this, MainActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        getSharedPreferences("setting", MODE_PRIVATE).edit().putBoolean("first", false).apply();
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
                        ((Button) findViewById(R.id.initial_second_button)).setText(getResources().getString(R.string.initial_second_button2));
                        bluetoothSPP.connect(BluetoothService.device.getAddress());
                        InitialActivity.this.unregisterReceiver(broadcastReceiver);
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
                                Toast.makeText(InitialActivity.this, "준비가 끝나면 다시 시도하세요.", Toast.LENGTH_LONG).show();
                                InitialActivity.this.unregisterReceiver(broadcastReceiver);
                                firstProgress();
                            }
                        }).show();
            }
        }
    };
}
