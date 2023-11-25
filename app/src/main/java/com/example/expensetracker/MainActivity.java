package com.example.expensetracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private final String[] debitKeywords = new String[]{"debited","withdrawn","spent"," w/d ","Money Transfer"};
    private final String[] creditKeywords = new String[]{"credited"};

    private final String[] ignoreKeywords = new String[]{"rummy","RummyCircle","failed","OTP","requested money","Swiggy Money"};


    private Button load;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Get SMS permission for the first time, the app is launched
        ActivityCompat.requestPermissions ( this,
                new String[] {Manifest.permission.READ_SMS},
                PackageManager.PERMISSION_GRANTED);

        load = findViewById(R.id.load);
        layout = findViewById(R.id.container);

        load.setOnClickListener(this::loadsms);
    }

    private void addCard(Expense expense) {
        View cardView = getLayoutInflater().inflate(R.layout.card_activity,null);
        TextView smsview = cardView.findViewById(R.id.msgBlock);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
        String formattedDate = dateFormat.format(expense.getDate());
        smsview.setText(String.format("%s -> \t%s\t\t%s",formattedDate, expense.getModeOfPayment(), expense.getAmount()));
        layout.addView(cardView);
    }

    private void addCard(String message) {
        View cardView = getLayoutInflater().inflate(R.layout.card_activity,null);
        TextView smsview = cardView.findViewById(R.id.msgBlock);
        smsview.setText(message);
        layout.addView(cardView);
    }


    private void loadsms(View view) {
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms"),null, null,null, null);

        addCard("======= Amount spent ========");
        createExpenseCard(cursor, debitKeywords);

        addCard("======= Amount Credited ========");
        createExpenseCard(cursor, creditKeywords);
    }

    private void createExpenseCard(Cursor cursor, String[] keywords) {
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            String sms = cursor.getString(cursor.getColumnIndex("body"));
            Date date = new Date(cursor.getLong(cursor.getColumnIndex("date")));

            if(Arrays.stream(keywords).anyMatch(sms::contains) && Arrays.stream(ignoreKeywords).noneMatch(sms::contains)){
                System.out.println(sms);
                Expense expense = Expense.fromSms(sms);
                expense.setDate(date);
                addCard(expense);
            }
            cursor.moveToNext();
        }
    }

}