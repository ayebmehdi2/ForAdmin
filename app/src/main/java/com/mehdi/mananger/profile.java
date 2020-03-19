package com.mehdi.mananger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehdi.mananger.databinding.ProfileBinding;

public class profile extends AppCompatActivity {

    ProfileBinding binding;
    FirebaseDatabase database;
    DatabaseReference reference;
    private String uid;
    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.profile);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        uid = preferences.getString("uid", null);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        reference.child("ADMINS").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                JavaUser user = dataSnapshot.getValue(JavaUser.class);
                if (user == null) return;

                if (user.getPhoto() != null){
                    try {
                        Glide.with(profile.this).load(user.getPhoto()).into(binding.imageView);
                    }catch (Exception e){ }
                }else{
                    binding.imageView.setImageResource(R.drawable.ic_account);
                }

                binding.textView.setText(user.getName());

                String type = "Compte " + user.getType();
                binding.textView1.setText(type);

            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) { }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(profile.this);
                SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(profile.this).edit();
                preferences.putString("uid", null);
                preferences.apply();
                startActivity(new Intent(profile.this,  SplachScreen.class));
            }
        });


    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
