package app.bqlab.vestband;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class InitialActivity extends AppCompatActivity {

    final int REQUEST_ENABLE_BLUETOOTH = 0;
    boolean isConnected = false;
    String id;
    BluetoothSPP bluetoothSPP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        firstProgress();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ENABLE_BLUETOOTH:
                    secondProgress();
                    break;
            }
        } else {
            switch (requestCode) {
                case REQUEST_ENABLE_BLUETOOTH:
                    Toast.makeText(InitialActivity.this, "준비가 끝나면 다시 시도하세요.", Toast.LENGTH_LONG).show();
                    firstProgress();
                    break;
            }
        }
    }

    private void firstProgress() {
        id = getIntent().getStringExtra("id");
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
        findViewById(R.id.initial_first).setVisibility(View.GONE);
        findViewById(R.id.initial_second).setVisibility(View.VISIBLE);
        findViewById(R.id.initial_third).setVisibility(View.GONE);
        findViewById(R.id.initial_fourth).setVisibility(View.GONE);
        findViewById(R.id.initial_fifth).setVisibility(View.GONE);
        bluetoothSPP = new BluetoothSPP(this);
        if (!bluetoothSPP.isBluetoothAvailable()) {
            Toast.makeText(InitialActivity.this, "지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
            finishAffinity();
        } else if (!bluetoothSPP.isBluetoothEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BLUETOOTH);
        } else if (!bluetoothSPP.isServiceAvailable()) {
            bluetoothSPP.setupService();
            bluetoothSPP.startService(BluetoothState.DEVICE_OTHER);
            secondProgress();
        } else if (!isConnected) {
            bluetoothSPP.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
                @Override
                public void onDeviceConnected(String name, String address) {
                    isConnected = true;
                    getSharedPreferences("deviceName", MODE_PRIVATE).edit().putString(id, name).apply();
                    getSharedPreferences("deviceAddress", MODE_PRIVATE).edit().putString(id, address).apply();
                    Toast.makeText(InitialActivity.this, "디바이스가 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    thirdProgress();
                }

                @Override
                public void onDeviceDisconnected() {
                    isConnected = false;
                    Toast.makeText(InitialActivity.this, "디바이스와의 연결이 끊겼습니다.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDeviceConnectionFailed() {
                    Toast.makeText(InitialActivity.this, "디바이스를 등록할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            });
            final DeviceList deviceList = new DeviceList();
            new AlertDialog.Builder(InitialActivity.this)
                    .setTitle("디바이스를 선택하세요.")
                    .setView(findViewById(R.id.list_devices))
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("스캔", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    private void thirdProgress() {

    }

    private void fifthProgress() {
        getSharedPreferences("flag", MODE_PRIVATE).edit().putBoolean("first", false).apply();
    }
}
