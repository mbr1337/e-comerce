package com.sklep.shopping.activity.viewholder;


import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sklep.shopping.R;
import com.sklep.shopping.activity.Interface.ItemOnClickListener;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final TextView productName;
    private final TextView productPrice;
    private final TextView productQuantity;
    private ItemOnClickListener listener;

    public CartViewHolder(View itemView) {
        super(itemView);

        productName = itemView.findViewById(R.id.cart_product_name);
        productPrice = itemView.findViewById(R.id.cart_product_price);
        productQuantity = itemView.findViewById(R.id.cart_product_quantity);
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(), false);
    }

    public TextView getProductName()
    {
        return productName;
    }

    public TextView getProductPrice()
    {
        return productPrice;
    }

    public TextView getProductQuantity()
    {
        return productQuantity;
    }
}


