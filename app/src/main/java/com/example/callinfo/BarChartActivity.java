package com.example.callinfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class BarChartActivity extends AppCompatActivity {

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
}
