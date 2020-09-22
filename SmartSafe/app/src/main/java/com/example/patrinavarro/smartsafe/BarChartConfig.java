package com.example.patrinavarro.smartsafe;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM;
import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM_INSIDE;
import static com.github.mikephil.charting.components.XAxis.XAxisPosition.TOP;

/**
 * Created by Patri Navarro on 21/05/2018.
 */

public class BarChartConfig {
    Context context;
    HorizontalBarChart barChart;
    ArrayList<BarEntry> yValues;
    ArrayList<CategoryBudget> categories;
    BarDataSet set;
    int[] barColorArray;
    BarData data;


    public BarChartConfig(Context context, HorizontalBarChart barChart, ArrayList<CategoryBudget> categories) {
        //Set
        this.context=context;
        this.barChart = barChart;
        this.categories=categories;
        yValues = new ArrayList<>();

        Log.d("HELP","Initializing BarChart configuration...");
        createEntries(categories);

        //The chart is generated and the data from yValues is set
        default_settings();
    }

    private void createEntries(ArrayList<CategoryBudget> categories) {
        yValues.clear();
        barColorArray= new int[categories.size()];
        int i=0;
        for(CategoryBudget cat:categories)
        {
            yValues.add(new BarEntry(i,cat.getPercentage()));
            determineColor(i,cat.getPercentage());
            Log.d("HELP","BarEntry "+cat.getPercentage() +" for category "+cat.getCategoryName());
            i++;
        }
        Log.d("HELP","Creating new BarDataSet...");
        set = new BarDataSet(yValues,"%");
        Log.d("HELP","Setting data into barchart...");
        data = new BarData(set);
        barChart.setData(data);
    }

    private void determineColor(int i, float percentage) {
        //CHOOSE THE COLOUR OF EACH VALUE DEPENDING ON THE RANGE THEY ARE IN
        if(percentage<50)barColorArray[i] = context.getResources().getColor(R.color.colorGreen);
        else if((percentage>=50)&&(percentage<70)) barColorArray[i] = context.getResources().getColor(R.color.colorYellow);
        else if((percentage>=70)&&(percentage<85)) barColorArray[i] = context.getResources().getColor(R.color.colorOrange);
        else barColorArray[i] = context.getResources().getColor(R.color.colorRed);
    }

    public void default_settings()
    {
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {return categories.get((int) value).getCategoryName();}
        };

        barChart.setFitBars(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setLabelCount(categories.size());
        xAxis.setValueFormatter(formatter);

        //HIDE EXCESS OF INFORMATION
        barChart.setDescription(null);    // Hide the description
        barChart.getAxisLeft().setDrawLabels(false);
        barChart.getAxisRight().setDrawLabels(false);;
        barChart.setDrawGridBackground(false);
        barChart.getLegend().setEnabled(false);

        xAxis.setPosition(BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(16);
        barChart.getRendererLeftYAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.disableGridDashedLine();

        barChart.getAxisRight().disableGridDashedLine();
        barChart.getAxisRight().setDrawTopYLabelEntry(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setDrawAxisLine(false);

        barChart.getAxisLeft().disableGridDashedLine();
        barChart.getAxisLeft().setDrawTopYLabelEntry(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawAxisLine(false);

        barChart.getAxisLeft().setAxisMaximum(100);
        barChart.getAxisLeft().setAxisMinimum(0);
        // Bars are sliding in from left to right
        barChart.animateXY(1000, 1000);
        // Display scores inside the bars
        barChart.setDrawValueAboveBar(false);

        set.setValueTextSize(14);
        set.setValueFormatter(new PercentFormatter());
        set.setColors(barColorArray);

        data.setBarWidth(0.8f);
    }


    public HorizontalBarChart getBarChart() {
        return barChart;
    }
}
