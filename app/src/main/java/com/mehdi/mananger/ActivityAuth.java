package com.mehdi.mananger;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class ActivityAuth extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;

    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        auth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@androidx.annotation.NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    startActivity(new Intent(ActivityAuth.this, HomeActivity.class));
                }else {
                    signIn();
                }
            }
        };

    }

    public void signIn(){
        Log.d("SignUpActivity", "user null");
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .setTheme(R.style.LoginTheme)
                        .build(),
                100);
    }


    public void setupUser(final FirebaseUser user){

            final String uid = user.getUid();
            SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(ActivityAuth.this).edit();
            preferences.putString("uid", uid);
            preferences.apply();

            reference.child("ADMINS").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(uid)){
                        startActivity(new Intent(ActivityAuth.this, HomeActivity.class));
                    }else {
                        CompleteCompte.user = user;
                        startActivity(new Intent(ActivityAuth.this, CompleteCompte.class));
                    }
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) { }
            });

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null){
                    startActivity(new Intent(ActivityAuth.this, ActivityAuth.class));
                    return;
                }
                setupUser(user);
            } else {
                Toast.makeText(this, "Sign Filed", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null){
            auth.removeAuthStateListener(authStateListener);
        }

    }
}
