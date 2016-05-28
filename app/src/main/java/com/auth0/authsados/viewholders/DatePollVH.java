package com.auth0.authsados.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.auth0.authsados.R;

/**
 * Created by lbalmaceda on 5/25/16.
 */
public class DatePollVH extends RecyclerView.ViewHolder {
    public TextView dateTextView;
    public TextView assistantCountTextView;

    public DatePollVH(View itemView) {
        super(itemView);
        dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
        assistantCountTextView = (TextView) itemView.findViewById(R.id.assistantsCountTextView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
