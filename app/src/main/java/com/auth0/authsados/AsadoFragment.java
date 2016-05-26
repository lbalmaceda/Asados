package com.auth0.authsados;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.auth0.authsados.pojo.Asado;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by lbalmaceda on 5/25/16.
 */
public class AsadoFragment extends Fragment {

    private DataSnapshot mNextData;
    private TextView dateTextView;
    private TextView locationTextView;
    private DatabaseReference mAsadoRef;
    private ChildEventListener mChildListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAsadoRef = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.ROOT_ASADOS)
                .limitToFirst(1)
                .orderByChild("finalDate")
                .getRef();
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                refreshValue(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                refreshValue(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                refreshValue(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                refreshValue(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

            private void refreshValue(DataSnapshot dataSnapshot) {
                mNextData = dataSnapshot;
                Asado asado = dataSnapshot.getValue(Asado.class);
                displayNextAsado(asado);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAsadoRef.addChildEventListener(mChildListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAsadoRef.removeEventListener(mChildListener);
    }

    private void displayNextAsado(@Nullable Asado nextAsado) {
        if (nextAsado == null) {
            dateTextView.setText("Not defined yet");
            locationTextView.setText("");
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(nextAsado.finalDate));
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            final String date = sdf.format(calendar.getTime());
            dateTextView.setText(date);
            locationTextView.setText(nextAsado.location);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View v = inflater.inflate(R.layout.fragment_next_asado, container, false);
        ImageView assistButton = (ImageView) v.findViewById(R.id.assistButton);
        assistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNextData == null || !mNextData.exists()) {
                    return;
                }
                updateAsado();
//                transactionUpdateAsado();
            }
        });
        dateTextView = (TextView) v.findViewById(R.id.dateTextView);
        locationTextView = (TextView) v.findViewById(R.id.locationTextView);
        return v;
    }

    private void transactionUpdateAsado() {
        mNextData.getRef().runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Asado asado = mutableData.getValue(Asado.class);
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (asado.assistants.containsKey(userId)) {
                    asado.assistants.remove(userId);
                } else {
                    asado.assistants.put(userId, true);
                }
                mutableData.setValue(asado);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });
    }

    private void updateAsado() {
        Asado asado = mNextData.getValue(Asado.class);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (asado.assistants.containsKey(userId)) {
            asado.assistants.remove(userId);
        } else {
            asado.assistants.put(userId, true);
        }
        mNextData.getRef().setValue(asado);
    }

}
