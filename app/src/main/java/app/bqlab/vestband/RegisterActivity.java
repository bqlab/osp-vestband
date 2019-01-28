package app.bqlab.vestband;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    String id, pw, pw2, name, sex, birth, register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        sex = "남자";
        findViewById(R.id.register_sex_male).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = "남자";
                ((Button) findViewById(R.id.register_sex_male)).setBackground(getResources().getDrawable(R.drawable.app_button_gray));
                ((Button) findViewById(R.id.register_sex_male)).setTextColor(getResources().getColor(R.color.colorWhite));
                ((Button) findViewById(R.id.register_sex_female)).setBackground(getResources().getDrawable(R.drawable.app_button_white));
                ((Button) findViewById(R.id.register_sex_female)).setTextColor(getResources().getColor(R.color.colorGray));
            }
        });
        findViewById(R.id.register_sex_female).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = "여자";
                ((Button) findViewById(R.id.register_sex_female)).setBackground(getResources().getDrawable(R.drawable.app_button_gray));
                ((Button) findViewById(R.id.register_sex_female)).setTextColor(getResources().getColor(R.color.colorWhite));
                ((Button) findViewById(R.id.register_sex_male)).setBackground(getResources().getDrawable(R.drawable.app_button_white));
                ((Button) findViewById(R.id.register_sex_male)).setTextColor(getResources().getColor(R.color.colorGray));
            }
        });
        ((RelativeLayout) findViewById(R.id.register_birth)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePicker datePicker = new DatePicker(RegisterActivity.this);
                datePicker.setCalendarViewShown(false);
                new AlertDialog.Builder(RegisterActivity.this)
                        .setView(datePicker)
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                                Date date = calendar.getTime();
                                birth = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA).format(date);
                                ((TextView) findViewById(R.id.register_birth_day)).setText(birth);
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


        
        findViewById(R.id.register_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = ((EditText) findViewById(R.id.register_id)).getText().toString().toLowerCase();
                name = ((EditText) findViewById(R.id.register_name)).getText().toString();
                pw = ((EditText) findViewById(R.id.register_pw)).getText().toString();
                pw2 = ((EditText) findViewById(R.id.register_pw2)).getText().toString();
                if (!isEmailFormat(id)) {
                    Toast.makeText(RegisterActivity.this, "이메일을 다시 확인해주세요.", Toast.LENGTH_LONG).show();
                } else if (!Objects.equals(getSharedPreferences("idpw", MODE_PRIVATE).getString(id, "none"), "none")) {
                    Toast.makeText(RegisterActivity.this, "이미 가입된 이메일입니다.", Toast.LENGTH_LONG).show();
                } else if (pw.length() < 8) {
                    Toast.makeText(RegisterActivity.this, "비밀번호가 8자 이하입니다.", Toast.LENGTH_LONG).show();
                } else if (!pw.equals(pw2)) {
                    Toast.makeText(RegisterActivity.this, "비밀번호를 다시 확인해주세요.", Toast.LENGTH_LONG).show();
                } else if (!((CheckBox) findViewById(R.id.register_agree)).isChecked()) {
                    Toast.makeText(RegisterActivity.this, "회원 약관을 동의해야 합니다.", Toast.LENGTH_LONG).show();
                } else if (id.isEmpty() || name.isEmpty() || pw.isEmpty() || pw2.isEmpty() || sex.isEmpty() || birth == null) {
                    Toast.makeText(RegisterActivity.this, "빈칸이 있는지 다시 확인해주세요.", Toast.LENGTH_LONG).show();
                } else {
                    register = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA).format(new Date());
                    getSharedPreferences("idpw", MODE_PRIVATE).edit().putString(id, pw).apply();
                    getSharedPreferences("name", MODE_PRIVATE).edit().putString(id, name).apply();
                    getSharedPreferences("sex", MODE_PRIVATE).edit().putString(id, sex).apply();
                    getSharedPreferences("name", MODE_PRIVATE).edit().putString(id, name).apply();
                    getSharedPreferences("birth", MODE_PRIVATE).edit().putString(id, birth).apply();
                    getSharedPreferences("register", MODE_PRIVATE).edit().putString(id, register).apply();
                    Toast.makeText(RegisterActivity.this, "회원 가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                    i.putExtra("id", id);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    private boolean isEmailFormat(String email) {
        String s = "abcdefghijklmnopqrstuvwxyz0123456789@.";
        for (int i = 0; i < email.length(); i++) {
            for (int j = 0; j < s.length(); j++) {
                if (email.charAt(i) == s.charAt(j))
                    break;
                if (j == s.length() - 1 && email.charAt(i) != s.charAt(j)) {
                    Toast.makeText(RegisterActivity.this, "dd", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }
        return id.contains("@") && id.contains(".");
    }
}
