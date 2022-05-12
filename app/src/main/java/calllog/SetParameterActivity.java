package calllog;

import static calllog.MainActivity.formatWithOutTime;
import static calllog.MainActivity.formatWithTime;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.test.calllog.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SetParameterActivity extends AppCompatActivity {

    final Calendar myCalendar = Calendar.getInstance();

    Switch swtOutgoing;
    Switch swtIncoming;
    Switch swtMissed;
    Button dateBtn;
    Button fromDateBtn;
    Button toDateBtn;
    EditText editNumber;

    String type;
    String date;
    String fromDate;
    String toDate;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_parameter);

        swtOutgoing = (Switch)findViewById(R.id.swtOutgoing);
        swtIncoming = (Switch)findViewById(R.id.swtIncoming);
        swtMissed = (Switch)findViewById(R.id.swtMissed);

        dateBtn = (Button) findViewById(R.id.btnDate);
        fromDateBtn = (Button) findViewById(R.id.btnDateFrom);
        toDateBtn = (Button) findViewById(R.id.btnDateTo);

        editNumber = (EditText)findViewById(R.id.edtPhoneNumber);

        handleOutgoingChecked();
        handleIncomingChecked();
        handleMissedChecked();

    }

    public void setDateValue(View v){
        switch (v.getId()){
            case R.id.btnDate: setDate((Button) findViewById(R.id.btnDate));
                break;
            case R.id.btnDateFrom: setDate((Button) findViewById(R.id.btnDateFrom));
                break;
            case R.id.btnDateTo: setDate((Button) findViewById(R.id.btnDateTo));
                break;
        }
    }

    void setDate(final Button btn){
        final DatePickerDialog.OnDateSetListener dpDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateButton(btn);
            }
        };

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new DatePickerDialog(SetParameterActivity.this, dpDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateButton(Button btn) {
        String myFormat = "dd/MMM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        System.out.println(sdf.format(myCalendar.getTime()));
        btn.setText(sdf.format(myCalendar.getTime()));

        switch (btn.getId()){
            case R.id.btnDate: date = formatWithTime.format(myCalendar.getTime());
                fromDateBtn.setText("SELECT FROM DATE");
                toDateBtn.setText("SELECT TO DATE");
                fromDate = null;
                toDate = null;
                break;
            case R.id.btnDateFrom: fromDate = formatWithTime.format(myCalendar.getTime());
                dateBtn.setText("SELECT A DATE");
                date = null;
                break;
            case R.id.btnDateTo: toDate = formatWithTime.format(myCalendar.getTime());
                dateBtn.setText("SELECT A DATE");
                date = null;
                break;
        }
    }

    public void handleOutgoingChecked(){
        swtOutgoing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    type = "outgoing";
                    swtIncoming.setChecked(false);
                    swtMissed.setChecked(false);
                }
            }
        });
    }

    public void handleIncomingChecked(){
        swtIncoming.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b){
                type = "incoming";
                swtOutgoing.setChecked(false);
                swtMissed.setChecked(false);
            }
            }
        });
    }

    public void handleMissedChecked(){
        swtMissed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b){
                type = "missed";
                swtIncoming.setChecked(false);
                swtOutgoing.setChecked(false);
            }
            }
        });
    }

    public void clearData(View v){
        swtOutgoing.setChecked(false);
        swtIncoming.setChecked(false);
        swtMissed.setChecked(false);

        dateBtn.setText("SELECT A DATE");
        fromDateBtn.setText("SELECT FROM DATE");
        toDateBtn.setText("SELECT TO DATE");

        editNumber.setText(null);

        type = null;
        date = null;
        fromDate = null;
        toDate = null;
        number = null;
    }

    public void moveToViewGraphActivity(View v){
        String condition = null;
        number = editNumber.getText().toString();
        try {

            if (type == null || (!swtIncoming.isChecked() && !swtOutgoing.isChecked() && !swtMissed.isChecked())) {
                Toast.makeText(this, "Please select a call type !!!", Toast.LENGTH_SHORT).show();
            } else if (number != null && number.length() != 0 && number.length() != 9) {
                Toast.makeText(this, "Invalid phone number !!!", Toast.LENGTH_SHORT).show();
            } else if (fromDate != null && toDate == null){
                Toast.makeText(this, "Please select to date !!!", Toast.LENGTH_SHORT).show();
            } else if (toDate != null && fromDate == null){
                Toast.makeText(this, "Please select from date !!!", Toast.LENGTH_SHORT).show();
            } else if ((toDate != null && fromDate != null) && (formatWithTime.parse(fromDate).after(formatWithTime.parse(toDate)))){
                Toast.makeText(this, "Invalid date range !!!", Toast.LENGTH_SHORT).show();
            } else {

                if(type != null){
                    condition = "type = '" + type + "' ";
                }

                if(date != null){
                    condition = condition + " AND DATE(date) = '" + formatWithOutTime.format(formatWithTime.parse(date)) + "'";
                }

                if(fromDate != null && toDate != null){
                    condition = condition + " AND DATE(date) >= '" + formatWithOutTime.format(formatWithTime.parse(fromDate)) + "' AND DATE(date) <= '" + formatWithOutTime.format(formatWithTime.parse(toDate)) + "'" ;
                }

                if(number != null && number.length() != 0) {
                    number = "0" + number;
                    condition = condition + " AND number = '" + number + "'";
                }

                DbHelper dbHelper = new DbHelper(getApplicationContext());
                ArrayList<GraphDataDTO> graphDataDTOS = (ArrayList<GraphDataDTO>) dbHelper.selectGraphData("Date(date), count(*)", condition + " group by Date(date)");
                ArrayList<ExportDataDTO> exportDataDTOS = (ArrayList<ExportDataDTO>)dbHelper.selectExportData("call_id,number,name,date,type,duration", condition + " order by call_id asc");
                if(graphDataDTOS.size() > 0) {
                    Intent intent = new Intent(SetParameterActivity.this, BarChartActivity.class);
                    intent.putExtra("number", number);
                    intent.putExtra("type", type);
                    intent.putExtra("date", date != null ? formatWithOutTime.format(formatWithTime.parse(date)) : null);
                    intent.putExtra("from_date", fromDate != null ? formatWithOutTime.format(formatWithTime.parse(fromDate)) : null);
                    intent.putExtra("to_date", toDate != null ? formatWithOutTime.format(formatWithTime.parse(toDate)) : null);
                    intent.putExtra("graph_data", graphDataDTOS);
                    intent.putExtra("export_data", exportDataDTOS);
                    startActivity(intent);
                } else{
                    Toast.makeText(this, "No data found !!!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }


}