package com.example.food;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.muddzdev.styleabletoast.StyleableToast;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Email extends AppCompatActivity {
    EditText _txtMessage, _txtMessage1, _txtMessage2, _txtUsername, _txtPassword;
    TextView _txtEmail,rateCount;
    Button _btnSend;
    RatingBar ratingBar;
    float rateValue;
    DrawerLayout drawerLayout;
    ImageView btMenu;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btMenu = (ImageView) findViewById(R.id.bt_menu);

        rateCount = findViewById(R.id.rateCount);
        ratingBar = findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rateValue = ratingBar.getRating();

                if (rateValue==0)
                    rateCount.setText("");
                else if (rateValue<=1 && rateValue>0)
                    rateCount.setText(Html.fromHtml("<font color = '000000'> Rating : </font>" + rateValue + "<font color = '#FF0000'> --> BAD </font>"));
                else if (rateValue<=2 && rateValue>1)
                    rateCount.setText(Html.fromHtml(" <font color = '000000'> Rating : </font>" + rateValue + "<font color = '#FFB700'> --> OK </font>"));
                else if (rateValue<=3 && rateValue>2)
                    rateCount.setText(Html.fromHtml("<font color = '000000'> Rating : </font>" + rateValue  + "<font color = '#00FF00'> --> GOOD </font>"));
                else if (rateValue<=4 && rateValue>3)
                    rateCount.setText(Html.fromHtml("<font color = '000000'> Rating : </font>" + rateValue + "<font color = '#00AAFF'> --> VERY GOOD </font>"));
                else if (rateValue<=5 && rateValue>4)
                    rateCount.setText(Html.fromHtml("<font color = '000000'> Rating : </font>" + rateValue + "<font color = '#F700FF'> --> BEST </font>"));
            }
        });

        btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
        _txtEmail=(TextView) findViewById(R.id.txtEmail);
        _txtMessage=(EditText) findViewById(R.id.txtMessage);
        _txtMessage1=(EditText) findViewById(R.id.txtMessage1);
        _txtMessage2=(EditText) findViewById(R.id.txtMessage2);
        _btnSend=(Button) findViewById(R.id.btnSend);
        _btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username="sslpanasonicpens@gmail.com";
                String password="pklpens2022";

                if (TextUtils.isEmpty(_txtMessage1.getText().toString())) {
                    _txtMessage1.setError("Required");
                    _txtMessage1.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(_txtMessage2.getText().toString())) {
                    _txtMessage2.setError("Required");
                    _txtMessage2.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(_txtMessage.getText().toString())) {
                    _txtMessage.setError("Required");
                    _txtMessage.requestFocus();
                    return;
                }
                String messageToSend=_txtMessage1.getText().toString() + "\n" + _txtMessage2.getText().toString() + "\n" + _txtMessage.getText().toString() + "\n" + rateCount.getText().toString();
                Properties props=new Properties();
                props.put("mail.smtp.auth","true");
                props.put("mail.smtp.starttls.enable","true");
                props.put("mail.smtp.host","smtp.gmail.com");
                props.put("mail.smtp.port","587");
                Session session=Session.getInstance(props,
                        new javax.mail.Authenticator(){
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });
                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(_txtEmail.getText().toString()));
                    message.setSubject("Sending email without opening gmail apps");
                    message.setText(messageToSend);
                    Transport.send(message);
                    StyleableToast.makeText(getApplicationContext(),"Send Successfully!", Toast.LENGTH_SHORT,R.style.logsuccess).show();
                    _txtMessage1.setText("");
                    _txtMessage2.setText("");
                    _txtMessage.setText("");
                    ratingBar.setRating(0);
                    rateCount.setText("");
                }catch (MessagingException e){
                    throw new RuntimeException(e);
                }
            }
        });
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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