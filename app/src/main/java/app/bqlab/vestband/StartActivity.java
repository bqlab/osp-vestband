package app.bqlab.vestband;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    String

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        init();
    }

    private void init() {
        findViewById(R.id.login_main_bot_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
            }
        });
        findViewById(R.id.login_main_bot_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.login_main).setVisibility(View.GONE);
                findViewById(R.id.login_login).setVisibility(View.GONE);
            }
        });
    }
}
