package com.example.mubble;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mubble.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            FirebaseAuth auth = FirebaseAuth.getInstance();

            Intent intent;

            if (auth.getCurrentUser() != null) {
                intent = new Intent(
                        SplashActivity.this,
                        MainActivity.class
                );
            } else {
                intent = new Intent(
                        SplashActivity.this,
                        LoginActivity.class
                );
            }

            startActivity(intent);

            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );

            finish();

        }, 1000);
    }
}