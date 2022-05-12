package calllog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.test.calllog.R;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BarChartActivity extends AppCompatActivity {

    ArrayList<ExportDataDTO> exportDataDTOS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_bar_chart);
        try {
            BarChart chart = findViewById(R.id.barchart);
            TextView selectedNumber = (TextView) findViewById(R.id.txtSelectedNumber);
            TextView selectedType = (TextView) findViewById(R.id.txtSelectedType);
            TextView selectedDate = (TextView) findViewById(R.id.txtSelectedDate);

            Intent intent = getIntent();
            exportDataDTOS = (ArrayList<ExportDataDTO>) intent.getSerializableExtra("export_data");
            ArrayList<GraphDataDTO> graphDataDTOS = (ArrayList<GraphDataDTO>) intent.getSerializableExtra("graph_data");
            String number = intent.getStringExtra("number");
            String type = intent.getStringExtra("type");
            String date = intent.getStringExtra("date");
            String fromDate = intent.getStringExtra("from_date");
            String toDate = intent.getStringExtra("to_date");

            selectedType.setText("Call type : " + type);
            if (date != null) {
                selectedDate.setText("Date : " + date);
            } else if (fromDate != null) {
                selectedDate.setText("Date : " + fromDate + " to " + toDate);
            } else {
                selectedDate.setVisibility(View.INVISIBLE);
            }

            if (number != null && number.length() > 0) {
                DbHelper dbHelper = new DbHelper(getApplicationContext());
                Object nameObj = dbHelper.selectOne("DISTINCT(name)", "number = '" + number + "'");
                String name = nameObj == null ? "Unknown" : nameObj.toString();
                selectedNumber.setText("Number : " + number + " (" + name + ")");
            } else {
                selectedNumber.setVisibility(View.INVISIBLE);
            }

            ArrayList graphData = new ArrayList();
            ArrayList year = new ArrayList();

            for (int i = 0; i < graphDataDTOS.size(); i++) {
                graphData.add(new BarEntry(graphDataDTOS.get(i).getCount(), i));
                year.add(graphDataDTOS.get(i).getDate());
            }

            BarDataSet bardataset = new BarDataSet(graphData, "Call Counts");
            chart.animateY(5000);
            BarData data = new BarData(year, bardataset);
            bardataset.setColors(ColorTemplate.JOYFUL_COLORS);
            chart.setData(data);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void aa(View v){

        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Toast.makeText(this, "Invalid  !!!", Toast.LENGTH_SHORT).show();
        }
        else {
            //We use the Download directory for saving our .csv file.
            File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists())
            {
                exportDir.mkdirs();
            }

            File file;
            PrintWriter printWriter = null;
            try
            {
                String date = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
                file = new File(exportDir, date +".csv");
                file.createNewFile();
                printWriter = new PrintWriter(new FileWriter(file));
                printWriter.println("Call Id, Name, Number, Date\n");
                for (ExportDataDTO ex : exportDataDTOS)
                {
                    String callId = String.valueOf(ex.getCallId());
                    String name = ex.getName();
                    String number = ex.getNumber();
                    String callDate = ex.getCallDate();

                    String record = callId + ", " + name + ", " + number + ", " + callDate;
                    printWriter.println(record);
                }
                Toast.makeText(this, "Success !!!", Toast.LENGTH_SHORT).show();
            }

            catch(Exception ex) {
                ex.printStackTrace();
                Toast.makeText(this, "Technical failure !!!", Toast.LENGTH_SHORT).show();
            }
            finally {
                if(printWriter != null) printWriter.close();
            }
        }
    }

}
