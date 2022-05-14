package com.example.app.Fragment;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.app.R;

public class ChartAdapter extends FragmentStateAdapter {
    private static int NUM_TAB = 2;
    private String ticker;

    public ChartAdapter(FragmentManager fragmentManager, Lifecycle lifecycle, String ticker) {
        super(fragmentManager, lifecycle);
        this.ticker = ticker;
    }

    @Override
    public int getItemCount() {
        return NUM_TAB;
    }

    @Override
    public Fragment createFragment(int pos) {
        Bundle args = new Bundle();
        switch (pos) {
            case 0:
                HourlyChartFragment hourlyChartFragment = new HourlyChartFragment();
                args.putString(HourlyChartFragment.ARG_OBJECT, ticker);
                hourlyChartFragment.setArguments(args);
                return hourlyChartFragment;
            case 1:
                HistoryChartFragment historyChartFragment = new HistoryChartFragment();
                args.putString(HistoryChartFragment.ARG_OBJECT, ticker);
                historyChartFragment.setArguments(args);
                return historyChartFragment;
            default:
                return null;
        }
    }

}
