package com.example.campuscrave;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailsActivity2 extends AppCompatActivity {

    TextView detailDesc, detailName, detailType, detailAlgn;
    ImageView detailImage;
    Button voteButton, unvoteButton;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);

        detailDesc = findViewById(R.id.detailDesc);
        detailImage = findViewById(R.id.detailImage);
        detailName = findViewById(R.id.detailName);
        detailType = findViewById(R.id.detailType);
        detailAlgn = findViewById(R.id.detailAlgn);
        voteButton = findViewById(R.id.vote);
        unvoteButton = findViewById(R.id.unvote);

        // Retrieve the key from the intent
        String itemKey = getIntent().getStringExtra("key");

        if (itemKey != null && !itemKey.isEmpty()) {
            // Initialize Firebase Realtime Database reference
            DatabaseReference itemsReference = FirebaseDatabase.getInstance().getReference().child("Tomorrow's Food Items");

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

        // Set OnClickListener for the vote button
        voteButton.setOnClickListener(v -> vote());

        // Set OnClickListener for the unvote button
        unvoteButton.setOnClickListener(v -> unvote());
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
                    String type = dataSnapshot.child("foodType").getValue(String.class);
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

    private void vote() {
        // Increment the vote count in the database
        databaseReference.child("vote").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long currentVotes = (long) dataSnapshot.getValue();
                    databaseReference.child("vote").setValue(currentVotes + 1);
                    Log.d("DetailsActivity2", "Vote incremented successfully");
                    showToast("Voted for food item successfully");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors here
                Log.e("DetailsActivity2", "DatabaseError: " + error.getMessage());
            }
        });
    }

    private void unvote() {
        // Decrement the vote count in the database
        databaseReference.child("vote").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long currentVotes = (long) dataSnapshot.getValue();
                    if (currentVotes > 0) {
                        databaseReference.child("vote").setValue(currentVotes - 1);
                        Log.d("DetailsActivity2", "Vote decremented successfully");
                        showToast("Unvoted for food item successfully");
                    } else {
                        Log.d("DetailsActivity2", "Vote count is already zero");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors here
                Log.e("DetailsActivity2", "DatabaseError: " + error.getMessage());
            }
        });
    }
        // Method to show a Toast message
        private void showToast(String message) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }





//package com.example.campuscrave;
//
//import androidx.appcompat.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.TextView;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Picasso;
//public class DetailsActivity2 extends AppCompatActivity{
//
//    TextView detailDesc, detailName, detailType;
//    ImageView detailImage;
//
//    DatabaseReference databaseReference;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail);
//
//        detailDesc = findViewById(R.id.detailDesc);
//        detailImage = findViewById(R.id.detailImage);
//        detailName = findViewById(R.id.detailName);
//        detailType = findViewById(R.id.detailType);
//        // Retrieve the key from the intent
//        String itemKey = getIntent().getStringExtra("key");
//
//        if (itemKey != null && !itemKey.isEmpty()) {
//            // Initialize Firebase Realtime Database reference
//            DatabaseReference itemsReference = FirebaseDatabase.getInstance().getReference().child("Tomorrow's Food Items");
//
//            // Check if the itemKey exists in the database
//            itemsReference.child(itemKey).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        // If the itemKey exists, retrieve data from the Realtime Database
//                        databaseReference = itemsReference.child(itemKey);
//                        retrieveDataFromDatabase();
//                    } else {
//                        // Handle the case where the itemKey does not exist
//                        Log.e("DetailActivity", "itemKey does not exist in the database");
//                        finish(); // Close the activity if itemKey does not exist
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    // Handle errors here
//                    Log.e("DetailActivity", "DatabaseError: " + error.getMessage());
//                }
//            });
//        } else {
//            // Handle the case where itemKey is null or empty
//            Log.e("DetailActivity", "Invalid itemKey");
//            finish(); // Close the activity if itemKey is not valid
//        }
//    }
//
//    private void retrieveDataFromDatabase() {
//        // Retrieve data from the Realtime Database
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                try {
//                    // Assuming your DataClass has appropriate getters
//                    String desc = dataSnapshot.child("dataDesc").getValue(String.class);
//                    String imageUrl = dataSnapshot.child("dataImage").getValue(String.class);
//                    String name = dataSnapshot.child("dataName").getValue(String.class);
//                    String type= dataSnapshot.child("foodType").getValue(String.class);
//                    // Set the retrieved data to the TextViews and ImageView
//                    if (desc != null) {
//                        detailDesc.setText(desc);
//                    }
//
//                    if (name != null) {
//                        detailName.setText(name);
//                    }
//
//                    // Use Picasso or any other image loading library to load the image
//                    if (imageUrl != null) {
//                        Picasso.get().load(imageUrl).into(detailImage);
//                    }
//
//                    if (type != null) {
//                        detailType.setText(type);
//                    }
//                } catch (Exception e) {
//                    // Handle exceptions
//                    e.printStackTrace();
//                    Log.e("DetailActivity", "Exception: " + e.getMessage());
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Handle errors here
//                Log.e("DetailActivity", "DatabaseError: " + error.getMessage());
//            }
//        });
//    }
//
//}
