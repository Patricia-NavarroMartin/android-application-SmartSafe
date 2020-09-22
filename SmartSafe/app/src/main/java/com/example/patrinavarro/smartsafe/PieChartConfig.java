package com.example.patrinavarro.smartsafe;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Patri Navarro on 03/05/2018.
 */

public class PieChartConfig {

    PieChart pieChart;
    ArrayList<PieEntry> yValues;
    ArrayList<String> categories;
    ArrayList<Float> amounts;
    float total;


    public PieChartConfig(PieChart pieChart, ArrayList<String> categories, ArrayList<Float> amounts, float total) {
        //Set
        this.pieChart = pieChart;
        this.categories=categories;
        this.amounts = amounts;
        this.total = total;
        yValues = new ArrayList<>();

        Log.d("HELP","yValues created");
        createEntries(categories,amounts,total);

        //The pie chart is generated and the data from yValues is set
        default_settings();

    }

    private void createEntries(ArrayList<String> categories, ArrayList<Float> amounts,float total) {
        int totalData = categories.size();
        yValues.clear();
        for(int i=0; i<totalData;i++)
        {
            yValues.add(new PieEntry(amounts.get(i), categories.get(i)));
            Log.d("HELP","PieEntry "+amounts.get(i)+" as "+categories.get(i));
        }
    }


    public void default_settings()
    {
        //General (changes when it is clicked)
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
//        pieChart.setCenterText(generateCenterSpannableText());
        Typeface tf = Typeface.defaultFromStyle(R.style.AppTheme);
        pieChart.setCenterTextTypeface(tf);
        pieChart.setEntryLabelTypeface(tf);
        //Inside text
        pieChart.setCenterText(("BALANCE\n"+total+"€"));
        //Hole settings
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(40f);
        pieChart.setTransparentCircleColor(61);

        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setDrawEntryLabels(false);

        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(0f);
        dataSet.setColors(ColorTemplate.createColors(ColorTemplate.JOYFUL_COLORS));


        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(15);
        pieData.setValueTextColor(R.color.colorPrimaryDark);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTypeface(tf);

        //Edit legend
        Legend legend = pieChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        pieChart.setData(pieData);
        pieChart.setNoDataText("No chart");
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.65f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    public PieChart getPieChart() {
        return pieChart;
    }
}
