package app.bqlab.vestband;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class PwsearchLayout extends LinearLayout {

    public PwsearchLayout(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_pwsearch, this);
    }
}
