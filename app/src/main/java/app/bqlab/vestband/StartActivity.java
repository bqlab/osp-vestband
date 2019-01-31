package app.bqlab.vestband;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    boolean mBackClickDelay = false;
    SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        init();
    }

    @Override
    public void onBackPressed() {
        if (!mBackClickDelay) {
            Toast.makeText(this, "한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
            mBackClickDelay = true;
        } else {
            super.onBackPressed();
            ActivityCompat.finishAffinity(this);
        }
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {

            }

            public void onFinish() {
                mBackClickDelay = false;
            }
        }.start();
    }

    private void init() {
        mPreferences = getSharedPreferences("setting", MODE_PRIVATE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        findViewById(R.id.start_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("setting", MODE_PRIVATE).edit().putBoolean("first", false).apply();
                startActivity(new Intent(StartActivity.this, InitialActivity.class));
            }
        });
        findViewById(R.id.start_image).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPreferences.edit().putBoolean("developer", !mPreferences.getBoolean("developer", false)).apply();
                Toast.makeText(StartActivity.this, "개발자 모드 : " + Boolean.toString(mPreferences.getBoolean("developer", false)), Toast.LENGTH_LONG).show();
                return true;
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
