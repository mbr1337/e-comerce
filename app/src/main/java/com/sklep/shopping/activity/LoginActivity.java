package com.sklep.shopping.activity;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.sklep.shopping.activity.model.Users;
import com.sklep.shopping.activity.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;
import com.sklep.shopping.R;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity
{
    private EditText inputUsername;
    private EditText inputPassword;
    private Button loginButton;
    private ProgressDialog loadingBar;
    private SwitchCompat loginAsAdminSwitch;
    private String parentDbName = "Users";
    private CheckBox checkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_button2);
        inputPassword = findViewById(R.id.login_password_input);
        inputUsername = findViewById(R.id.login_username_input);
        loadingBar = new ProgressDialog(this);
        checkBoxRememberMe = findViewById(R.id.remember_me);
        loginAsAdminSwitch = findViewById(R.id.login_as_admin);

        loginAsAdminSwitch.setOnCheckedChangeListener((compoundButton, isChecked ) ->
        {
            if (isChecked)
            {
                loginButton.setText(getResources().getString(R.string.login_as_admin));
                loginAsAdminSwitch.setText(getResources().getString(R.string.login_as_user));
                parentDbName = "Admins";
            }
            else
            {
                loginButton.setText(getResources().getString(R.string.login));
                loginAsAdminSwitch.setText(getResources().getString(R.string.admin));
                parentDbName = "Users";
            }
        });

        Paper.init(this);
    }

    public void loginUser(View view)
    {
        String login = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

        if (login.isEmpty())
        {
            Toast.makeText(this, "Proszę podać swój login", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty())
        {
            Toast.makeText(this, "Wpisz hasło", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Logowanie");
            loadingBar.setMessage("Proszę czekać...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            allowAccessToAccount(login, password);
        }
    }

    private void allowAccessToAccount(final String login, final String password)
    {

        if (checkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.getUsernameKey(), login);
            Paper.book().write(Prevalent.getUserPasswordKey(), password);
        }
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference(parentDbName);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Users usersData = null;
                for (DataSnapshot i : dataSnapshot.getChildren())
                {
                    usersData = i.getValue(Users.class);
                    if (usersData.getName().equals(login))
                    {
                        break;
                    }
                }

                if (usersData != null)
                {
                    if (usersData.getName().equals(login))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            if (parentDbName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this, "Zalogowano pomyślnie jako administrator", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this, "Zalogowano pomyślnie", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);
                                Prevalent.setCurrentOnlineUser(usersData);
                                startActivity(intent);
                            }

                        } else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Niepoprawne hasło", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Użytkownik  " + login + "nie istnieje.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
}
