package com.sklep.shopping.activity;

import android.content.Intent;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.sklep.shopping.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sklep.shopping.activity.model.Cart;
import com.sklep.shopping.activity.prevalent.Prevalent;
import com.sklep.shopping.activity.viewholder.CartViewHolder;

public class CartActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private Button nextButton;
    private TextView txtTotalAmount;
    private TextView prompt;
    private int overTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        nextButton = findViewById(R.id.next_button);
        txtTotalAmount = findViewById(R.id.total_price);
        prompt = findViewById(R.id.prompt);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        CheckOrderState();
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child("User view")
                                .child(Prevalent.getCurrentOnlineUser().getPhone()).child("Products"), Cart.class).build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model)
            {
                holder.getProductQuantity().setText("Ilość = " + model.getQuantity());
                holder.getProductPrice().setText("Cena = " + model.getPrice() + " zł");
                holder.getProductName().setText(model.getName());
                int oneTyprProductTPrice = ((Integer.parseInt(model.getPrice()))) * Integer.parseInt(model.getQuantity());
                overTotalPrice = overTotalPrice + oneTyprProductTPrice;

                holder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Edycja",
                                        "Usuń"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Opcje koszyka: ");
                        builder.setItems(options, (dialogInterface, i) ->
                        {
                            if (i == 0)
                            {
                                Intent intent = new Intent(CartActivity.this,
                                        ProductDetailsActivity.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent);
                            }
                            if (i == 1)
                            {
                                cartListRef.child("User view")
                                        .child(Prevalent.getCurrentOnlineUser().getPhone())
                                        .child("Products")
                                        .child(model.getPid())
                                        .removeValue()
                                        .addOnCompleteListener(task ->
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(CartActivity.this,
                                                        "Pomyślnie usunięto element.",
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(CartActivity.this,
                                                        DrawerActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                return new CartViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void CheckOrderState()
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
                    String userName = dataSnapshot.child("name").getValue().toString();
                    if (shippingState.equals("Shipped"))
                    {
                        txtTotalAmount.setText("Drogi użytkowniku, zamówienie zostało pomyślnie wysłane.");
                        recyclerView.setVisibility(View.GONE);
                        prompt.setVisibility(View.VISIBLE);
                        prompt.setText("Twoje zamówienie zostało pomyślnie wysłane. Wkrótce otrzymasz zamówienie.");
                        nextButton.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "Możesz kupić więcej produktów, po otrzymaniu pierwszego zamówienia", Toast.LENGTH_SHORT).show();
                    } else if (shippingState.equals("Nie wysłano"))
                    {
                        txtTotalAmount.setText("Stan wysyłki: Nie wysłano");
                        recyclerView.setVisibility(View.GONE);
                        prompt.setVisibility(View.VISIBLE);

                        nextButton.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "Możesz kupić więcej produktów, po otrzymaniu pierwszego zamówienia", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    public void next(View view)
    {
        txtTotalAmount.setText("Całkowita cena = zł" + overTotalPrice);
        Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
        intent.putExtra("Total Price", String.valueOf(overTotalPrice));
        startActivity(intent);
        finish();
    }
}
