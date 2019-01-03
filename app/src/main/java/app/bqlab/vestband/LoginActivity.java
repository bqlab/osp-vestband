package app.bqlab.vestband;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        ((EditText)findViewById(R.id.login_id)).setText(getIntent().getStringExtra("id"));
        findViewById(R.id.login_find).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout login = findViewById(R.id.login);
                for (int i = 0; i < login.getChildCount(); i++) {
                    login.getChildAt(i).setClickable(false);
                    login.getChildAt(i).setFocusable(false);
                }
                login.addView(new PwsearchLayout(LoginActivity.this));
            }
        });
        findViewById(R.id.login_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = ((EditText) findViewById(R.id.login_id)).getText().toString();
                String pw = ((EditText) findViewById(R.id.login_pw)).getText().toString();
                if (!id.contains("@") || id.isEmpty() || pw.isEmpty()) {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setMessage("아이디와 비밀번호를 다시 확인하세요.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                } else {
                    if (!pw.equals(getSharedPreferences("idpw", MODE_PRIVATE).getString(id, "none"))) {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setMessage("아이디와 비밀번호를 다시 확인하세요.")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    } else {
                        if (getSharedPreferences("setting", MODE_PRIVATE).getBoolean("first", true)) {
                            startActivity(new Intent(LoginActivity.this, InitialActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                }
            }
        });
    }
}
