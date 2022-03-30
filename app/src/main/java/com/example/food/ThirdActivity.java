package com.example.food;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ThirdActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<DataModel> mList;
    private ItemAdapter adapter;
    DrawerLayout drawerLayout;
    ImageView btMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btMenu = (ImageView) findViewById(R.id.bt_menu);


        btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        recyclerView = findViewById(R.id.main_recyclervie);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mList = new ArrayList<>();

        //list1
        List<String> nestedList1 = new ArrayList<>();
        nestedList1.add("Setting -> Developer Options -> Turn On Force GPU Rendering");

        List<String> nestedList2 = new ArrayList<>();
        nestedList2.add("File Manager -> SSL");

        List<String> nestedList3 = new ArrayList<>();
        nestedList3.add("File Manager -> Excel SPJU");

        List<String> nestedList4 = new ArrayList<>();
        nestedList4.add("Press Up Volume Button to Turn On the Flashlight");
        nestedList4.add("Press Down Volume Button to Turn Off the Flashlight");

        List<String> nestedList5 = new ArrayList<>();
        nestedList5.add("Remember Edit Text");
        nestedList5.add("Barcode & QRCode Scanner");
        nestedList5.add("Capture Image with Date, Current Time, Device");
        nestedList5.add("Set Image from Gallery with Date, Current Time, Device");
        nestedList5.add("Zoom In Image");
        nestedList5.add("Automatic GPS");
        nestedList5.add("Manual GPS");
        nestedList5.add("Flashlight");
        nestedList5.add("Update Data from List");
        nestedList5.add("Delete Data from List");
        nestedList5.add("Save Image to Device when Add Data");
        nestedList5.add("Search Data on List Data");
        nestedList5.add("Floating Button with 2 options to Open Folder Excel Results & Image Results");
        nestedList5.add("Export Data to Excel");
        nestedList5.add("About Us");
        nestedList5.add("Rate Us");
        nestedList5.add("Logout");
        nestedList5.add("Exit");

        List<String> nestedList6 = new ArrayList<>();
        nestedList6.add("Don't Choose Image from Gallery with size more than 800 KB!");
        nestedList6.add("Use Manual GPS first before Capture or Set Image!");

        mList.add(new DataModel(nestedList1 , "Application Feels Lagging"));
        mList.add(new DataModel( nestedList2,"Where's Capture Image Result ?"));
        mList.add(new DataModel( nestedList3,"Where's Excel Result ?"));
        mList.add(new DataModel( nestedList4,"How to use Flashlight when scanning?"));
        mList.add(new DataModel( nestedList5,"Application Features"));
        mList.add(new DataModel(nestedList6,"Warning !"));

        adapter = new ItemAdapter(mList);
        recyclerView.setAdapter(adapter);
    }

    public void ClickLogo(View view){
        MainActivity.closeDrawer(drawerLayout);
    }

    public void ClickHome(View view){
        MainActivity.redirectActivity(this, utama.class);
    }

    public void ClickAdd(View view){
        MainActivity.redirectActivity(this, MainActivity.class);
    }

    public void ClickListData(View view){
        MainActivity.redirectActivity(this, FoodList.class);
    }

    public void ClickLogout(View view){
        MainActivity.logout(this);
    }

    public void ClickExit(View view){
        MainActivity.exit(this);
    }

    public void ClickAboutUs(View view){
        MainActivity.redirectActivity(this, about.class);
    }

    public void ClickFAQ(View view){
        MainActivity.redirectActivity(this, ThirdActivity.class);
    }

    public void ClickCriticism(View view){
        MainActivity.redirectActivity(this, Email.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);
    }

    @Override
    public void onBackPressed(){

    }
}