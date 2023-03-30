package com.sklep.shopping.activity;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sklep.shopping.R;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{
    private EditText inputName;
    private EditText inputPhoneNumber;
    private EditText inputPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputName = findViewById(R.id.register_username);
        inputPassword = findViewById(R.id.register_password);
        inputPhoneNumber = findViewById(R.id.register_phone);
        loadingBar = new ProgressDialog(this);
    }

    public void createAccount(View view)
    {
        String name = inputName.getText().toString();
        String phone = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Proszę wpisać swoje imię", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Proszę wpisać swój numer telefonu", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Proszę wpisać swoje hasło", Toast.LENGTH_SHORT).show();
        } else
        {
            loadingBar.setTitle("Utwórz konto");
            loadingBar.setMessage("Proszę czekać...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validatePhoneNumber(name, phone, password);
        }

    }

    private void validatePhoneNumber(final String name, final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);
                    RootRef.child("Users").child(phone).updateChildren(userdataMap).addOnCompleteListener(task ->
                    {

                        if (task.isSuccessful())
                        {
                            Toast.makeText(RegisterActivity.this, "Twoje konto zostało poprawnie utworzone.", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(RegisterActivity.this, "Błąd sieci. Proszę spróbować poźniej", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else
                {
                    Toast.makeText(RegisterActivity.this, "Numer telefonu  " + phone + " już istnieje", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Spróbuj ponownie, używając innego numeru telefonu.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
}
