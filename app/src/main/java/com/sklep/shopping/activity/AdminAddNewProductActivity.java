package com.sklep.shopping.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sklep.shopping.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity
{
    private String categoryName;
    private String description;
    private String price;
    private String pName;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String productRandomKey;
    private String downloadImageUrl;
    private ImageView inputProductImage;
    private EditText inputProductName;
    private EditText inputProductDescription;
    private EditText inputProductPrice;
    private EditText inputCategory;
    private Uri imageUri;
    private StorageReference productImagesRef;
    private DatabaseReference productsRef;
    private ProgressDialog loadingBar;

    private static final int galleryPick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

//        categoryName = getIntent().getExtras().get("category").toString();
        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        inputProductImage = findViewById(R.id.added_product_img);
        inputProductName = findViewById(R.id.products_name);
        inputProductDescription = findViewById(R.id.products_description);
        inputProductPrice = findViewById(R.id.products_price);
        inputCategory = findViewById(R.id.products_category);
        loadingBar = new ProgressDialog(this);
    }


    public void openGallery(View view)
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galleryPick);
    }

    // akcja po wybraniu zdjecia z galerii
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            inputProductImage.setImageURI(imageUri);
        }
    }

    public void addProduct(View view)
    {
        categoryName = inputCategory.getText().toString();
        description = inputProductDescription.getText().toString();
        price = inputProductPrice.getText().toString();
        pName = inputProductName.getText().toString();

        if (imageUri == null)
        {
            Toast.makeText(this, "Zdjęcie produktu jest obowiązkowe", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description))
        {
            Toast.makeText(this, "Proszę wpisać opis produktu", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(price))
        {
            Toast.makeText(this, "Proszę wpisać cenę produktu...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pName))
        {
            Toast.makeText(this, "Proszę wpisać nazwe produktu...", Toast.LENGTH_SHORT).show();
        } else
        {
            storeProductInformation();
        }
    }

    private void storeProductInformation()
    {
        loadingBar.setTitle("Dodaj nowy produkt");
        loadingBar.setMessage("Proszę czekać...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = productImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(e ->
        {
            String message = e.toString();
            Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }).addOnSuccessListener(taskSnapshot ->
        {
            Toast.makeText(AdminAddNewProductActivity.this, "Obraz produktu przesłany pomyślnie", Toast.LENGTH_SHORT).show();
            uploadTask.continueWithTask(task ->
            {
                if (!task.isSuccessful())
                {
                    throw task.getException();
                }

                downloadImageUrl = filePath.getDownloadUrl().toString();
                return filePath.getDownloadUrl();
            }).addOnCompleteListener(task ->
            {
                if (task.isSuccessful())
                {
                    downloadImageUrl = task.getResult().toString();

                    Toast.makeText(AdminAddNewProductActivity.this, "Pomyślnie uzyskano adres URL obrazu produktu...", Toast.LENGTH_SHORT).show();

                    saveProductInfoToDatabase();
                }
            });
        });

    }

    private void saveProductInfoToDatabase()
    {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", description);
        productMap.put("image", downloadImageUrl);
//        productMap.put("category", categoryName);
        productMap.put("price", price);
        productMap.put("pname", pName);

        productsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(task ->
                {
                    if (task.isSuccessful())
                    {
                        Intent intent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                        startActivity(intent);

                        loadingBar.dismiss();
                        Toast.makeText(AdminAddNewProductActivity.this, "Produkt został dodany pomyślnie", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        loadingBar.dismiss();
                        Toast.makeText(AdminAddNewProductActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
