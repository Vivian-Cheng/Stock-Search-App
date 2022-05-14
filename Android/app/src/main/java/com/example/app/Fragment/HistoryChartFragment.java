package com.example.app.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.app.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HistoryChartFragment extends Fragment {

    public static final String ARG_OBJECT = "object";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("oncreate history chart");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View root = inflater.inflate(R.layout.fragment_history_chart, container, false);
        System.out.println("history chart");

        return inflater.inflate(R.layout.fragment_history_chart, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle args = getArguments();
        String ticker = args.getString(ARG_OBJECT);
        System.out.println("onViewCreated: " + ticker);
        WebView webView = view.findViewById(R.id.history_chart_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/history_chart.html");
        //webView.loadUrl("javascript:setChart('"+ticker+"')");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                webView.loadUrl("javascript:loadData('"+ticker+"')");
            }
        });
    }

}