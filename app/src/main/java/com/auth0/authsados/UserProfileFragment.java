package com.auth0.authsados;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.auth0.authsados.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by lbalmaceda on 5/25/16.
 */
public class UserProfileFragment extends Fragment {

    private DatabaseReference mUserRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserRef = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.ROOT_USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        final TextView nameText = (TextView) v.findViewById(R.id.name);
        final RadioGroup genderGroup = (RadioGroup) v.findViewById(R.id.genderGroup);
        final CheckBox adminCheckbox = (CheckBox) v.findViewById(R.id.adminCheckbox);

        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nameText.setText(user.name);
                if ("female".equalsIgnoreCase(user.gender)) {
                    genderGroup.check(R.id.radioGenderFemale);
                } else if ("male".equalsIgnoreCase(user.gender)) {
                    genderGroup.check(R.id.radioGenderMale);
                }
                adminCheckbox.setChecked(user.isAdmin);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioGenderMale) {
                    mUserRef.child(FirebaseConstants.USER_GENDER).setValue("male");
                } else {
                    mUserRef.child(FirebaseConstants.USER_GENDER).setValue("female");
                }
            }
        });

        adminCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUserRef.child(FirebaseConstants.USER_ADMIN).setValue(isChecked);
            }
        });
        return v;
    }

}
