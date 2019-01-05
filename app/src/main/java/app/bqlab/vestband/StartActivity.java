package app.bqlab.vestband;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    boolean isClickedBackbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        init();
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

    private void init() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        findViewById(R.id.start_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, InitialActivity.class));
                getSharedPreferences("setting", MODE_PRIVATE).edit().putBoolean("first", false).apply();
            }
        });
        findViewById(R.id.start_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
            }
        });
        findViewById(R.id.start_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });
    }
}
