package com.example.callinfo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static SimpleDateFormat formatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat formatWithOutTime = new SimpleDateFormat("yyyy-MM-dd");
    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateDatabase();
        updateRate();
        viewWelcomeAlert();
    }

    public void updateDatabase(){
        dbHelper = new DbHelper(getApplicationContext());
        List<CallDTO> callDTOList = readCallLog();
        dbHelper.insert(callDTOList);
    }

    public void updateRate(){
        try {
            dbHelper = new DbHelper(getApplicationContext());
            double totalCount = Double.valueOf(dbHelper.selectOne("COUNT(*)", "1=1").toString());
            double missedCount = Double.valueOf(dbHelper.selectOne("COUNT(*)", "type = 'missed'").toString());
            Double missedRate =  (missedCount / totalCount) * 100;

            TextView percentage = (TextView) findViewById(R.id.txtPercentage);
            percentage.setTextColor(missedRate >= 75 ? Color.parseColor("#1CB111") : missedRate >= 50 ? Color.parseColor("#FB6E03") : Color.GREEN);
            percentage.setText(String.format("%.2f", missedRate) + " %");
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void viewWelcomeAlert(){
        AlertDialog.Builder welcomeAlert = new AlertDialog.Builder(MainActivity.this);

        welcomeAlert.setTitle("Welcome to CallInfo");
        welcomeAlert.setMessage("Do you want to continue ?");
        welcomeAlert.setIcon(android.R.drawable.ic_dialog_alert);
        welcomeAlert.setCancelable(false);
        welcomeAlert.setPositiveButton("Continue",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        welcomeAlert.setNegativeButton("Exit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        welcomeAlert.show();
    }

    private List<CallDTO> readCallLog() {
        List<CallDTO> callDTOList = new ArrayList<>();
        CallDTO callDTO;
        try {
            Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
            int cNumber = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int cName = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int cType = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int cDate = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int cDuration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

            while (managedCursor.moveToNext()) {
                String number = managedCursor.getString(cNumber);
                if(number.startsWith("+61")){
                    number = number.replace("+61", "0");
                }
                else if(number.startsWith("+")){
                    continue;
                }

                String name = managedCursor.getString(cName);
                String callType = managedCursor.getString(cType);
                String callDate = managedCursor.getString(cDate);
                String date = formatWithTime.format(new Date(Long.valueOf(callDate)));
                String duration = managedCursor.getString(cDuration);

                String type = null;
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        type = "outgoing";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        type = "incoming";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        type = "missed";
                        break;

                    case CallLog.Calls.REJECTED_TYPE:
                        type = "missed";
                        break;
                }
                callDTO = new CallDTO();

                callDTO.setNumber(number);
                callDTO.setName(name);
                callDTO.setDate(date.toString());
                callDTO.setType(type);
                callDTO.setDuration(Double.valueOf(duration));
                callDTOList.add(callDTO);
//                System.out.println("Phone Number - " + number + " Name - " + name + " Type - " + type + " Date - " + date + " Duration in sec - " + duration);
            }
            managedCursor.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return callDTOList;
    }

    public void moveToSetParameterActivity(View v){
        Intent intent = new Intent(MainActivity.this, SetParameterActivity.class);
        startActivity(intent);
    }


}