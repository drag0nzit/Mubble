package com.example.mubble;

import com.google.firebase.auth.FirebaseAuth;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.example.mubble.database.FirestoreManager;
import com.example.mubble.models.User;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mubble.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        binding.tvLogin.setOnClickListener(v -> {
            finish();
        });

        binding.btnRegister.setOnClickListener(v -> {

            android.util.Log.d("REGISTER", "1. Кнопка нажата");

            String name = binding.etName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String repeatPassword = binding.etRepeatPassword.getText().toString().trim();

            android.util.Log.d("REGISTER", "2. Поля считаны");

            if (name.isEmpty()) {
                android.util.Log.d("REGISTER", "Имя пустое");
                binding.etName.setError("Введите имя");
                return;
            }

            if (email.isEmpty()) {
                android.util.Log.d("REGISTER", "Почта пустая");
                binding.etEmail.setError("Введите почту");
                return;
            }

            if (password.isEmpty()) {
                android.util.Log.d("REGISTER", "Пароль пустой");
                binding.etPassword.setError("Введите пароль");
                return;
            }

            if (!password.equals(repeatPassword)) {
                android.util.Log.d("REGISTER", "Пароли не совпадают");
                binding.etRepeatPassword.setError("Пароли не совпадают");
                return;
            }

            android.util.Log.d("REGISTER", "3. Проверки пройдены");

            auth.createUserWithEmailAndPassword(email, password)

                    .addOnCompleteListener(this, task -> {

                        android.util.Log.d("REGISTER", "4. onComplete вызван");

                        if (task.isSuccessful()) {

                            android.util.Log.d("REGISTER", "5. Регистрация успешна");

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            if (firebaseUser != null) {

                                android.util.Log.d("REGISTER", "6. Пользователь получен");

                                User user = new User(
                                        firebaseUser.getUid(),
                                        email,
                                        name,
                                        ""
                                );

                                FirestoreManager firestoreManager = new FirestoreManager();

                                android.util.Log.d("REGISTER", "7. Записываем пользователя в Firestore");

                                firestoreManager.createUser(user);

                                android.util.Log.d("REGISTER", "8. Пользователь записан");
                            }

                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Аккаунт создан!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            android.util.Log.d("REGISTER", "9. Переход в MainActivity");

                            startActivity(
                                    new Intent(RegisterActivity.this, MainActivity.class)
                            );

                            finishAffinity();

                        } else {

                            android.util.Log.e(
                                    "REGISTER",
                                    "Ошибка регистрации",
                                    task.getException()
                            );

                            Toast.makeText(
                                    RegisterActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    })

                    .addOnFailureListener(e -> {

                        android.util.Log.e(
                                "REGISTER",
                                "addOnFailureListener",
                                e
                        );

                    });

            android.util.Log.d("REGISTER", "10. Запрос отправлен");

        });

    }
}