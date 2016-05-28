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
import android.widget.Toast;

import com.auth0.authsados.pojo.Asado;
import com.auth0.authsados.pojo.DatePoll;
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

    private void saveAsado(final Asado asado, List<DatePoll> suggestedDates) {

        final DatabaseReference newAsadoRef = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.ROOT_ASADOS)
                .push();
        if (suggestedDates.size() == 1) {
            asado.selectedDate = suggestedDates.get(0);
        } else {
            final DatabaseReference pollsRef = FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseConstants.ROOT_DATE_POLLS)
                    .child(newAsadoRef.getKey());

            for (DatePoll dp : suggestedDates) {
                pollsRef.push().setValue(dp);
            }
        }
        newAsadoRef.setValue(asado);

        getFragmentManager().popBackStack();
        Toast.makeText(getActivity(), "Asado created!", Toast.LENGTH_SHORT).show();
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
        Asado asado = new Asado(locationEditText.getText().toString());
        List<DatePoll> polls = new ArrayList<>();
        if (!pickedDate1.getText().toString().isEmpty()) {
            polls.add(new DatePoll(pickedDate1.getText().toString()));
        }
        if (!pickedDate2.getText().toString().isEmpty()) {
            polls.add(new DatePoll(pickedDate2.getText().toString()));
        }
        if (!pickedDate3.getText().toString().isEmpty()) {
            polls.add(new DatePoll(pickedDate3.getText().toString()));
        }

        saveAsado(asado, polls);
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
