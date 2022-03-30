package com.example.food;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.muddzdev.styleabletoast.StyleableToast;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;
import com.tapadoo.alerter.OnShowAlertListener;
import com.zolad.zoominimageview.ZoomInImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.zhaiyifan.rememberedittext.RememberEditText;

public class MainActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {

    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    RememberEditText edtPname, edtPic, edtPanel, edtLamp, edtPole;
    EditText edtOthers;
    TextView txtDate, txtDevice, txtLatitude, txtLongitude, txtLocation;
    Button btnChoose, btnAdd, scanBtn, buttonMapsManual, buttonMapsAuto, buttonPicture;
    ZoomInImageView imageView;
    ImageView btMenu;
    Build build;
    String information, mes4, mes5, mes6, mes7, mes8, mes9, mes91, mes92;
    FusedLocationProviderClient fusedLocationProviderClient;
    Bitmap bitmap123;

    final int REQUEST_CODE_GALLERY = 999;

    public static SQLiteHelper sqLiteHelper;

    public static final String teks1 = "lat";
    public static final String teks2 = "lon";
    public static final String teks3 = "loc";

    public static final String teks4 = "pname";
    public static final String teks5 = "pic";
    public static final String teks6 = "panel";
    public static final String teks7 = "lamp";
    public static final String teks8 = "pole";
    public static final String teks9 = "others";

    public static final String teks10 = "pname2";
    public static final String teks11 = "pic2";
    public static final String teks12 = "panel2";
    public static final String teks13 = "lamp2";
    public static final String teks14 = "pole2";
    public static final String teks15 = "others2";

    DrawerLayout drawerLayout;
    OutputStream outputStream;
    ProgressDialog progressDialog;

    private ImageView toggleButton;
    boolean hasCameraFlash = false;
    boolean flashOn = false;
    boolean isPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btMenu = (ImageView) findViewById(R.id.bt_menu);

        RememberEditText.clearCache(MainActivity.this);

        toggleButton = (ImageView) findViewById(R.id.toggleButton);

        hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(hasCameraFlash){
                    if (flashOn){
                        flashOn = false;
                        toggleButton.setImageResource(R.drawable.ic_power);
                        try {
                            flashLightOff();
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        flashOn = true;
                        toggleButton.setImageResource(R.drawable.ic_poweron);
                        try {
                            flashLightOn();
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "No Flash available on your device", Toast.LENGTH_LONG);
                }
            }
        });

        btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        init();

        sqLiteHelper = new SQLiteHelper(this, "SmartStreetLight.sqlite", null, 18);

        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS CRUD(id INTEGER PRIMARY KEY AUTOINCREMENT, pname VARCHAR, pic VARCHAR, panel VARCHAR, lamp VARCHAR, pole VARCHAR, date VARCHAR, device VARCHAR, latitude VARCHAR, longitude VARCHAR, location VARCHAR, image BLOB, others VARCHAR)");

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if (TextUtils.isEmpty(edtPname.getText().toString()) || TextUtils.isEmpty(edtPic.getText().toString()) || TextUtils.isEmpty(edtPanel.getText().toString()) || TextUtils.isEmpty(edtLamp.getText().toString()) || TextUtils.isEmpty(edtPole.getText().toString()) || TextUtils.isEmpty(txtDate.getText().toString()) || TextUtils.isEmpty(txtDevice.getText().toString()) || TextUtils.isEmpty(txtLatitude.getText().toString()) || TextUtils.isEmpty(txtLongitude.getText().toString()) || TextUtils.isEmpty(txtLocation.getText().toString()) || TextUtils.isEmpty(edtOthers.getText().toString())){
//                        StyleableToast.makeText(getApplicationContext(), "Please fill all the data!!!", Toast.LENGTH_SHORT, R.style.resultfailed).show();
                        Alerter.create(MainActivity.this)
                                .setTitle("Add Data Failed!")
                                .setText("Please fill all the data")
                                .setIcon(R.drawable.ic_close)
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
                    }else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                requestPermissions(permission,WRITE_EXTERNAL_STORAGE_CODE);
                            }
                            else {
                                save();
                            }
                        }else{
                            save();
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });

        buttonMapsAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 45);
            }
        });

        buttonMapsManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
        });

        buttonPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        saveinfo();
        checkConnection();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void flashLightOn() throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String cameraId = cameraManager.getCameraIdList()[0];
        cameraManager.setTorchMode(cameraId,true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void flashLightOff() throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String cameraId = cameraManager.getCameraIdList()[0];
        cameraManager.setTorchMode(cameraId,false);
    }

    private void save(){
        sqLiteHelper.insertData(
                edtPname.getText().toString().trim(),
                edtPic.getText().toString().trim(),
                edtPanel.getText().toString().trim(),
                edtLamp.getText().toString().trim(),
                edtPole.getText().toString().trim(),
                txtDate.getText().toString().trim(),
                txtDevice.getText().toString().trim(),
                txtLatitude.getText().toString().trim(),
                txtLongitude.getText().toString().trim(),
                txtLocation.getText().toString().trim(),
                imageViewToByte(imageView),
                edtOthers.getText().toString().trim()
        );
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        Thread timer = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                    Intent intent = new Intent(getApplicationContext(),FoodList.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                    finish();
                    super.run();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        timer.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //do something
                StyleableToast.makeText(getApplicationContext(),"Added Successfully!", Toast.LENGTH_SHORT,R.style.logsuccess).show();
            }
        }, 2000 );//time in milisecond
        bitmap123 = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        File filepath = Environment.getExternalStorageDirectory();
        File dir = new File(filepath.getAbsolutePath()+"/SSL");
        dir.mkdirs();
        String pname = edtPname.getText().toString();
        String pic = edtPic.getText().toString();
        String panel = edtPanel.getText().toString();
        String lamp = edtLamp.getText().toString();
        String pole = edtPole.getText().toString();
        String date = txtDate.getText().toString();
        String device = txtDevice.getText().toString();
        String latitude = txtLatitude.getText().toString();
        String longitude = txtLongitude.getText().toString();
        String location = txtLocation.getText().toString();
        String others = edtOthers.getText().toString();
        File file = new File(dir, pname+"_"+pic+"_"+panel+"_"+lamp+"_"+pole+"_"+date+"_"+device+"_"+latitude+"_"+longitude+"_"+location+"_"+others+".png");
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap123.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkConnection() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(new ConnectionReceiver(),intentFilter);
        ConnectionReceiver.listener = this;
        ConnectivityManager manager = (ConnectivityManager)
        getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        showSnackBar(isConnected);
    }

    private void showSnackBar(boolean isConnected) {
        if(isConnected){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    StyleableToast.makeText(getApplicationContext(),"Connected to internet", Toast.LENGTH_SHORT,R.style.consuccess).show();
                    Alerter.create(MainActivity.this)
                            .setTitle("Connected to internet!")
                            .setText("Signal is on")
                            .setIcon(R.drawable.ic_signalon)
                            .setBackgroundColorRes(android.R.color.holo_green_dark)
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
            }, 2000 );
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    StyleableToast.makeText(getApplicationContext(),"Not connected to internet", Toast.LENGTH_SHORT,R.style.confailed).show();
                    Alerter.create(MainActivity.this)
                            .setTitle("Not connected to internet!")
                            .setText("Signal is off")
                            .setIcon(R.drawable.ic_signaloff)
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
            }, 2000 );
        }
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        showSnackBar(isConnected);
    }

    private void saveinfo(){
        information = build.MODEL;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();

                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        txtLatitude.setText(Html.fromHtml("<font color = '000000'></font>" + addresses.get(0).getLatitude()));
                        txtLongitude.setText(Html.fromHtml("<font color = '000000'></font>" + addresses.get(0).getLongitude()));
                        txtLocation.setText(Html.fromHtml("<font color = '000000'></font>" + addresses.get(0).getAddressLine(0)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void mapsactivity() {
        mes4 = edtPname.getText().toString();
        mes5 = edtPic.getText().toString();
        mes6 = edtPanel.getText().toString();
        mes7 = edtLamp.getText().toString();
        mes8 = edtPole.getText().toString();
        mes9 = edtOthers.getText().toString();
        Intent ekspor2 = new Intent(MainActivity.this, MapsActivity.class);
        ekspor2.putExtra(teks4,mes4);
        ekspor2.putExtra(teks5,mes5);
        ekspor2.putExtra(teks6,mes6);
        ekspor2.putExtra(teks7,mes7);
        ekspor2.putExtra(teks8,mes8);
        ekspor2.putExtra(teks9,mes9);
        startActivity(ekspor2);
    }

    public void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else{
                StyleableToast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT, R.style.warning).show();
            }
            return;
        }else if(requestCode == 100){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
            else{
                StyleableToast.makeText(getApplicationContext(), "You don't have permission to access camera!", Toast.LENGTH_SHORT, R.style.warning).show();
            }
            return;
        }else if(requestCode == 45) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                StyleableToast.makeText(getApplicationContext(), "You don't have permission to access location!", Toast.LENGTH_SHORT, R.style.warning).show();
            }
            return;
        }else if(requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapsactivity();
            } else {
                StyleableToast.makeText(getApplicationContext(), "You don't have permission to access location!", Toast.LENGTH_SHORT, R.style.warning).show();
            }
            return;
        }else if(requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                save();
            } else {
                StyleableToast.makeText(getApplicationContext(), "You don't have permission to save image!", Toast.LENGTH_SHORT, R.style.warning).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void ClickLogo(View view){
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    }

    public void ClickHome(View view){
        redirectActivity(this, utama.class);
    }

    public void ClickAdd(View view){
        redirectActivity(this, MainActivity.class);
    }

    public void ClickListData(View view){
        redirectActivity(this, FoodList.class);
    }

    public void ClickLogout(View view){
        logout(this);
    }

    public void ClickExit(View view){
        exit(this);
    }

    public void ClickAboutUs(View view){
        redirectActivity(this, about.class);
    }

    public void ClickFAQ(View view){
        redirectActivity(this, ThirdActivity.class);
    }

    public void ClickCriticism(View view){
        redirectActivity(this, Email.class);
    }

    public static void logout(Activity activity){
        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("LOGOUT")
                .setContentText("Are you sure want to logout ?")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        activity.startActivity(new Intent(activity,login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                })
                .setCancelButton("CANCEL", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    public static void exit(Activity activity){
        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("EXIT")
                .setContentText("Are you sure want to exit ?")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        activity.finishAffinity();
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
    }

    public static void redirectActivity(Activity activity,Class aClass){
        Intent intent = new Intent(activity,aClass);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        activity.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
        checkConnection();
    }

    @Override
    public void onBackPressed(){
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(captureImage);
            Date dateCurrent = Calendar.getInstance().getTime();
            txtDate.setText(dateCurrent.toString());
            txtDevice.setText(information);
            StyleableToast.makeText(getApplicationContext(), "Image has been Captured!", Toast.LENGTH_SHORT, R.style.result).show();
        }else if(requestCode == REQUEST_CODE_GALLERY){
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                InputStream inputStream = null;

                try {
                    assert uri != null;
                    inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);
                    Date dateCurrent = Calendar.getInstance().getTime();
                    txtDate.setText(dateCurrent.toString());
                    txtDevice.setText(information);
                    StyleableToast.makeText(getApplicationContext(), "Image has been Selected!", Toast.LENGTH_SHORT, R.style.result).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }else{
                StyleableToast.makeText(getApplicationContext(), "No Image Selected!", Toast.LENGTH_SHORT, R.style.warning).show();
            }
        }else if (result != null) {
            if (result.getContents() != null) {
                new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("SCANNING RESULT")
                        .setContentText(result.getContents())
                        .setCustomImage(R.drawable.ic_code)
                        .setConfirmText("P")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                edtPole.setText(result.getContents());
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .setCancelButton("SCAN", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                scanCode();
                            }
                        })
                        .setNeutralButton("L", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                edtLamp.setText(result.getContents());
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();

            } else {
                StyleableToast.makeText(getApplicationContext(), "No Scan Results!", Toast.LENGTH_SHORT, R.style.warning).show();
            }
        }else {
            StyleableToast.makeText(getApplicationContext(), "No Image Captured!", Toast.LENGTH_SHORT, R.style.warning).show();
        }
    }

    private void init(){
        Intent ekspor = getIntent();
        final String mes1 = ekspor.getStringExtra(teks1);
        final String mes2 = ekspor.getStringExtra(teks2);
        final String mes3 = ekspor.getStringExtra(teks3);
        final String mes10 = ekspor.getStringExtra(teks10);
        final String mes11 = ekspor.getStringExtra(teks11);
        final String mes12 = ekspor.getStringExtra(teks12);
        final String mes13 = ekspor.getStringExtra(teks13);
        final String mes14 = ekspor.getStringExtra(teks14);
        final String mes15 = ekspor.getStringExtra(teks15);

        edtPname = (RememberEditText) findViewById(R.id.edtPname);
        edtPname.setText(mes10);
        edtPic = (RememberEditText) findViewById(R.id.edtPic);
        edtPic.setText(mes11);
        edtPanel = (RememberEditText) findViewById(R.id.edtPanel);
        edtPanel.setText(mes12);
        edtLamp = (RememberEditText) findViewById(R.id.edtLamp);
        edtLamp.setText(mes13);
        edtPole = (RememberEditText) findViewById(R.id.edtPole);
        edtPole.setText(mes14);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtDate.setText("");
        txtDevice = (TextView) findViewById(R.id.txtDevice);
        txtDevice.setText("");
        txtLatitude = (TextView) findViewById(R.id.txtLatitude);
        txtLatitude.setText(mes1);
        txtLongitude = (TextView) findViewById(R.id.txtLongitude);
        txtLongitude.setText(mes2);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtLocation.setText(mes3);
        edtOthers = (EditText) findViewById(R.id.edtOthers);
        edtOthers.setText(mes15);
        btnChoose = (Button) findViewById(R.id.btnChoose);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        imageView = (ZoomInImageView) findViewById(R.id.imageView);
        scanBtn = (Button) findViewById(R.id.scanBtn);
        buttonMapsAuto = (Button) findViewById(R.id.buttonMapsAuto);
        buttonMapsManual = (Button) findViewById(R.id.buttonMapsManual);
        buttonPicture = (Button) findViewById(R.id.buttonPicture);
    }
}