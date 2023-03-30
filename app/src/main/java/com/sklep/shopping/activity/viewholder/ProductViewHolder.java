package com.sklep.shopping.activity.viewholder;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sklep.shopping.R;
import com.sklep.shopping.activity.Interface.ItemOnClickListener;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final TextView txtProductName;
    private final TextView txtProductDescription;
    private final TextView txtProductPrice;
    private final ImageView imageView;
    private ItemOnClickListener listener;

    public ProductViewHolder(View itemView)
    {
        super(itemView);


        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.products_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.products_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.products_price);
    }

    @Override
    public void onClick(View view)
    {
        listener.onClick(view, getAdapterPosition(), false);
    }

    public TextView getTxtProductName()
    {
        return txtProductName;
    }

    public TextView getTxtProductDescription()
    {
        return txtProductDescription;
    }

    public TextView getTxtProductPrice()
    {
        return txtProductPrice;
    }

    public ImageView getImageView()
    {
        return imageView;
    }
}

