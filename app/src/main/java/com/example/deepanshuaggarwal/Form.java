package com.example.deepanshuaggarwal.receiptvault;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;

public class Form extends AppCompatActivity {
    ImageView icon,im;
    private StorageReference storageReference;
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
    EditText date,category,warranty,seller,comment;
    Button imb;
    public  boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    public String getReceiptNumber(){
        SharedPreferences sharedPreferences=getSharedPreferences("ReceiptNumber",MODE_PRIVATE);
        int k=sharedPreferences.getInt("Current",-1);
        if(k==-1){
            sharedPreferences.edit().putInt("Current",1).apply();
            return "RVAULT1";
        }
        else{

            sharedPreferences.edit().putInt("Current",k+1).apply();
            return "RVAULT"+Integer.valueOf(k+1).toString();
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage,String filename){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir",Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,filename);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
    private Bitmap loadImageFromStorage(String path,String filename)
    {

        try {
            File f=new File(path, filename);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }

    }



    TextWatcher tw = new TextWatcher() {
        private String current = "";
        private String ddmmyyyy = "DDMMYYYY";
        private Calendar cal = Calendar.getInstance();

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }
                //Fix for pressing delete next to a forward slash
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8){
                    clean = clean + ddmmyyyy.substring(clean.length());
                }else{
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    int day  = Integer.parseInt(clean.substring(0,2));
                    int mon  = Integer.parseInt(clean.substring(2,4));
                    int year = Integer.parseInt(clean.substring(4,8));

                    mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                    cal.set(Calendar.MONTH, mon-1);
                    year = (year<1900)?1900:(year>2100)?2100:year;
                    cal.set(Calendar.YEAR, year);
                    // ^ first set year for the line below to work correctly
                    //with leap years - otherwise, date e.g. 29/02/2012
                    //would be automatically corrected to 28/02/2012

                    day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                    clean = String.format("%02d%02d%02d",day, mon, year);
                }

                clean = String.format("%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                date.setText(current);
                date.setSelection(sel < current.length() ? sel : current.length());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }};

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
            final String rno=getReceiptNumber();
            final String filename=rno+".jpg";
            final Bitmap image=getIntent().getParcelableExtra("Image");
            final String k=saveToInternalStorage(image,filename);
                     String result="Failed";

            if(isNetworkConnected() &&isInternetAvailable()){

                Uri file =Uri.fromFile(new File(k+"/"+filename));
                ToFireBase toFireBase=new ToFireBase();
                result=toFireBase.doInBackground(file.toString(),filename);
            }



        icon=findViewById(R.id.icon);
        icon.setImageBitmap(image);
        date=findViewById(R.id.date);
        category=findViewById(R.id.category);
        warranty=findViewById(R.id.warranty);
        seller=findViewById(R.id.seller);
        comment=findViewById(R.id.comment);
        imb=findViewById(R.id.submitform);
        date.addTextChangedListener(tw);
            final String imagelink=result;
        imb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("Location",k+"  :  "+filename);

                SQLiteDatabase db=openOrCreateDatabase("ReceiptData",MODE_PRIVATE,null);
                DatabaseHelper databaseHelper=new DatabaseHelper("Receipt",db);
                databaseHelper.addrow(rno,category.getText().toString(),date.getText().toString(),Integer.parseInt(warranty.getText().toString()),seller.getText().toString(),comment.getText().toString(),k,filename,db,imagelink);
                Toast.makeText(Form.this,"Receipt Added Successfully",Toast.LENGTH_SHORT).show();
                Intent i=new Intent(Form.this,MainInterface.class);
                Form.this.startActivity(i);






            }
        });

    }
}
//If stored to firebase then link else FAILED
