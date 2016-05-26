package com.auth0.authsados.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.auth0.authsados.R;

/**
 * Created by lbalmaceda on 5/25/16.
 */
public class AsadoVH extends RecyclerView.ViewHolder {
    public TextView dateTextView;
    public TextView locationTextView;
    public TextView assistantCountTextView;

    public AsadoVH(View itemView) {
        super(itemView);
        dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
        locationTextView = (TextView) itemView.findViewById(R.id.locationTextView);
        assistantCountTextView = (TextView) itemView.findViewById(R.id.assistantsCountTextView);
    }
}
