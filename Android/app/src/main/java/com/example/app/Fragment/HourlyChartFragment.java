package com.example.app.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.app.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class HourlyChartFragment extends Fragment {

    public static final String ARG_OBJECT = "object";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("hourly chart");
        return inflater.inflate(R.layout.fragment_hourly_chart, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle args = getArguments();
        String ticker = args.getString(ARG_OBJECT);
        WebView webView = view.findViewById(R.id.hourly_chart_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/hourly_chart.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                webView.loadUrl("javascript:loadData('"+ticker+"')");
            }
        });
    }
}