package com.example.food;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class about extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView btMenu;
    ImageView ins, tele, fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btMenu = (ImageView) findViewById(R.id.bt_menu);
        ins = findViewById(R.id.instagram);
        tele = findViewById(R.id.telegram);
        fb = findViewById(R.id.facebook);

        btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        ins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://linktr.ee/Instagram.PKLPENS2022");
            }
        });

        tele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://linktr.ee/Telegram.PKLPENS2022");
            }
        });

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://linktr.ee/facebook.PKLPENS2022");
            }
        });
    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
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