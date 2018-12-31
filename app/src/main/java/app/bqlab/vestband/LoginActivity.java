package app.bqlab.vestband;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        findViewById(R.id.login_find).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make pop-up
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
                                })
                                .setNeutralButton("회원가입", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                                    }
                                }).show();
                    } else {
                        if (getSharedPreferences("flag", MODE_PRIVATE).getBoolean("beginner", true)) {
                            getSharedPreferences("flag", MODE_PRIVATE).edit().putBoolean("beginner", false).apply();
                            startActivity(new Intent(LoginActivity.this, InitialActivity.class));
                        }
                    }
                }
            }
        });
    }
}
