package com.example.campuscrave;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    TextView detailDesc, detailName, detailType, detailAlgn;
    ImageView detailImage;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailDesc = findViewById(R.id.detailDesc);
        detailImage = findViewById(R.id.detailImage);
        detailName = findViewById(R.id.detailName);
        detailType = findViewById(R.id.detailType);
        detailAlgn = findViewById(R.id.detailAlgn);
        // Retrieve the key from the intent
        String itemKey = getIntent().getStringExtra("key");

        if (itemKey != null && !itemKey.isEmpty()) {
            // Initialize Firebase Realtime Database reference
            DatabaseReference itemsReference = FirebaseDatabase.getInstance().getReference().child("Current Food Items");

            // Check if the itemKey exists in the database
            itemsReference.child(itemKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // If the itemKey exists, retrieve data from the Realtime Database
                        databaseReference = itemsReference.child(itemKey);
                        retrieveDataFromDatabase();
                    } else {
                        // Handle the case where the itemKey does not exist
                        Log.e("DetailActivity", "itemKey does not exist in the database");
                        finish(); // Close the activity if itemKey does not exist
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Handle errors here
                    Log.e("DetailActivity", "DatabaseError: " + error.getMessage());
                }
            });
        } else {
            // Handle the case where itemKey is null or empty
            Log.e("DetailActivity", "Invalid itemKey");
            finish(); // Close the activity if itemKey is not valid
        }
    }

    private void retrieveDataFromDatabase() {
        // Retrieve data from the Realtime Database
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    // Assuming your DataClass has appropriate getters
                    String desc = dataSnapshot.child("dataDesc").getValue(String.class);
                    String imageUrl = dataSnapshot.child("dataImage").getValue(String.class);
                    String name = dataSnapshot.child("dataName").getValue(String.class);
                    String type= dataSnapshot.child("foodType").getValue(String.class);
                    String Algn= dataSnapshot.child("dataAlgn").getValue(String.class);
                    // Set the retrieved data to the TextViews and ImageView
                    if (desc != null) {
                        detailDesc.setText(desc);
                    }

                    if (name != null) {
                        detailName.setText(name);
                    }

                    // Use Picasso or any other image loading library to load the image
                    if (imageUrl != null) {
                        Picasso.get().load(imageUrl).into(detailImage);
                    }

                    if (type != null) {
                        detailType.setText(type);
                    }

                    if (Algn != null) {
                        detailAlgn.setText(Algn);
                    }
                } catch (Exception e) {
                    // Handle exceptions
                    e.printStackTrace();
                    Log.e("DetailActivity", "Exception: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors here
                Log.e("DetailActivity", "DatabaseError: " + error.getMessage());
            }
        });
    }
}
