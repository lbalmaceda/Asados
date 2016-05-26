package com.auth0.authsados;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.auth0.authsados.pojo.Asado;
import com.auth0.authsados.viewholders.AsadoVH;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lbalmaceda on 5/25/16.
 */
public class AsadoListFragment extends Fragment {

    private DatabaseReference mAsadosRef;
    private FirebaseRecyclerAdapter<Asado, AsadoVH> mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAsadosRef = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.ROOT_ASADOS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View v = inflater.inflate(R.layout.fragment_asados, container, false);
        RecyclerView recycler = (RecyclerView) v.findViewById(R.id.recyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setHasFixedSize(true);
        mAdapter = new FirebaseRecyclerAdapter<Asado, AsadoVH>(Asado.class, R.layout.item_asado, AsadoVH.class, mAsadosRef) {
            @Override
            protected void populateViewHolder(AsadoVH holder, Asado model, int position) {
                String finalDate = model.finalDate;
                if (finalDate == null) {
                    finalDate = "Not defined yet..";
                }
                holder.dateTextView.setText(String.format("Final date: %s", finalDate));
                holder.locationTextView.setText(String.format("Location: %s", model.location));
                holder.assistantCountTextView.setText(String.format("Assistants: %d", model.assistants.size()));
            }
        };

        recycler.setAdapter(mAdapter);
        Button addButton = (Button) v.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFragment();
            }
        });
        return v;
    }

    private void showAddFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContent, new CreateAsadoFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}
