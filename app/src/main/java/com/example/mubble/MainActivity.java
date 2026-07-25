package com.example.mubble;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.mubble.databinding.ActivityMainBinding;
import com.example.mubble.databinding.ActivitySplashBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showHome();
        setupButtonMenu();

    }
    private void setupButtonMenu(){
        binding.bottomNav.setOnItemSelectedListener(item->{
            int id= item.getItemId();
            if(id==R.id.nav_home){
                showHome();
                return true;
            }
            if(id==R.id.nav_listen){
                showListen();
                return true;}

            if(id==R.id.nav_profile){
                showProfile();
                return true;
            }
            return false;
        });
    }
    private void showHome(){
        binding.frameHome.setVisibility(View.VISIBLE);
        binding.frameProfile.setVisibility(View.GONE);
        binding.frameListen.setVisibility(View.GONE);

    }
    private void showProfile(){
        binding.frameHome.setVisibility(View.GONE);
        binding.frameProfile.setVisibility(View.VISIBLE);
        binding.frameListen.setVisibility(View.GONE);

    }
    private void showListen(){
        binding.frameHome.setVisibility(View.GONE);
        binding.frameProfile.setVisibility(View.GONE);
        binding.frameListen.setVisibility(View.VISIBLE);

    }
}