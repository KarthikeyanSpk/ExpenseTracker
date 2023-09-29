package com.example.expensetracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;


import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private final String[] debitKeywords = new String[]{"debited","withdrawn","spent","w/d","Money Transfer"};
    private final String[] creditKeywords = new String[]{"credited"};

    private Button load;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions ( this,
                new String[] {Manifest.permission.READ_SMS},
                PackageManager.PERMISSION_GRANTED);

        load = findViewById(R.id.load);
        layout = findViewById(R.id.container);

        load.setOnClickListener(this::loadsms);
    }

    private void addCard(String sms) {
        View cardView = getLayoutInflater().inflate(R.layout.card_activity,null);
        TextView smsview = cardView.findViewById(R.id.msgBlock);
        smsview.setText(sms);
        layout.addView(cardView);
    }



    private void loadsms(View view) {
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms"),null, null,null, null);
        cursor.moveToFirst();

        addCard("======= Amount spent ========");
        while(!cursor.isAfterLast()){
            String sms = cursor.getString(12);
            if(Arrays.stream(debitKeywords).anyMatch(sms::contains)){
                addCard(sms);
            }
            cursor.moveToNext();
        }
        addCard("======= Amount Credited ========");

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            String sms = cursor.getString(12);
            if(Arrays.stream(creditKeywords).anyMatch(sms::contains)){
                addCard(sms);
            }
            cursor.moveToNext();
        }


    }
}