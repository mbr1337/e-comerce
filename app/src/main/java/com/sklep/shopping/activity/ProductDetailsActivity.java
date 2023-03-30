package com.sklep.shopping.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sklep.shopping.R;
import com.sklep.shopping.activity.model.Products;
import com.sklep.shopping.activity.prevalent.Prevalent;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity
{
    private ImageView productImage;
    private EditText quantity;
    private TextView productPrice;
    private TextView productDescription;
    private TextView productName;
    private String productID;
    private String state = "Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID = getIntent().getStringExtra("pid");
        quantity = findViewById(R.id.quantity);
        productImage = findViewById(R.id.product_image_details);
        productName = (TextView) findViewById(R.id.product_name_details);
        productDescription = findViewById(R.id.product_description_details);
        productPrice = findViewById(R.id.product_price_details);

        quantity.setText("1");

        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable edt) {
                if (edt.length() == 1 && edt.toString().equals("0"))
                    quantity.setText("");
            }
        });

        getProductDetails(productID);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        checkOrderState();
    }

    private void addingToCartList()
    {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd. yyy");
        String saveCurrentDate = currentDate.format(calForDate.getTime());
        String saveCurrentTime = currentDate.format(calForDate.getTime());
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String, Object> cartMap = new HashMap<>();

        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", quantity.getText().toString());
        cartMap.put("discount", "");

        cartListRef.child("User view").child(Prevalent.getCurrentOnlineUser().getPhone()).child("Products")
                .child(productID).updateChildren(cartMap).addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                cartListRef.child("Admin view").child(Prevalent.getCurrentOnlineUser().getPhone())
                        .child("Products").child(productID)
                        .updateChildren(cartMap)
                        .addOnCompleteListener(task1 ->
                        {
                            if (task1.isSuccessful())
                            {
                                Toast.makeText(ProductDetailsActivity.this, "Dodano do koszyka", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ProductDetailsActivity.this, DrawerActivity.class);
                                startActivity(intent);
                            }
                        });
            }
        });
    }

    private void getProductDetails(String productID)
    {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productsRef.child(productID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    Products products = dataSnapshot.getValue(Products.class);
                    productName.setText(products.getName());
                    productPrice.setText(products.getPrice());
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void checkOrderState()
    {
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.getCurrentOnlineUser().getPhone());
        ordersRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    if (shippingState.equals("Shipped"))
                    {
                        state = "Order Shipped";
                    } else if (shippingState.equals("Not Shipped"))
                    {
                        state = "Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    public void addToCart(View view)
    {
        if (state.equals("Order Placed") || state.equals("Order Shipped"))
        {
            Toast.makeText(ProductDetailsActivity.this,
                    "Możesz dodać więcej produktów po wysłaniu lub potwierdzeniu zamówienia", Toast.LENGTH_LONG).show();
        } else
        {
            addingToCartList();
        }
    }
}
