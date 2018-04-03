package com.example.deepanshuaggarwal.receiptvault;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainInterface extends AppCompatActivity {
    ImageView scan,view,trans;
    TextView name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_interface);
        SQLiteDatabase db=openOrCreateDatabase("ReceiptData",MODE_PRIVATE,null);
        DatabaseHelper databaseHelper=new DatabaseHelper("Receipt",db);
        db.execSQL("drop table Receipt");
        databaseHelper.GetFailedTask(db,MainInterface.this);
        String Name=getIntent().getStringExtra("Name");
           name=findViewById(R.id.namei);
           scan=findViewById(R.id.scan);
           view=findViewById(R.id.receiptview);
           trans=findViewById(R.id.extra);
           name.setText(Name);
           scan.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent i=new Intent(MainInterface.this,Scan.class);
                   MainInterface.this.startActivity(i);
               }
           });
           view.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                Intent i=new Intent(MainInterface.this,Viewer.class);
                MainInterface.this.startActivity(i);
               }
           });

    }
}
//Only scan intent setted;
//YAha se background m purana upload ka kaam khatatm kar rha hu