package com.example.campuscrave;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;




public class ReportActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private List<String> xValues = new ArrayList<>();
    private List<BarEntry> entries = new ArrayList<>();
    private BarChart barchart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        FirebaseApp.initializeApp(this);

        // Initialize chart
        barchart = findViewById(R.id.chart);

        // Reference to your Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Tomorrow's Food Items");

        // Fetch data from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                entries.clear();
                xValues.clear();

                // Loop through the dataSnapshot to get each item's vote and itemname
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String itemname = itemSnapshot.child("dataName").getValue(String.class);
                    Long voteLong = itemSnapshot.child("vote").getValue(Long.class);
                    if (voteLong != null) {
                        long vote = voteLong;

                        // Add data to the lists
                        xValues.add(itemname);
                        entries.add(new BarEntry(xValues.size() - 1, (float) vote));
                    }
                }

                // Call the method to set up the chart after fetching the data
                setupChart();

                // Generate PDF and save it
                generateAndSavePDF();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors here
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Redraw the chart when the screen orientation changes
        setupChart();
    }

    private void setupChart() {
        barchart.getAxisRight().setDrawLabels(false);

        YAxis yAxis = barchart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setZeroLineWidth(2f);
        yAxis.setAxisLineColor(android.R.color.black);
        yAxis.setLabelCount(10);

        BarDataSet dataSet = new BarDataSet(entries, "Food Items");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData barData = new BarData(dataSet);
        barchart.setData(barData);

        barchart.getDescription().setEnabled(false);

        barchart.invalidate();

        barchart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
        barchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        barchart.getXAxis().setGranularity(1f);

        barchart.getXAxis().setGranularityEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            // Permission already granted, proceed with generating and saving PDF
            generateAndSavePDF();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with generating and saving PDF
                generateAndSavePDF();
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
                Toast.makeText(this, "Permission Denied: Cannot save PDF to external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void generateAndSavePDF() {
        try {
            // Create a new PDF file in the public external storage directory
            File publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File pdfFile = new File(publicDir, "report.pdf");

            // Save the chart image
            File chartImageFile = saveChartImage(publicDir.getAbsolutePath());

            // Create a PDF document
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            PdfWriter pdfWriter = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);

            // Add a title to the PDF
            document.add(new Paragraph("Generated Report"));

            // Add the chart image to the PDF
            Image chartImage = new Image(ImageDataFactory.create(chartImageFile.getAbsolutePath()));
            document.add(chartImage);

            // Close the document
            document.close();

            // Open an Intent to share the generated PDF
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri contentUri = Uri.fromFile(pdfFile);
            intent.setDataAndType(contentUri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Verify that the intent will resolve to an activity
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Handle the case where no PDF viewer app is available
                Toast.makeText(this, "No PDF viewer app found", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File saveChartImage(String directory) {
        File file = new File(directory, "chart.png");

        // Save the chart as an image
        try {
            FileOutputStream stream = new FileOutputStream(file);
            barchart.getChartBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}

//public class ReportActivity extends AppCompatActivity {
//    private List<String> xValues = new ArrayList<>();
//    private List<BarEntry> entries = new ArrayList<>();
//
//    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_report);
//        FirebaseApp.initializeApp(this);
//
//        // Reference to your Firebase Realtime Database
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Tomorrow's Food Items");
//
//        // Fetch data from the database
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                entries.clear();
//                xValues.clear();
//
//                // Loop through the dataSnapshot to get each item's vote and itemname
//                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
//                    String itemname = itemSnapshot.child("dataName").getValue(String.class);
//                    Long voteLong = itemSnapshot.child("vote").getValue(Long.class);
//                    if (voteLong != null) {
//                        long vote = voteLong;
//
//                        // Add data to the lists
//                        xValues.add(itemname);
//                        entries.add(new BarEntry(xValues.size() - 1, (float) vote));
//                    }
//                }
//
//                // Call the method to set up the chart after fetching the data
//                setupChart();
//
//                // Generate PDF and save it
//                generateAndSavePDF();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Handle errors here
//            }
//        });
//    }
//
//    private void setupChart() {
//        BarChart barchart = findViewById(R.id.chart);
//        barchart.getAxisRight().setDrawLabels(false);
//
//        YAxis yAxis = barchart.getAxisLeft();
//        yAxis.setAxisMinimum(0f);
//        yAxis.setAxisMaximum(100f);
//        yAxis.setZeroLineWidth(2f);
//        yAxis.setAxisLineColor(android.R.color.black);
//        yAxis.setLabelCount(10);
//
//        BarDataSet dataSet = new BarDataSet(entries, "Food Items");
//        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//        BarData barData = new BarData(dataSet);
//        barchart.setData(barData);
//
//        barchart.getDescription().setEnabled(false);
//
//        barchart.invalidate();
//
//        barchart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
//        barchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//
//        barchart.getXAxis().setGranularity(1f);
//
//        barchart.getXAxis().setGranularityEnabled(true);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void checkAndRequestPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//        } else {
//            // Permission already granted, proceed with generating and saving PDF
//            generateAndSavePDF();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, proceed with generating and saving PDF
//                generateAndSavePDF();
//            } else {
//                // Permission denied, handle accordingly (e.g., show a message)
//                Toast.makeText(this, "Permission Denied: Cannot save PDF to external storage", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void generateAndSavePDF() {
//        try {
//            // Create a new PDF file in the public external storage directory
//            File publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//            File pdfFile = new File(publicDir, "report.pdf");
//
//            // Save the chart image
//            BarChart barchart = findViewById(R.id.chart);
//            File chartImageFile = saveChartImage(barchart, publicDir.getAbsolutePath());
//
//            // Create a PDF document
//            FileOutputStream outputStream = new FileOutputStream(pdfFile);
//            PdfWriter pdfWriter = new PdfWriter(outputStream);
//            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
//            Document document = new Document(pdfDocument);
//
//            // Add a title to the PDF
//            document.add(new Paragraph("Generated Report"));
//
//            // Add the chart image to the PDF
//            Image chartImage = new Image(ImageDataFactory.create(chartImageFile.getAbsolutePath()));
//            document.add(chartImage);
//
//            // Close the document
//            document.close();
//
//            // Open an Intent to share the generated PDF
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            Uri contentUri = Uri.fromFile(pdfFile);
//            intent.setDataAndType(contentUri, "application/pdf");
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//            // Verify that the intent will resolve to an activity
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                startActivity(intent);
//            } else {
//                // Handle the case where no PDF viewer app is available
//                Toast.makeText(this, "No PDF viewer app found", Toast.LENGTH_SHORT).show();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private File saveChartImage(BarChart chart, String directory) {
//        File file = new File(directory, "chart.png");
//
//        // Save the chart as an image
//        try {
//            FileOutputStream stream = new FileOutputStream(file);
//            chart.getChartBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
//            stream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return file;
//    }
//}
