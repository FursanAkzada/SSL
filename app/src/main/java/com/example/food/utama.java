package com.example.food;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import com.muddzdev.styleabletoast.StyleableToast;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class utama extends AppCompatActivity {
    boolean isPressed = false;
    public static boolean isPressed2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utama);
    }
    public void ClickNew(View view){
            isPressed2 = true;
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
    }
    public void ClickList(View view){
        if (isPressed2){
            Intent intent = new Intent(this, FoodList.class);
            startActivity(intent);
        }else {
            StyleableToast.makeText(getApplicationContext(), "Choose New Data at the 1st time to access it!", Toast.LENGTH_LONG, R.style.warning).show();
        }
    }
    public void ClickFaQ(View view){
        Intent intent = new Intent(this, ThirdActivity.class);
        startActivity(intent);
    }
    public void ClickCS(View view){
        Intent intent = new Intent(this, Email.class);
        startActivity(intent);
    }
    public void ClickAbout(View view){
        Intent intent = new Intent(this, about.class);
        startActivity(intent);
    }
    public void ClickLogout2(View view){
        MainActivity.logout(this);
    }

    @Override
    public void onBackPressed(){
        if (isPressed){
            new SweetAlertDialog(utama.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("EXIT")
                    .setContentText("Are you sure want to exit ?")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            utama.this.finishAffinity();
                            System.exit(0);
                        }
                    })
                    .setCancelButton("CANCEL", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }else {
            StyleableToast.makeText(getApplicationContext(), "Please click back again to Exit!", Toast.LENGTH_SHORT, R.style.warning).show();
            isPressed = true;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                isPressed = false;
            }
        };
        new Handler().postDelayed(runnable,2000);
    }
}