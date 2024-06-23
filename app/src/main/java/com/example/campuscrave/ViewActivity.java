package com.example.campuscrave;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.content.Intent;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;

public class ViewActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    RecyclerView recyclerView;
    List<DataClass> dataList;
    MyAdapter adapter;
    SearchView searchView;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.search);
        radioGroup = findViewById(R.id.radioGroup);

        // Initialize RecyclerView, AlertDialog, and Firebase
        initializeComponents();

        // Set up the search and filter functionality
        setupSearchAndFilter();
    }

    private void initializeComponents() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ViewActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();
        adapter = new MyAdapter(ViewActivity.this, dataList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Current Food Items");
        dialog.show();
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    dataClass.setKey(itemSnapshot.getKey());
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        // Set an OnClickListener for each item in the RecyclerView
        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Start DetailActivity when an item is clicked
                DataClass clickedItem = dataList.get(position);
                Intent intent = new Intent(ViewActivity.this, DetailActivity.class);
                intent.putExtra("key", clickedItem.getKey());
                startActivity(intent);
            }
        });
    }

    private void setupSearchAndFilter() {
        // Set up the search and filter functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // When using searchList, pass the selected filter type as an argument
                searchList(newText, getSelectedFilter());
                return true;
            }
        });

        // Add a listener to the SearchView's close button to reset the filter
        searchView.setOnCloseListener(() -> {
            // Clear the search query and reset the filter
            searchList("", getSelectedFilter());
            return false;
        });

        // Add a listener to the RadioGroup to filter the RecyclerView based on the selected option
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // When using searchList, pass the selected filter type as an argument
            searchList(searchView.getQuery().toString(), getSelectedFilter());
        });
    }

    private String getSelectedFilter() {
        RadioButton selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        if (selectedRadioButton != null) {
            return selectedRadioButton.getText().toString();
        }
        return "";
    }

    public void searchList(String text, String foodTypeFilter) {
        ArrayList<DataClass> searchList = new ArrayList<>();
        for (DataClass dataClass : dataList) {
            // Check if the data matches the search query and the food type filter
            if ((foodTypeFilter.isEmpty() || foodTypeFilter.equalsIgnoreCase("All")) &&
                    (dataClass.getDataName().toLowerCase().contains(text.toLowerCase()))) {
                // Show all items regardless of foodType when "All" is selected
                searchList.add(dataClass);
            } else if (dataClass.getFoodType().equalsIgnoreCase(foodTypeFilter) &&
                    dataClass.getDataName().toLowerCase().contains(text.toLowerCase())) {
                // Show items based on both search query and foodType filter
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }
}
