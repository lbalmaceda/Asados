package com.auth0.authsados;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.auth0.authsados.pojo.Asado;
import com.auth0.authsados.pojo.DatePoll;
import com.auth0.authsados.viewholders.DatePollVH;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by lbalmaceda on 5/25/16.
 */
public class AsadoFragment extends Fragment {

    private static final String TAG = AsadoFragment.class.getSimpleName();

    private TextView dateTextView;
    private TextView locationTextView;
    private TextView assistantsCountTextView;

    private DataSnapshot mLastAsado;
    private DatabaseReference mAsadoRef;
    private ChildEventListener mAsadoListener;
    private FirebaseRecyclerAdapter<DatePoll, DatePollVH> mPollsAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAsadoRef = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.ROOT_ASADOS)
                .limitToFirst(1)
                .getRef();
        mAsadoListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mLastAsado = dataSnapshot;
                removePollAdapter();
                addAsado(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                mLastAsado = dataSnapshot;
                removePollAdapter();
                addAsado(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mLastAsado = null;
                removePollAdapter();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

            private void addAsado(DataSnapshot dataSnapshot) {
                Asado asado = dataSnapshot.getValue(Asado.class);
                Log.e(TAG, "Asado " + dataSnapshot.getKey() + " added on location " + asado.location + " with next date " + asado.selectedDate);
                displayAsado(asado);

//                if (asado.selectedDate != null) {
//                    return;
//                }
                DatabaseReference pollsRef = FirebaseDatabase.getInstance().getReference()
                        .child(FirebaseConstants.ROOT_DATE_POLLS)
                        .child(dataSnapshot.getKey())
                        .orderByChild("date")
                        .getRef();

                updatePollAdapter(pollsRef);
            }

            private void updatePollAdapter(DatabaseReference ref) {
                mPollsAdapter = new FirebaseRecyclerAdapter<DatePoll, DatePollVH>(DatePoll.class, R.layout.item_date_poll, DatePollVH.class, ref) {
                    @Override
                    protected void populateViewHolder(DatePollVH viewHolder, final DatePoll model, final int position) {
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        calendar.setTimeInMillis(Long.parseLong(model.date));
                        String date = sdf.format(calendar.getTime());

                        viewHolder.dateTextView.setText(date);
                        viewHolder.assistantCountTextView.setText(String.format("Assistants: %d", model.assistants.size()));
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                model.toggleVote(userId);
                                getRef(position).setValue(model);
                            }
                        });
                    }
                };

                mRecyclerView.setAdapter(mPollsAdapter);
            }

            private void removePollAdapter() {
                if (mPollsAdapter != null) {
                    mPollsAdapter.cleanup();
                    mPollsAdapter = null;
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAsadoRef.addChildEventListener(mAsadoListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAsadoRef.removeEventListener(mAsadoListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPollsAdapter != null) {
            mPollsAdapter.cleanup();
        }
    }

    private void displayAsado(@Nullable Asado nextAsado) {
        if (nextAsado == null) {
            dateTextView.setText("Not defined yet");
            locationTextView.setText("");
            assistantsCountTextView.setText("");
        } else {
            String date = "Not defined yet";
            assistantsCountTextView.setText("Assistants: 0");
            if (nextAsado.selectedDate != null) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                calendar.setTimeInMillis(Long.parseLong(nextAsado.selectedDate.date));
                date = sdf.format(calendar.getTime());
                assistantsCountTextView.setText(String.format("Assistants: %d", nextAsado.selectedDate.assistants.size()));
            }
            dateTextView.setText(date);
            locationTextView.setText(nextAsado.location);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View v = inflater.inflate(R.layout.fragment_next_asado, container, false);
        Button assistButton = (Button) v.findViewById(R.id.assistButton);
        assistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVoteAsado();
            }
        });
        dateTextView = (TextView) v.findViewById(R.id.dateTextView);
        locationTextView = (TextView) v.findViewById(R.id.locationTextView);
        assistantsCountTextView = (TextView) v.findViewById(R.id.assistantsCountTextView);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.datePolls);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);

        return v;
    }

    private void toggleVoteAsado() {
        Asado asado = mLastAsado.getValue(Asado.class);
        if (asado.selectedDate == null) {
            return;
        }
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        asado.selectedDate.toggleVote(userId);

        mAsadoRef.child(mLastAsado.getKey())
                .child("selectedDate")
                .setValue(asado.selectedDate);
    }

}
