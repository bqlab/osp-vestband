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
        init();
    }

    private void init() {
        findViewById(R.id.pwsearch_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout p = ((FrameLayout) getParent());
                p.removeView(PwsearchLayout.this);
                for (int i = 0; i < p.getChildCount(); i++) {
                    p.getChildAt(i).setClickable(true);
                    p.getChildAt(i).setFocusable(true);
                }
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
