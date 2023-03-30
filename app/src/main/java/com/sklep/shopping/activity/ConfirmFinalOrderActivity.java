package com.sklep.shopping.activity;

import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sklep.shopping.R;
import com.sklep.shopping.activity.prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ConfirmFinalOrderActivity extends AppCompatActivity
{
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText addressEditText;
    private EditText cityEditText;
    private String totalAmount;
    private Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");

        nameEditText = findViewById(R.id.shippment_name);
        phoneEditText = findViewById(R.id.shippment_phone_number);
        addressEditText = findViewById(R.id.shippment_address);
        cityEditText = findViewById(R.id.shippment_city);
        confirmBtn = findViewById(R.id.confirmBtn);

        Toast.makeText(this, "Całkowita cena = " + totalAmount + " zł.", Toast.LENGTH_SHORT).show();
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check();
            }
        });
    }

    public void check()
    {
        if (TextUtils.isEmpty(nameEditText.getText().toString()))
        {
            Toast.makeText(this, "Podaj swoje imię i nazwisko", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Podaj swój numer telefonu", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Podaj swój prawidłowy adres.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cityEditText.getText().toString()))
        {
            Toast.makeText(this, "Podaj nazwę swojego miasta", Toast.LENGTH_SHORT).show();
        } else
        {
            confirmOrder();
        }
    }

    private void confirmOrder()
    {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd. yyy");
        String saveCurrentDate = currentDate.format(calForDate.getTime());
        String saveCurrentTime = currentDate.format(calForDate.getTime());
        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.getCurrentOnlineUser().getPhone());

        Map<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("name", nameEditText.getText().toString());
        ordersMap.put("phone", phoneEditText.getText().toString());
        ordersMap.put("address", addressEditText.getText().toString());
        ordersMap.put("city", cityEditText.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("state", "Not Shipped");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                FirebaseDatabase.getInstance().getReference()
                        .child("Cart List")
                        .child("User view")
                        .child(Prevalent.getCurrentOnlineUser().getPhone())
                        .removeValue()
                        .addOnCompleteListener(task1 ->
                        {
                            if (task1.isSuccessful())
                            {
                                Toast.makeText(ConfirmFinalOrderActivity.this,
                                        "Twoje zamówienie zostało pomyślnie złożone.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ConfirmFinalOrderActivity.this, DrawerActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });


    }
}

