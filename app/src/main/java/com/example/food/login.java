package com.example.food;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.muddzdev.styleabletoast.StyleableToast;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;
import com.tapadoo.alerter.OnShowAlertListener;

public class login extends AppCompatActivity {
    EditText username, password;
    Button btnLogin;
    String correct_username = "panasonic";
    String correct_password = "spju";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //validate inputs
                    if (TextUtils.isEmpty(username.getText().toString()) ||
                            TextUtils.isEmpty(password.getText().toString())){
                        Alerter.create(login.this)
                                .setTitle("Empty Data Provided!")
                                .setText("Please fill the data correctly")
                                .setIcon(R.drawable.ic_priority)
                                .setBackgroundColorRes(android.R.color.holo_orange_light)
                                .setDuration(2000)
                                .enableSwipeToDismiss()
                                .enableProgress(true)
                                .setProgressColorRes(R.color.design_default_color_primary)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setOnShowListener(new OnShowAlertListener() {
                                    @Override
                                    public void onShow() {

                                    }
                                })
                                .setOnHideListener(new OnHideAlertListener() {
                                    @Override
                                    public void onHide() {

                                    }
                                })
                                .show();
                    }
                    else
                    if(username.getText().toString().equals(correct_username)){
                        //check password
                        if (password.getText().toString().equals(correct_password)){
                            StyleableToast.makeText(getApplicationContext(),"Login Success", Toast.LENGTH_SHORT,R.style.logsuccess).show();
                            openUtama();
                            username.setText("");
                            password.setText("");
                        }
                        else {
                            Alerter.create(login.this)
                                    .setTitle("Login Failed!")
                                    .setText("Invalid Username or Password")
                                    .setIcon(R.drawable.ic_clear)
                                    .setBackgroundColorRes(android.R.color.holo_red_dark)
                                    .setDuration(2000)
                                    .enableSwipeToDismiss()
                                    .enableProgress(true)
                                    .setProgressColorRes(R.color.design_default_color_primary)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setOnShowListener(new OnShowAlertListener() {
                                        @Override
                                        public void onShow() {

                                        }
                                    })
                                    .setOnHideListener(new OnHideAlertListener() {
                                        @Override
                                        public void onHide() {

                                        }
                                    })
                                    .show();
                        }
                    }
                    else {
                        Alerter.create(login.this)
                                .setTitle("Login Failed!")
                                .setText("Invalid Username or Password")
                                .setIcon(R.drawable.ic_clear)
                                .setBackgroundColorRes(android.R.color.holo_red_dark)
                                .setDuration(2000)
                                .enableSwipeToDismiss()
                                .enableProgress(true)
                                .setProgressColorRes(R.color.design_default_color_primary)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setOnShowListener(new OnShowAlertListener() {
                                    @Override
                                    public void onShow() {

                                    }
                                })
                                .setOnHideListener(new OnHideAlertListener() {
                                    @Override
                                    public void onHide() {

                                    }
                                })
                                .show();
                    }
                }
            }
        );
    }

    public void openUtama() {
        Intent intent = new Intent(this, utama.class);
        startActivity(intent);
    }
}