package com.auth0.authsados;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.auth0.authsados.pojo.Asado;
import com.auth0.authsados.pojo.Poll;
import com.auth0.authsados.pojo.PollOption;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by lbalmaceda on 5/25/16.
 */
public class CreateAsadoFragment extends Fragment implements View.OnClickListener {

    private EditText locationEditText;
    private EditText pickedDate1;
    private EditText pickedDate2;
    private EditText pickedDate3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View v = inflater.inflate(R.layout.fragment_create_asado, container, false);
        Button createButton = (Button) v.findViewById(R.id.createButton);
        createButton.setOnClickListener(this);
        Button pickDateButton1 = (Button) v.findViewById(R.id.pickDateButton1);
        Button pickDateButton2 = (Button) v.findViewById(R.id.pickDateButton2);
        Button pickDateButton3 = (Button) v.findViewById(R.id.pickDateButton3);
        pickDateButton1.setOnClickListener(this);
        pickDateButton2.setOnClickListener(this);
        pickDateButton3.setOnClickListener(this);
        pickedDate1 = (EditText) v.findViewById(R.id.pickedDate1);
        pickedDate2 = (EditText) v.findViewById(R.id.pickedDate2);
        pickedDate3 = (EditText) v.findViewById(R.id.pickedDate3);
        locationEditText = (EditText) v.findViewById(R.id.locationEditText);
        return v;
    }

    private void saveAsado(final Asado asado, @Nullable Poll poll) {
        if (poll == null) {
            FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseConstants.ROOT_ASADOS)
                    .push().setValue(asado);
            getFragmentManager().popBackStack();
            return;
        }

        final DatabaseReference pollRef = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.ROOT_POLLS)
                .push();
        asado.datePoll = pollRef.getKey();
        pollRef.setValue(poll, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                String key = databaseReference.getKey();
                if (databaseError != null) {
                    return;
                }
                FirebaseDatabase.getInstance().getReference()
                        .child(FirebaseConstants.ROOT_ASADOS)
                        .push().setValue(asado);
                getFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.createButton:
                createAsado();
                break;
            case R.id.pickDateButton1:
            case R.id.pickDateButton2:
            case R.id.pickDateButton3:
                showDatePicker(id);
                break;
        }
    }

    private void showDatePicker(final int dateTextId) {
        Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, monthOfYear, dayOfMonth);
                String date = String.valueOf(selectedCalendar.getTimeInMillis());
                switch (dateTextId) {
                    case R.id.pickDateButton1:
                        pickedDate1.setText(date);
                        break;
                    case R.id.pickDateButton2:
                        pickedDate2.setText(date);
                        break;
                    case R.id.pickDateButton3:
                        pickedDate3.setText(date);
                        break;
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.DATE, 7);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setCalendarViewShown(true);
        datePickerDialog.getDatePicker().setSpinnersShown(false);
        datePickerDialog.show();
    }

    private void createAsado() {
        if (!validateFields()) {
            return;
        }
        List<String> dates = new ArrayList<>();
        if (!pickedDate1.getText().toString().isEmpty()) {
            dates.add(pickedDate1.getText().toString());
        }
        if (!pickedDate2.getText().toString().isEmpty()) {
            dates.add(pickedDate2.getText().toString());
        }
        if (!pickedDate3.getText().toString().isEmpty()) {
            dates.add(pickedDate3.getText().toString());
        }

        Asado asado;
        Poll poll = null;
        if (dates.size() == 1) {
            asado = new Asado(locationEditText.getText().toString(), dates.get(0));
        } else {
            asado = new Asado(locationEditText.getText().toString());
            List<PollOption> pollOptions = new ArrayList<>();
            for (String date : dates) {
                pollOptions.add(new PollOption(date));
            }
            poll = new Poll(pollOptions);
        }

        saveAsado(asado, poll);
    }

    private boolean validateFields() {
        if (locationEditText.getText().toString().isEmpty()) {
            locationEditText.setError("Cannot be empty!");
            return false;
        }
        if (pickedDate1.getText().toString().isEmpty()) {
            pickedDate1.setError("Must pick 1 date!");
            return false;
        }
        return true;
    }
}
