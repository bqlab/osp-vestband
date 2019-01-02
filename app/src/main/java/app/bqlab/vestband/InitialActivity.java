package app.bqlab.vestband;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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

public class InitialActivity extends AppCompatActivity {

    final int REQUEST_ENABLE_BLUETOOTH = 0;
    boolean isConnected = false;
    BluetoothAdapter bluetoothAdapter;

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
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(InitialActivity.this, "지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
            finishAffinity();
        } else if (!bluetoothAdapter.isEnabled())
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BLUETOOTH);
        else if (!isConnected) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (!pairedDevices.isEmpty()) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals("Spine Up")) {
                        new ConnectThread(device).run();
                    }
                }
            }
        }
    }

    private void fifthProgress() {
        getSharedPreferences("flag", MODE_PRIVATE).edit().putBoolean("first", false).apply();
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket socket;
        private BluetoothDevice device;
        public ConnectThread(BluetoothDevice device) {
            try {
                this.device = device;
                this.socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void run() {
            try {
                socket.connect();
                
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread extends Thread {
        InputStream inputStream;
        OutputStream outputStream;
        public ConnectedThread(BluetoothSocket socket, String socketType) {
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while(true) {
                try {
                    bytes = inputStream.read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
