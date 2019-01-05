package app.bqlab.vestband;

import android.annotation.SuppressLint;
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
import android.os.CountDownTimer;
import android.os.Debug;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BLUETOOTH = 0;
    private static final int REQUEST_DISCOVERABLE = 1;
    boolean isClickedBackbutton;
    String totalTimeText, rightTimeText, badTimeText;
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

    @Override
    public void onBackPressed() {
        if (!isClickedBackbutton) {
            Toast.makeText(this, "한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
            isClickedBackbutton = true;
        } else {
            super.onBackPressed();
            ActivityCompat.finishAffinity(this);
        }
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {

            }

            public void onFinish() {
                isClickedBackbutton = false;
            }
        }.start();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothSPP = new BluetoothSPP(MainActivity.this);
        UserService.right = getSharedPreferences("setting", MODE_PRIVATE).getInt("right", 0);
        UserService.bad = getSharedPreferences("setting", MODE_PRIVATE).getInt("bad", 0);
        UserService.totalTime = getSharedPreferences("time", MODE_PRIVATE).getInt("total", 0);
        UserService.rightTime = getSharedPreferences("time", MODE_PRIVATE).getInt("right", 0);
        UserService.badTime = getSharedPreferences("time", MODE_PRIVATE).getInt("bad", 0);
        ((SwipeRefreshLayout) findViewById(R.id.main_refresh_layout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UserService.thread.interrupt();
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                Toast.makeText(MainActivity.this, "새로고침이 완료되었습니다.", Toast.LENGTH_LONG).show();
                ((SwipeRefreshLayout) findViewById(R.id.main_refresh_layout)).setRefreshing(false);
            }
        });
        //main_bar setting
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
        //main_dashboard setting

        PieChart chart = findViewById(R.id.main_dashboard_chart);
        if (getSharedPreferences("time", MODE_PRIVATE).getInt("total", 0) != 0) {
            ArrayList<PieEntry> values = new ArrayList<>();
            chart.setUsePercentValues(true);
            chart.getDescription().setEnabled(false);
            chart.setTouchEnabled(false);
            chart.setTransparentCircleRadius(0f);
            chart.setExtraOffsets(0, 0, 0, 0);
            chart.setDrawSliceText(false);
            chart.setDrawHoleEnabled(true);
            chart.setHoleRadius(90f);
            chart.setHoleColor(getResources().getColor(R.color.colorWhite));
            chart.getLegend().setEnabled(false);
            values.add(new PieEntry(getSharedPreferences("time", MODE_PRIVATE).getInt("bad", 0), "bad"));
            values.add(new PieEntry(getSharedPreferences("time", MODE_PRIVATE).getInt("right", 0)));
            PieDataSet dataSet = new PieDataSet(values, "Data");
            dataSet.setSliceSpace(0f);
            dataSet.setColors(getResources().getColor(R.color.colorRedForChart), getResources().getColor(R.color.colorBlueForChart));
            PieData data = new PieData(dataSet);
            data.setValueTextSize(0f);
            chart.setData(data);
            if (UserService.badTime < UserService.rightTime) {
                ((TextView) findViewById(R.id.main_dashboard_chart_grade)).setTextColor(getResources().getColor(R.color.colorBlueForChart));
                ((TextView) findViewById(R.id.main_dashboard_chart_grade)).setText("GOOD");
                ((TextView) findViewById(R.id.main_dashboard_chart_state)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.main_dashboard_chart_state)).setText("나쁜 자세 " + String.valueOf((double) UserService.badTime / (double) UserService.totalTime * 100) + "%");
            } else {
                ((TextView) findViewById(R.id.main_dashboard_chart_grade)).setTextColor(getResources().getColor(R.color.colorRedForChart));
                ((TextView) findViewById(R.id.main_dashboard_chart_grade)).setText("BAD");
                ((TextView) findViewById(R.id.main_dashboard_chart_state)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.main_dashboard_chart_state)).setText("나쁜 자세 " + String.valueOf((int) ((double) UserService.badTime / (double) UserService.totalTime * 100)) + "%");
            }
            ((TextView) findViewById(R.id.main_dashboard_vibrate_content)).setText(String.valueOf(UserService.badTime) + "회");
        } else {
            ArrayList<PieEntry> values = new ArrayList<>();
            chart.setUsePercentValues(true);
            chart.getDescription().setEnabled(false);
            chart.setTouchEnabled(false);
            chart.setTransparentCircleRadius(0f);
            chart.setExtraOffsets(0, 0, 0, 0);
            chart.setDrawSliceText(false);
            chart.setDrawHoleEnabled(true);
            chart.setHoleRadius(90f);
            chart.setHoleColor(getResources().getColor(R.color.colorWhite));
            chart.getLegend().setEnabled(false);
            values.add(new PieEntry(1f, "no data"));
            PieDataSet dataSet = new PieDataSet(values, "Data");
            dataSet.setSliceSpace(0f);
            dataSet.setColors(getResources().getColor(R.color.colorWhiteDark));
            PieData data = new PieData(dataSet);
            data.setValueTextSize(0f);
            chart.setData(data);
            ((TextView) findViewById(R.id.main_dashboard_chart_grade)).setTextColor(getResources().getColor(R.color.colorWhiteDark));
            ((TextView) findViewById(R.id.main_dashboard_chart_state)).setVisibility(View.GONE);
        }
        if (UserService.totalTime > 3600)
            totalTimeText = String.valueOf(UserService.totalTime / 3600) + "시간 " + String.valueOf((UserService.totalTime % 3600) / 60) + "분";
        else
            totalTimeText = String.valueOf((UserService.totalTime % 3600) / 60) + "분";
        ((TextView) findViewById(R.id.main_dashboard_total_content)).setText(totalTimeText);
        ((TextView) findViewById(R.id.main_analisys_vibrate_content)).setText(String.valueOf(UserService.badTime));
        findViewById(R.id.main_dashboard_analisys).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutByIndex(1);
            }
        });
        findViewById(R.id.main_dashboard_stretch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutByIndex(2);
            }
        });
        //main_analisys setting -> analysis is correct expression
        if (UserService.rightTime > 3600)
            rightTimeText = String.valueOf(UserService.rightTime / 3600) + "시간 " + String.valueOf((UserService.rightTime % 3600) / 60) + "분";
        else
            rightTimeText = String.valueOf((UserService.rightTime % 3600) / 60) + "분";
        if (UserService.badTime > 3600)
            badTimeText = String.valueOf(UserService.badTime / 3600) + "시간 " + String.valueOf((UserService.badTime % 3600) / 60) + "분";
        else
            badTimeText = String.valueOf((UserService.badTime % 3600) / 60) + "분";
        ((TextView) findViewById(R.id.main_analisys_time_content)).setText(String.valueOf(totalTimeText));
        ((TextView) findViewById(R.id.main_analisys_vibrate_content)).setText(String.valueOf(UserService.badTime) + "회");
        ((TextView) findViewById(R.id.main_analisys_right_content)).setText(rightTimeText);
        ((TextView) findViewById(R.id.main_analisys_bad_content)).setText(badTimeText);
        findViewById(R.id.main_analisys_top_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutByIndex(0);
            }
        });
        //main_stretch setting
        findViewById(R.id.main_stretch_top_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutByIndex(0);
            }
        });
        //main_setting setting
        if (UserService.isConnected) {
            ((TextView) findViewById(R.id.main_setting_top_connect_state)).setText("연결 됨");
            findViewById(R.id.main_setting_connect_top_circle).setBackground(getResources().getDrawable(R.drawable.app_blue_circle));
        } else {
            ((TextView) findViewById(R.id.main_setting_top_connect_state)).setText("연결 안됨");
            findViewById(R.id.main_setting_connect_top_circle).setBackground(getResources().getDrawable(R.drawable.app_red_circle));
        }
        findViewById(R.id.main_setting_top_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutByIndex(4);
            }
        });
        findViewById(R.id.main_setting_set_notify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String right = String.valueOf(getSharedPreferences("setting", MODE_PRIVATE).getInt("right", 0)) + "도";
                String bad = String.valueOf(getSharedPreferences("setting", MODE_PRIVATE).getInt("bad", 0)) + "도";
                ((TextView) findViewById(R.id.main_setting_notify_notify_right)).setText(right);
                ((TextView) findViewById(R.id.main_setting_notify_notify_bad)).setText(bad);
                getLayoutByIndex(5);
            }
        });
        findViewById(R.id.main_setting_set_posture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, InitialActivity.class);
                i.putExtra("thirdProgress", true);
                startActivity(i);
            }
        });
        findViewById(R.id.main_setting_my_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutByIndex(6);
            }
        });
        findViewById(R.id.main_setting_my_version).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutByIndex(7);
            }
        });
        findViewById(R.id.main_setting_my_notice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("등록된 공지가 없습니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        //main_setting_connect setting
        if (UserService.isConnected)
            ((TextView) findViewById(R.id.main_setting_connect_state)).setText("연결 됨");
        else
            ((TextView) findViewById(R.id.main_setting_connect_state)).setText("연결 안됨");
        findViewById(R.id.main_setting_connect_top_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutByIndex(3);
            }
        });
        //main_setting_notify setting
        findViewById(R.id.main_setting_notify_top_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutByIndex(3);
            }
        });
        findViewById(R.id.main_setting_notify_notify_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] s = new String[]{"즉시>", "5초>", "10초>"};
                final ArrayAdapter<String> a = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
                a.add("즉시");
                a.add("5초");
                a.add("10초");
                new AlertDialog.Builder(MainActivity.this)
                        .setAdapter(a, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((TextView) findViewById(R.id.main_setting_notify_notify_time)).setText(s[which]);
                                getSharedPreferences("setting", MODE_PRIVATE).edit().putInt("notifyTime", which * 5).apply();
                            }
                        }).show();

            }
        });
        //main_setting_profile setting
        ((TextView) findViewById(R.id.main_setting_profile_email)).setText(UserService.id);
        ((TextView) findViewById(R.id.main_setting_profile_name)).setText(getSharedPreferences("name", MODE_PRIVATE).getString(UserService.id, "none"));
        ((TextView) findViewById(R.id.main_setting_profile_sex)).setText(getSharedPreferences("sex", MODE_PRIVATE).getString(UserService.id, "none"));
        ((TextView) findViewById(R.id.main_setting_profile_birth)).setText(getSharedPreferences("birth", MODE_PRIVATE).getString(UserService.id, "none"));
        ((TextView) findViewById(R.id.main_setting_profile_register)).setText(getSharedPreferences("register", MODE_PRIVATE).getString(UserService.id, "none"));
        findViewById(R.id.main_setting_profile_top_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutByIndex(3);
            }
        });
        findViewById(R.id.main_setting_profile_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "로그아웃되었습니다.", Toast.LENGTH_LONG).show();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.putExtra("id", UserService.id);
                startActivity(i);
                finish();
            }
        });
        findViewById(R.id.main_setting_profile_withdrawal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("서비스를 탈퇴하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "탈퇴되었습니다.", Toast.LENGTH_LONG).show();
                                getSharedPreferences("idpw", MODE_PRIVATE).edit().putString(UserService.id, "none").apply();
                                Intent i = new Intent(MainActivity.this, StartActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }).show();
            }
        });
        //main_setting_version setting
        try {
            ((TextView) findViewById(R.id.main_setting_version_current)).setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
            ((TextView) findViewById(R.id.main_setting_version_latest)).setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
            ((TextView) findViewById(R.id.main_setting_version_using)).setText("최신버전을 사용 중 입니다.\n");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        findViewById(R.id.main_setting_version_top_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLayoutByIndex(3);
            }
        });
        findViewById(R.id.main_setting_version_upgrade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("스토어에서 정보를 불러올 수 없습니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    private void connectDevice() {
        if (!UserService.isConnected) {
            Toast.makeText(this, "장치와 연결되어 있지 않습니다.", Toast.LENGTH_LONG).show();

            bluetoothSPP.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
                @Override
                public void onDataReceived(byte[] data, String message) {
                    UserService.degree = Integer.parseInt(message) - 90;
                }
            });
            bluetoothSPP.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
                @Override
                public void onDeviceConnected(String name, String address) {
                    UserService.isConnected = true;
                    startService(new Intent(MainActivity.this, UserService.class));
                    Toast.makeText(MainActivity.this, "연결되었습니다.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onDeviceDisconnected() {
                    UserService.isConnected = false;
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
                    UserService.isConnected = false;
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
            if (!UserService.isConnected)
                searchDevice();
        }
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
        } else if (!UserService.isConnected) {
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
                    ((LinearLayout) mainBar.getChildAt(0)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_dashboard_p));
                    ((LinearLayout) mainBar.getChildAt(1)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_analisys_np));
                    ((LinearLayout) mainBar.getChildAt(2)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_stretch_np));
                    ((LinearLayout) mainBar.getChildAt(3)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_setting_np));
                    break;
                case 1:
                    ((LinearLayout) mainBar.getChildAt(0)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_dashboard_np));
                    ((LinearLayout) mainBar.getChildAt(1)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_analisys_p));
                    ((LinearLayout) mainBar.getChildAt(2)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_stretch_np));
                    ((LinearLayout) mainBar.getChildAt(3)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_setting_np));
                    break;
                case 2:
                    ((LinearLayout) mainBar.getChildAt(0)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_dashboard_np));
                    ((LinearLayout) mainBar.getChildAt(1)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_analisys_np));
                    ((LinearLayout) mainBar.getChildAt(2)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_stretch_p));
                    ((LinearLayout) mainBar.getChildAt(3)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_setting_np));
                    break;
                case 3:
                    ((LinearLayout) mainBar.getChildAt(0)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_dashboard_np));
                    ((LinearLayout) mainBar.getChildAt(1)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_analisys_np));
                    ((LinearLayout) mainBar.getChildAt(2)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_stretch_np));
                    ((LinearLayout) mainBar.getChildAt(3)).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.main_bar_setting_p));
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
                        UserService.device = device;
                        bluetoothSPP.connect(UserService.device.getAddress());
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
