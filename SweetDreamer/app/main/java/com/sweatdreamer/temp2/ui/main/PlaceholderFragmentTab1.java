package com.sweatdreamer.temp2.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.sweatdreamer.temp2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragmentTab1 extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    public static PlaceholderFragmentTab1 newInstance(int index) {
        PlaceholderFragmentTab1 fragment = new PlaceholderFragmentTab1();
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
        View root = inflater.inflate(R.layout.fragment_heartbeat_tab_1, container, false);

        BarChart heartBar = root.findViewById(R.id.heartBar);
        heartBar.getXAxis().setDrawGridLines(false);
        heartBar.getXAxis().setDrawAxisLine(false);
        heartBar.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        heartBar.getAxisLeft().setDrawGridLines(false);
        heartBar.getAxisLeft().setDrawAxisLine(false);
        heartBar.getAxisLeft().setDrawLabels(false);
        heartBar.getAxisRight().setDrawGridLines(false);
        heartBar.getAxisRight().setDrawAxisLine(false);
        heartBar.getAxisRight().setDrawLabels(false);
        heartBar.getDescription().setEnabled(false);
        heartBar.getLegend().setEnabled(false);

        List<BarEntry> entries = new ArrayList<BarEntry>();

        int maxBarAmount = 24;
        for (float i = 0; i < maxBarAmount; i++){
            entries.add(new BarEntry(i,0));
        }
        BarDataSet dataSet = new BarDataSet(entries, "Label"); // add entries to dataset

        BarData barData = new BarData(dataSet);
        heartBar.setData(barData);
        heartBar.invalidate(); // refresh

        return root;
    }
}