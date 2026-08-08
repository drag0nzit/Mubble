package com.example.mubble;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mubble.adapters.TrackAdapter;
import com.example.mubble.database.FirestoreManager;
import com.example.mubble.databinding.ActivityMainBinding;
import com.example.mubble.databinding.ActivitySplashBinding;
import com.example.mubble.models.Track;
import com.example.mubble.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private RecyclerView rvTracks;
    private TrackAdapter trackAdapter;
    private ArrayList<Track> trackList;
    private FirestoreManager firestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserProfile();

        showHome();
        setupButtonMenu();

        rvTracks = findViewById(R.id.rvTracks);

        trackList = new ArrayList<>();

        trackAdapter = new TrackAdapter(trackList);

        rvTracks.setLayoutManager(new LinearLayoutManager(this));
        rvTracks.setAdapter(trackAdapter);

        firestoreManager = new FirestoreManager();

        loadTracks();



        binding.profileLayout.logoutButton.setOnClickListener(v -> {

            auth.signOut();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finishAffinity();


        });

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

    private void loadUserProfile() {

        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null)
            return;

        db.collection("users")
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        User user = documentSnapshot.toObject(User.class);

                        if (user != null) {

                            binding.profileLayout.profileName.setText(user.getUsername());
                            binding.profileLayout.profileEmail.setText(user.getEmail());

                        }

                    }

                });

    }
    private void loadTracks() {

        firestoreManager.getTracks(tracks -> {
            trackList.clear();
            trackList.addAll(tracks);
            trackAdapter.notifyDataSetChanged();

        });

    }
}