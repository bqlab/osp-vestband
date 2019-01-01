package app.bqlab.vestband;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Objects;

public class PwsearchLayout extends LinearLayout {

    public PwsearchLayout(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_pwsearch, this);
        findViewById(R.id.pwsearch_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FrameLayout) getParent()).removeView(PwsearchLayout.this);
            }
        });
        findViewById(R.id.pwsearch_send).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = ((EditText) findViewById(R.id.pwsearch_email)).getText().toString();
                if (Objects.equals(getContext().getSharedPreferences("idpw", Context.MODE_PRIVATE).getString(id, "none"), "none") || !id.contains("e"))
                    Toast.makeText(getContext(), "이메일을 다시 확인하세요.", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getContext(), "안내 메일을 발송했습니다.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
