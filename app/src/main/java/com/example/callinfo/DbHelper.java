package com.example.callinfo;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.callinfo.MainActivity.formatWithTime;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CallInfo.db";
    private static final int DATABASE_VERSION = 1;
    private static final String SP_KEY_DB_VER = "db_ver";
    private final Context mContext;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        initialize();
    }

    private void initialize() {
        if (databaseExists()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            int dbVersion = prefs.getInt(SP_KEY_DB_VER, 1);
            if (DATABASE_VERSION != dbVersion) {
                File dbFile = mContext.getDatabasePath(DATABASE_NAME);
                if (!dbFile.delete()) {
                }
            }
        }
        if (!databaseExists()) {
            createDatabase();
        }
    }

    private boolean databaseExists() {
        File dbFile = mContext.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    private void createDatabase() {
        String parentPath = mContext.getDatabasePath(DATABASE_NAME).getParent();
        String path = mContext.getDatabasePath(DATABASE_NAME).getPath();
        File file = new File(parentPath);
        if (!file.exists()) {
            if (!file.mkdir()) {
                System.out.println("Unable to create database directory");
                return;
            }
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = mContext.getAssets().open(DATABASE_NAME);
            outputStream = new FileOutputStream(path);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(SP_KEY_DB_VER, DATABASE_VERSION);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void insert(List<CallDTO> callDTOList){
        long result = 1;
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            Object maxDate = selectOne("MAX(date)", "1=1");
            if(maxDate != null){
                maxDate = formatWithTime.parse(maxDate.toString());
            }

            for (CallDTO callDTO : callDTOList) {
                Date callDate = formatWithTime.parse(callDTO.getDate());

                contentValues.put("number", callDTO.getNumber());
                contentValues.put("name", callDTO.getName());
                contentValues.put("date", callDTO.getDate());
                contentValues.put("type", callDTO.getType());
                contentValues.put("duration", callDTO.getDuration());

                if(maxDate == null || callDate.after((Date)maxDate)){
                    result = result * database.insert("CallLog", null, contentValues);
                }
            }

            if (result < 0) {
                System.out.println("Error when inserting data");
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public Object selectOne(String value, String condition){
        Cursor c;
        Object returnValue = null;
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            c = database.rawQuery("SELECT " + value + " FROM CallLog WHERE " + condition, null);
            c.moveToFirst();
            returnValue = c.getString(0);
            c.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return returnValue;
    }

    public ArrayList<GraphDataDTO> selectMultiple(String value, String condition){
        Cursor c = null;
        GraphDataDTO graphDataDTO;
        ArrayList<GraphDataDTO> graphDataDTOList = new ArrayList<>();
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            c = database.rawQuery("SELECT " + value + " FROM CallLog WHERE " + condition, null);
            c.moveToFirst();
            if(c.getCount() > 0){
                do{
                    graphDataDTO = new GraphDataDTO();
                    graphDataDTO.setDate(c.getString(0));
                    graphDataDTO.setCount(Integer.parseInt(c.getString(1)));
                    graphDataDTOList.add(graphDataDTO);
                } while(c.moveToNext());
            }
            c.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return graphDataDTOList;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }


}
