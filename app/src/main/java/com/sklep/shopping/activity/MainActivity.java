package com.sklep.shopping.activity;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sklep.shopping.R;
import com.sklep.shopping.activity.model.Users;
import com.sklep.shopping.activity.prevalent.Prevalent;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity
{
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingBar = new ProgressDialog(this);
        Paper.init(this);

        String usernameKey = Paper.book().read(Prevalent.getUsernameKey());
        String userPasswordKey = Paper.book().read(Prevalent.getUserPasswordKey());
        if (usernameKey != null && !usernameKey.equals("") && userPasswordKey != null && !userPasswordKey.equals(""))
        {
            if (!TextUtils.isEmpty(usernameKey) && !TextUtils.isEmpty(userPasswordKey))
            {
                allowAccess(usernameKey, userPasswordKey);

                loadingBar.setTitle("Zalogowano");
                loadingBar.setMessage("Proszę czekać...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }

    private void allowAccess(final String login, final String password)
    {
        final DatabaseReference rootReference;
        rootReference = FirebaseDatabase.getInstance().getReference();
        rootReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("Users").child(login).exists())
                {

                    Users usersData = dataSnapshot.child("Users").child(login).getValue(Users.class);
                    if (usersData.getName().equals(login))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this, "Już jesteś zalogowany", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(MainActivity.this, DrawerActivity.class);
                            Prevalent.setCurrentOnlineUser(usersData);
                            startActivity(intent);

                        } else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Niepoprawne hasło ", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else
                {
                    Toast.makeText(MainActivity.this, "Użytkownik " + login + " nie istnieje", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    public void login(View view)
    {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void register(View view)
    {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
