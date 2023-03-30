package com.sklep.shopping.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sklep.shopping.R;

public class AdminCategoryActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);
    }


    public void checkOrders(View view)
    {
        Intent intent = new Intent(AdminCategoryActivity.this, AdminNewOrdersActivity.class);
        startActivity(intent);
    }

    public void logout(View view)
    {
        Intent intent = new Intent(AdminCategoryActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void addNewProduct(View view) {
        Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
        startActivity(intent);
    }
}
