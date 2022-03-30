package com.example.food;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.inputmethodservice.Keyboard;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.muddzdev.styleabletoast.StyleableToast;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;
import com.tapadoo.alerter.OnShowAlertListener;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

public class FoodList extends AppCompatActivity {

    GridView gridView;
    ArrayList<Food> list;
    FoodListAdapter adapter = null;
    DrawerLayout drawerLayout;
    ImageView btMenu;
    SearchView searchView;
    Integer CurrentID;
    private ImageView exportIV;
    FloatingActionButton fab, fab1, fab2;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;

    boolean isOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_list_activity);
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btMenu = (ImageView) findViewById(R.id.bt_menu);
        searchView = findViewById(R.id.searchView);
        exportIV = findViewById(R.id.iv_import);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String foldername = "SSL";
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory() + File.separator + foldername), "*/*");
                startActivity(intent);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String foldername = "EXCEL SPJU";
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory() + File.separator + foldername), "*/*");
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        exportIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions, 1);
                    } else {
                        importData();
                    }
                } else {
                    importData();
                }

            }
        });

        btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        gridView = (GridView) findViewById(R.id.gridView);
        list = new ArrayList<>();
        adapter = new FoodListAdapter(this, R.layout.food_items, list);
        gridView.setAdapter(adapter);

        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM CRUD");
        list.clear();
        while (cursor.moveToNext()){
            String id = cursor.getString(0);
            String pname = cursor.getString(1);
            String pic = cursor.getString(2);
            String panel = cursor.getString(3);
            String lamp = cursor.getString(4);
            String pole = cursor.getString(5);
            String date = cursor.getString(6);
            String device = cursor.getString(7);
            String latitude = cursor.getString(8);
            String longitude = cursor.getString(9);
            String location = cursor.getString(10);
            byte[] image = cursor.getBlob(11);
            String others = cursor.getString(12);

            list.add(new Food(pname, pic, panel, lamp, pole, date, device, latitude, longitude, location, image, others, id));
        }
        adapter.notifyDataSetChanged();

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(adapter.pilter){

                }else{
                    new SweetAlertDialog(FoodList.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                            .setTitleText("Warning!!!")
                            .setCustomImage(R.drawable.ic_help)
                            .setContentText("Please choose your option !")
                            .setConfirmText("Update")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    Cursor c = MainActivity.sqLiteHelper.getData("SELECT id FROM CRUD");
                                    ArrayList<Integer> arrID = new ArrayList<Integer>();
                                    while (c.moveToNext()){
                                        arrID.add(c.getInt(0));
                                    }
                                    CurrentID = arrID.get(position);
                                    showDialogUpdate(FoodList.this, arrID.get(position));
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .setCancelButton("Delete", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    Cursor c = MainActivity.sqLiteHelper.getData("SELECT id FROM CRUD");
                                    ArrayList<Integer> arrID = new ArrayList<Integer>();
                                    while (c.moveToNext()){
                                        arrID.add(c.getInt(0));
                                    }
                                    showDialogDelete(FoodList.this, arrID.get(position));
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
                return true;
            }
        });
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

    private void animateFab(){
        if (isOpen){
            fab.startAnimation(rotateBackward);
            fab1.startAnimation(fabClose);
            fab2.startAnimation(fabClose);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isOpen=false;
        }
        else{
            fab.startAnimation(rotateForward);
            fab1.startAnimation(fabOpen);
            fab2.startAnimation(fabOpen);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isOpen=true;
        }
    }

    private void importData() {
        if(list.size()>0){
            createXlFile();
        } else {
            StyleableToast.makeText(getApplicationContext(), "List are empty!", Toast.LENGTH_SHORT, R.style.resultfailed).show();
        }
    }

    private void createXlFile() {
        // File filePath = new File(Environment.getExternalStorageDirectory() + "/Demo.xls");
        Workbook wb = new HSSFWorkbook();

        Cell cell = null;

        Sheet sheet = null;
        sheet = wb.createSheet("Data SPJU");
        //Now column and row
        Row row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("Project Name");

        cell = row.createCell(1);
        cell.setCellValue("PIC");

        cell = row.createCell(2);
        cell.setCellValue("Panel Name");

        cell = row.createCell(3);
        cell.setCellValue("Lamp Code");

        cell = row.createCell(4);
        cell.setCellValue("Pole Code");

        cell = row.createCell(5);
        cell.setCellValue("Date");

        cell = row.createCell(6);
        cell.setCellValue("Device");

        cell = row.createCell(7);
        cell.setCellValue("Latitude");

        cell = row.createCell(8);
        cell.setCellValue("Longitude");

        cell = row.createCell(9);
        cell.setCellValue("Location");

        cell = row.createCell(10);
        cell.setCellValue("Image");

        cell = row.createCell(11);
        cell.setCellValue("Others");

        //column width
        sheet.setColumnWidth(0, (30 * 200));
        sheet.setColumnWidth(1, (30 * 200));
        sheet.setColumnWidth(2, (30 * 200));
        sheet.setColumnWidth(3, (30 * 200));
        sheet.setColumnWidth(4, (30 * 200));
        sheet.setColumnWidth(5, (30 * 200));
        sheet.setColumnWidth(6, (30 * 200));
        sheet.setColumnWidth(7, (30 * 200));
        sheet.setColumnWidth(8, (30 * 200));
        sheet.setColumnWidth(9, (30 * 200));
        sheet.setColumnWidth(10, (30 * 200));
        sheet.setColumnWidth(11, (30 * 200));

        for (int i = 0; i < list.size(); i++) {
            Row row1 = sheet.createRow(i + 1);

            cell = row1.createCell(0);
            cell.setCellValue(list.get(i).getPname());

            cell = row1.createCell(1);
            cell.setCellValue((list.get(i).getPic()));
            //  cell.setCellStyle(cellStyle);

            cell = row1.createCell(2);
            cell.setCellValue(list.get(i).getPanel());

            cell = row1.createCell(3);
            cell.setCellValue(list.get(i).getLamp());

            cell = row1.createCell(4);
            cell.setCellValue((list.get(i).getPole()));
            //  cell.setCellStyle(cellStyle);

            cell = row1.createCell(5);
            cell.setCellValue(list.get(i).getDate());

            cell = row1.createCell(6);
            cell.setCellValue(list.get(i).getDevice());

            cell = row1.createCell(7);
            cell.setCellValue((list.get(i).getLatitude()));
            //  cell.setCellStyle(cellStyle);

            cell = row1.createCell(8);
            cell.setCellValue(list.get(i).getLongitude());

            cell = row1.createCell(9);
            cell.setCellValue(list.get(i).getLocation());

            cell = row1.createCell(10);
            cell.setCellValue(list.get(i).getImage().toString());
            //  cell.setCellStyle(cellStyle);

            cell = row1.createCell(11);
            cell.setCellValue(list.get(i).getOthers());

            sheet.setColumnWidth(0, (30 * 200));
            sheet.setColumnWidth(1, (30 * 200));
            sheet.setColumnWidth(2, (30 * 200));
            sheet.setColumnWidth(3, (30 * 200));
            sheet.setColumnWidth(4, (30 * 200));
            sheet.setColumnWidth(5, (30 * 200));
            sheet.setColumnWidth(6, (30 * 200));
            sheet.setColumnWidth(7, (30 * 200));
            sheet.setColumnWidth(8, (30 * 200));
            sheet.setColumnWidth(9, (30 * 200));
            sheet.setColumnWidth(10, (30 * 200));
            sheet.setColumnWidth(11, (30 * 200));
        }

        String folderName = "Excel SPJU";
        String fileName = folderName + System.currentTimeMillis() + ".xls";
        String path = Environment.getExternalStorageDirectory() + File.separator + folderName + File.separator + fileName;

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + folderName);
        if (!file.exists()) {
            file.mkdirs();
        }

        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(path);
            wb.write(outputStream);
            // ShareViaEmail(file.getParentFile().getName(),file.getName());
            StyleableToast.makeText(getApplicationContext(),"Excel Created in " + path, Toast.LENGTH_SHORT,R.style.logsuccess).show();
        } catch (IOException e) {
            e.printStackTrace();
            StyleableToast.makeText(getApplicationContext(), "Not OK!", Toast.LENGTH_LONG, R.style.resultfailed).show();
            try {
                outputStream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

    ImageView imageView;
    EditText edtPname, edtPic, edtPanel, edtLamp, edtPole, edtOthers;
    TextView txtDate, txtDevice, txtLatitude, txtLongitude,txtLocation;
    Button scanBtn, btnUpdate;

    private void showDialogUpdate(Activity activity, int position){
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_food_activity);
        dialog.setTitle("Update");

        edtPname = (EditText) dialog.findViewById(R.id.edtPname);
        edtPic = (EditText) dialog.findViewById(R.id.edtPic);
        edtPanel = (EditText) dialog.findViewById(R.id.edtPanel);
        edtLamp = (EditText) dialog.findViewById(R.id.edtLamp);
        edtPole = (EditText) dialog.findViewById(R.id.edtPole);
        txtDate = (TextView) dialog.findViewById(R.id.txtDate);
        txtDevice = (TextView) dialog.findViewById(R.id.txtDevice);
        txtLatitude = (TextView) dialog.findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) dialog.findViewById(R.id.txtLongitude);
        txtLocation = (TextView) dialog.findViewById(R.id.txtLocation);
        edtOthers = (EditText) dialog.findViewById(R.id.edtOthers);
        imageView = (ImageView) dialog.findViewById(R.id.imageViewFood);
        scanBtn = (Button) dialog.findViewById(R.id.scanBtn);
        btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate);

        txtDate.setText("");
        txtDevice.setText("");
        txtLatitude.setText("");
        txtLongitude.setText("");
        txtLocation.setText("");

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.97);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.8);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        Cursor c2 = MainActivity.sqLiteHelper.getData("SELECT * FROM CRUD");
            while (c2.moveToNext()){
                Integer id = c2.getInt(0);
                String pname = c2.getString(1);
                String pic = c2.getString(2);
                String panel = c2.getString(3);
                String lamp = c2.getString(4);
                String pole = c2.getString(5);
                String date = c2.getString(6);
                String device = c2.getString(7);
                String latitude = c2.getString(8);
                String longitude = c2.getString(9);
                String location = c2.getString(10);
                byte[] image = c2.getBlob(11);
                String others = c2.getString(12);

                if (CurrentID.equals(id)) {
                    edtPname.setText(pname);
                    edtPic.setText(pic);
                    edtPanel.setText(panel);
                    edtLamp.setText(lamp);
                    edtPole.setText(pole);
                    txtDate.setText(date);
                    txtDevice.setText(device);
                    txtLatitude.setText(latitude);
                    txtLongitude.setText(longitude);
                    txtLocation.setText(location);
                    edtOthers.setText(others);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    imageView.setImageBitmap(bitmap);
                }
            }

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if (TextUtils.isEmpty(edtPname.getText().toString()) || TextUtils.isEmpty(edtPic.getText().toString()) || TextUtils.isEmpty(edtPanel.getText().toString()) || TextUtils.isEmpty(edtLamp.getText().toString()) || TextUtils.isEmpty(edtPole.getText().toString()) || TextUtils.isEmpty(edtOthers.getText().toString())){
                        StyleableToast.makeText(getApplicationContext(), "Please fill all the data!!!", Toast.LENGTH_SHORT, R.style.resultfailed).show();
                    }else{
                        MainActivity.sqLiteHelper.updateData(
                                edtPname.getText().toString().trim(),
                                edtPic.getText().toString().trim(),
                                edtPanel.getText().toString().trim(),
                                edtLamp.getText().toString().trim(),
                                edtPole.getText().toString().trim(),
                                edtOthers.getText().toString().trim(),
                                position
                        );

                        dialog.dismiss();
                        StyleableToast.makeText(getApplicationContext(), "Update Successfully!!!", Toast.LENGTH_SHORT, R.style.result).show();
                    }
                }
                catch (Exception error){
                    Log.e("Update error: ", error.getMessage());
                }
                updateFoodList();
            }
        });
    }

    private void showDialogDelete(FoodList foodList, final int idFood){
        new SweetAlertDialog(FoodList.this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Warning!!!")
            .setContentText("Are you sure want to delete this data ?")
            .setConfirmText("OK")
            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    try{
                        MainActivity.sqLiteHelper.deleteData(idFood);
                        sDialog.dismissWithAnimation();
                        StyleableToast.makeText(getApplicationContext(), "Delete Successfully!!!", Toast.LENGTH_SHORT, R.style.result).show();
                    } catch (Exception e) {
                        Log.e("error",e.getMessage());
                    }
                    updateFoodList();
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

    private void updateFoodList(){
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM CRUD");
        list.clear();
        while (cursor.moveToNext()){
            String id = cursor.getString(0);
            String pname = cursor.getString(1);
            String pic = cursor.getString(2);
            String panel = cursor.getString(3);
            String lamp = cursor.getString(4);
            String pole = cursor.getString(5);
            String date = cursor.getString(6);
            String device = cursor.getString(7);
            String latitude = cursor.getString(8);
            String longitude = cursor.getString(9);
            String location = cursor.getString(10);
            byte[] image = cursor.getBlob(11);
            String others = cursor.getString(12);

            list.add(new Food(pname, pic, panel, lamp, pole, date, device, latitude, longitude, location, image, others, id));
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);
    }

    @Override
    public void onBackPressed(){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
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
        }
    }
}