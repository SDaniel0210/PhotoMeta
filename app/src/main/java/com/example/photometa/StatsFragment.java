package com.example.photometa;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.photometa.data.local.AppDatabase;
import com.example.photometa.data.local.dao.PhotoDAO;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsFragment extends Fragment {

    private TextView totalImageValue;
    private TextView gpsValue;
    private TextView aiVerifiedValue;
    private BarChart topCameraChart;
    private BarChart aiRatioChart;
    private BarChart locationInsightsChart;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        totalImageValue = view.findViewById(R.id.totalImageValue_lb);
        gpsValue = view.findViewById(R.id.gpsValue_lb);
        aiVerifiedValue = view.findViewById(R.id.aiVerifiedValue_lb);
        topCameraChart = view.findViewById(R.id.topCamera_chart);
        aiRatioChart = view.findViewById(R.id.aiRatio_chart);
        locationInsightsChart = view.findViewById(R.id.locationInsights_chart);


        loadStats();

        return view;
    }

    private void loadStats() {
        AppDatabase db = AppDatabase.getInstance(getContext());
        PhotoDAO photoDao = db.photoDao();

        new Thread(() -> {
            int totalCount = photoDao.getTotalCount();
            int gpsCount = photoDao.getGpsCount();
            int aiVerifiedCount = photoDao.getAiVerifiedCount();
            List<String> topCameras = photoDao.getTopCameras();

            Map<String, Integer> counts = new HashMap<>();
            for (String name : topCameras) {
                if (name == null) name = "UNKNOWN";
                counts.put(name, counts.getOrDefault(name, 0) + 1);
            }

            float[] values = new float[counts.size()];
            String[] labels = new String[counts.size()];
            int i = 0;
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                labels[i] = entry.getKey();
                values[i] = entry.getValue();
                i++;
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    totalImageValue.setText(String.valueOf(totalCount));
                    gpsValue.setText(String.valueOf(gpsCount));
                    aiVerifiedValue.setText(String.valueOf(aiVerifiedCount));

                    if (totalCount > 0) {
                        updateBarChart(locationInsightsChart,
                                new float[]{gpsCount, totalCount - gpsCount},
                                new String[]{"GPS", "No GPS"},
                                new int[]{Color.rgb(76, 175, 80), Color.rgb(244, 67, 54)});

                        updateBarChart(aiRatioChart,
                                new float[]{aiVerifiedCount, totalCount - aiVerifiedCount},
                                new String[]{"AI", "Not AI"},
                                new int[]{Color.rgb(33, 150, 243), Color.rgb(189, 189, 189)});

                        updateBarChart(topCameraChart,
                                values,
                                labels,
                                new int[]{Color.rgb(255, 152, 0)});
                    }
                });
            }
        }).start();
    }

    private void updateBarChart(BarChart chart, float[] values, String[] labels, int[] colors) {
        if (chart == null) return;

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            entries.add(new BarEntry(i, values[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.rgb(150, 150, 150));

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        chart.setData(barData);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.length);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setTextColor(Color.rgb(150, 150, 150));
        xAxis.setTextSize(11f);
        xAxis.setAxisLineColor(Color.rgb(150, 150, 150));

        chart.getAxisLeft().setTextColor(Color.rgb(150, 150, 150));
        chart.getAxisLeft().setAxisLineColor(Color.rgb(150, 150, 150));
        chart.getAxisLeft().setGridColor(Color.parseColor("#444444"));
        chart.getAxisLeft().setAxisMinimum(0f);

        chart.setFitBars(true);
        chart.animateY(1000);
        chart.invalidate();
    }
}
