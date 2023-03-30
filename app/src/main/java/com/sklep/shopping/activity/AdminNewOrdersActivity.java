package com.sklep.shopping.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sklep.shopping.R;
import com.sklep.shopping.activity.model.AdminOrders;

public class AdminNewOrdersActivity extends AppCompatActivity
{
    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersList = findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRef, AdminOrders.class)
                        .build();
        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model)
                    {

                        holder.userName.setText("Imie: " + model.getName());
                        holder.userPhoneNumber.setText("Telefon: " + model.getPhone());
                        holder.userTotalPrice.setText("Całkowita cena = " + model.getTotalAmount());
                        holder.userDateTime.setText("Zamówiony w: " + model.getDate() + " " + model.getTime());
                        holder.userShippingAddress.setText("Adres: " + model.getAddress() + ", " + model.getCity());
                        holder.showOrdersButton.setOnClickListener(view ->
                        {
                            String uID = getRef(position).getKey();
                            Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserProductsActivity.class);
                            intent.putExtra("uid", uID);
                            startActivity(intent);
                        });

                        holder.itemView.setOnClickListener(view ->
                        {
                            CharSequence[] options1 = new CharSequence[]{"Yes", "No"};

                            AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                            builder.setTitle("Czy wysłałeś produkty z tego zamówienia?");
                            builder.setItems(options1, (dialogInterface, i) ->
                            {
                                if (i == 0)
                                {
                                    String uID = getRef(position).getKey();
                                    RemoverOrder(uID);

                                } else
                                {
                                    finish();
                                }

                            });
                            builder.show();
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                        return new AdminOrdersViewHolder(view);
                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();

    }


    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView userName;
        private final TextView userPhoneNumber;
        private final TextView userTotalPrice;
        private final TextView userDateTime;
        private final TextView userShippingAddress;
        private final Button showOrdersButton;

        public AdminOrdersViewHolder(View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userShippingAddress = itemView.findViewById(R.id.order_address_city);
            showOrdersButton = itemView.findViewById(R.id.show_order_button);
        }
    }

    private void RemoverOrder(String uID)
    {
        ordersRef.child(uID).removeValue();
    }
}
