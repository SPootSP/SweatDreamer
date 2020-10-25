package com.sweatdreamer.temp2.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sweatdreamer.temp2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragmentTab2 extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private int heartOffset = 0;
    private int[] heartBeat = {0};

    public static PlaceholderFragmentTab2 newInstance(int index) {
        PlaceholderFragmentTab2 fragment = new PlaceholderFragmentTab2();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_heartbeat_tab_2, container, false);

        LineChart heartLine = root.findViewById(R.id.heartLine);
        heartLine.getXAxis().setDrawGridLines(false);
        heartLine.getXAxis().setDrawAxisLine(false);
        heartLine.getXAxis().setDrawLabels(false);
        heartLine.getAxisLeft().setDrawGridLines(false);
        heartLine.getAxisLeft().setDrawAxisLine(false);
        heartLine.getAxisLeft().setDrawLabels(false);
        heartLine.getAxisRight().setDrawGridLines(false);
        heartLine.getAxisRight().setDrawAxisLine(false);
        heartLine.getAxisRight().setDrawLabels(false);
        heartLine.getDescription().setEnabled(false);
        heartLine.getLegend().setEnabled(false);

        List<Entry> entries = new ArrayList<Entry>();

        int maxBarAmount = 100;
        for (float i = 0; i < maxBarAmount; i++){
            float y = heartBeat[(int) ((heartOffset+i)%heartBeat.length)];
            entries.add(new Entry(i,y));
        }
        heartOffset += 1;
        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset

        LineData lineData = new LineData(dataSet);
        heartLine.setData(lineData);
        heartLine.invalidate(); // refresh

        return root;
    }
}