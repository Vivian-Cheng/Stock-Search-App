package com.example.app;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.example.app.API.APIController;
import com.example.app.Adapter.RecyclerNewAdapter;
import com.example.app.Adapter.RecyclerPeerAdapter;
import com.example.app.Fragment.ChartAdapter;
import com.example.app.databinding.ActivityInfoBinding;
import com.example.app.databinding.InfoAboutBinding;
import com.example.app.databinding.InfoDescriptionBinding;
import com.example.app.databinding.InfoEarnBinding;
import com.example.app.databinding.InfoHistoricalBinding;
import com.example.app.databinding.InfoInsightsBinding;
import com.example.app.databinding.InfoNewsBinding;
import com.example.app.databinding.InfoPortfolioBinding;
import com.example.app.databinding.InfoRecommendBinding;
import com.example.app.databinding.InfoStatsBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {
    String ticker;
    String companyName = "";
    Double currPrice = 0.0;
    private static final int NEWS_LIMIT = 20;
    private int numOfRequest = 5;
    private ActivityInfoBinding activityInfoBinding;
    private InfoDescriptionBinding infoDescriptionBinding;
    private InfoPortfolioBinding infoPortfolioBinding;
    private InfoAboutBinding infoAboutBinding;
    private InfoHistoricalBinding infoHistoricalBinding;
    private InfoStatsBinding infoStatsBinding;
    private InfoInsightsBinding infoInsightsBinding;
    private InfoRecommendBinding infoRecommendBinding;
    private InfoEarnBinding infoEarnBinding;
    private InfoNewsBinding infoNewsBinding;
    private Dialog dialog;
    private Dialog messageDialog;
    private APIController apiController;
    private View view;
    private int[] chartIcon = {R.drawable.ic_chart_line, R.drawable.ic_clock_time_three};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up view binding
        activityInfoBinding = ActivityInfoBinding.inflate(getLayoutInflater());
        infoDescriptionBinding = activityInfoBinding.description;
        infoPortfolioBinding = activityInfoBinding.portfolio;
        infoAboutBinding = activityInfoBinding.about;
        infoHistoricalBinding = activityInfoBinding.chartTab;
        infoStatsBinding = activityInfoBinding.stats;
        infoInsightsBinding = activityInfoBinding.insights;
        infoRecommendBinding = activityInfoBinding.recommend;
        infoEarnBinding = activityInfoBinding.earn;
        infoNewsBinding = activityInfoBinding.news;

        apiController = new APIController(this);

        ProgressBar progressBar = activityInfoBinding.spinner;

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.trade_dialog);
        messageDialog = new Dialog(this);
        messageDialog.setContentView(R.layout.trade_finish_dialog);
        messageDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        view = activityInfoBinding.getRoot();
        setContentView(view);

        // Get the Intent that started this activity and extract the message
        Intent intent = getIntent();
        ticker = intent.getStringExtra(MainActivity.EXTRA_TICKER);
        System.out.println(ticker);

        // Update action bar title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(ticker);

        // test
        apiController.getRequestQueue().addRequestEventListener((request, event) -> {
            if (event == RequestQueue.RequestEvent.REQUEST_FINISHED) {
                if (numOfRequest == 0) {
                    progressBar.setVisibility(View.GONE);
                    //alertHandler("requests finished");
                }
            }
        });
        portfoHandler();
        descriptHandler();
        peersHandler();
        quoteHandler();
        socialHandler();
        newsHandler();

        // Set up fragment viewpage
        historyChartHandler();
        recommendHandler();
        earnHandler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu, menu);
        System.out.println("onCreateOptionsMenu: " + LocalStorage.getInstance(this).inFavorite(ticker));
        if (LocalStorage.getInstance(this).inFavorite(ticker)) {
            menu.getItem(0).setIcon(getDrawable(R.drawable.ic_star));
        } else {
            menu.getItem(0).setIcon(getDrawable(R.drawable.ic_star_outline));
        }
        //System.out.println("onmenu: " + ticker);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                LocalStorage.getInstance(this).toFavorite(ticker);
                if (LocalStorage.getInstance(this).inFavorite(ticker)) {
                    item.setIcon(getDrawable(R.drawable.ic_star));
                    alertHandler(ticker + " " + getString(R.string.is_added_to_favorites));
                } else {
                    item.setIcon(getDrawable(R.drawable.ic_star_outline));
                    alertHandler(ticker + " " + getString(R.string.is_removed_from_favorites));
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void portfoHandler() {
        Button tradeBtn = infoPortfolioBinding.tradeBtn;
        Button buyBtn = dialog.findViewById(R.id.buy_btn);
        Button sellBtn = dialog.findViewById(R.id.sell_btn);
        Button doneBtn = messageDialog.findViewById(R.id.done_btn);
        EditText input = dialog.findViewById(R.id.trade_input);
        TextView amount = dialog.findViewById(R.id.shares_input);
        TextView total = dialog.findViewById(R.id.shares_total);
        TextView price = dialog.findViewById(R.id.price);
        TextView wallet = dialog.findViewById(R.id.wallet);
        TextView successMsg = messageDialog.findViewById(R.id.trade_message_action);

        wallet.setText(DataFormatter.toPriceFormat(LocalStorage.getInstance(this).getBalance()));

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String inputValue = input.getText().toString();
                if (!DataFormatter.isInteger(inputValue) || Integer.valueOf(inputValue) < 0) {
                    inputValue = "0";
                }
                amount.setText(inputValue);
                Double totalValue = Integer.valueOf(inputValue)*Double.valueOf(price.getText().toString().substring(1));
                total.setText(DataFormatter.toDecimal(totalValue));
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        tradeBtn.setOnClickListener(dialogView -> {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        buyBtn.setOnClickListener(buyView -> {
            String inputValue = input.getText().toString();
            if (!DataFormatter.isInteger(inputValue)) {
                alertHandler(getString(R.string.please_enter_a_valid_amount));
            } else if (Integer.valueOf(inputValue) <= 0) {
                alertHandler(getString(R.string.cannot_buy_non_positive_shares));
            } else if (LocalStorage.getInstance(this).getBalance() - Double.valueOf(total.getText().toString()) < 0) {
                alertHandler(getString(R.string.not_enough_money_to_buy));
            } else {
                dialog.cancel();
                messageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                messageDialog.show();
                LocalStorage.getInstance(this).toPortfo(ticker, Integer.valueOf(inputValue), Double.valueOf(total.getText().toString()));
                LocalStorage.getInstance(this).toBalance(-Double.valueOf(total.getText().toString()));
                successMsg.setText(getString(R.string.buy_message) + " " + inputValue);
                // update
                updatePortfo();
                wallet.setText(DataFormatter.toPriceFormat(LocalStorage.getInstance(this).getBalance()));
            }
        });
        sellBtn.setOnClickListener(sellView -> {
            String inputValue = input.getText().toString();
            if (!DataFormatter.isInteger(inputValue)) {
                alertHandler(getString(R.string.please_enter_a_valid_amount));
            } else if (Integer.valueOf(inputValue) <= 0) {
                alertHandler(getString(R.string.cannot_sell_non_positive_shares));
            } else if (LocalStorage.getInstance(this).getPortfoOwned(ticker) - Integer.valueOf(inputValue) < 0) {
                alertHandler(getString(R.string.not_enough_shares_to_sell));
            } else {
                dialog.cancel();
                messageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                messageDialog.show();
                LocalStorage.getInstance(this).toPortfo(ticker, -Integer.valueOf(inputValue), -Double.valueOf(total.getText().toString()));
                LocalStorage.getInstance(this).toBalance(Double.valueOf(total.getText().toString()));
                successMsg.setText(getString(R.string.sell_message) + " " + inputValue);
                // update
                updatePortfo();
                wallet.setText(DataFormatter.toPriceFormat(LocalStorage.getInstance(this).getBalance()));
            }
        });
        doneBtn.setOnClickListener(doneView -> {
            messageDialog.cancel();
        });
    }

    private void updatePortfo() {
        int sharesOwned = LocalStorage.getInstance(this).getPortfoOwned(ticker);
        Double totalCost = LocalStorage.getInstance(this).getPortfoCost(ticker);
        Double value = sharesOwned * currPrice;
        infoPortfolioBinding.sharesOwned.setText(String.valueOf(sharesOwned));
        infoPortfolioBinding.avgCost.setText(DataFormatter.toPriceFormat(LocalStorage.getInstance(this).getPortfoAvg(ticker)));
        infoPortfolioBinding.totalCost.setText(DataFormatter.toPriceFormat(totalCost));
        infoPortfolioBinding.change.setText(DataFormatter.toPriceFormat(value - totalCost));
        Double change = Double.valueOf(DataFormatter.toDecimal(value - totalCost));
        infoPortfolioBinding.change.setTextColor(getResources().getColor(change == 0 ? R.color.black : (change > 0 ? R.color.lightgreen : R.color.lightred)));
        infoPortfolioBinding.marketValue.setText(DataFormatter.toPriceFormat(value));
        infoPortfolioBinding.marketValue.setTextColor(getResources().getColor(change == 0 ? R.color.black : (change > 0 ? R.color.lightgreen : R.color.lightred)));
    }

    private void alertHandler(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void descriptHandler() {
        apiController.getDescript(ticker, data -> {
            numOfRequest --;
            try {
                infoDescriptionBinding.symbol.setText(data.getString("ticker"));
                infoDescriptionBinding.companyName.setText(data.getString("name"));
                companyName = data.getString("name");
                Picasso.get().load(data.getString("logo")).into(infoDescriptionBinding.logo);
                infoAboutBinding.ipoStartDate.setText(DataFormatter.toDateFormat(data.getString("ipo")));
                infoAboutBinding.industry.setText(data.getString("finnhubIndustry"));
                infoAboutBinding.webpage.setText(data.getString("weburl"));
                infoInsightsBinding.socialCompany.setText(data.getString("name"));
                TextView dialogCompany = dialog.findViewById(R.id.trade_company);
                TextView dialogTicker = dialog.findViewById(R.id.trade_ticker);
                TextView messageCompany = messageDialog.findViewById(R.id.trade_message_company);
                dialogCompany.setText(companyName);
                dialogTicker.setText(ticker);
                messageCompany.setText("shares of " + companyName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void peersHandler() {
        apiController.getPeers(ticker, data -> {
            numOfRequest --;
            RecyclerView recyclerView = (RecyclerView)infoAboutBinding.companyPeersList;
            List<String> peerSource = new ArrayList<>();
            //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            //recyclerView.setLayoutManager(layoutManager);
            try {
                for (int i = 0; i < data.length(); i++) {
                    if (!data.getString(i).contains("."))
                        peerSource.add(data.getString(i)+",");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RecyclerPeerAdapter recyclerPeerAdapter = new RecyclerPeerAdapter(peerSource, this);
            LinearLayoutManager horizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayout);
            recyclerView.setAdapter(recyclerPeerAdapter);
        });
    }

    private void quoteHandler() {
        apiController.getQuote(ticker, data -> {
            numOfRequest --;
            try {
                infoDescriptionBinding.currPrice.setText(DataFormatter.toPriceFormat(data.getDouble("c")));
                currPrice = Double.valueOf(DataFormatter.toDecimal(data.getDouble("c")));
                Double change = data.getDouble("d");
                infoDescriptionBinding.currChange.setText(DataFormatter.toPriceFormat(change));
                infoDescriptionBinding.currChange.setTextColor(change == 0.0 ? getResources().getColor(R.color.black) : (change > 0 ? getResources().getColor(R.color.lightgreen) : getResources().getColor(R.color.lightred)));
                infoDescriptionBinding.currChangePercent.setText(DataFormatter.toPercentFormat(data.getDouble("dp")));
                infoDescriptionBinding.currChangePercent.setTextColor(change == 0.0 ? getResources().getColor(R.color.black) : (change > 0 ? getResources().getColor(R.color.lightgreen) : getResources().getColor(R.color.lightred)));
                if (change > 0)
                    infoDescriptionBinding.trending.setImageDrawable(getDrawable(R.drawable.ic_trending_up));
                else
                    infoDescriptionBinding.trending.setImageDrawable(getDrawable(R.drawable.ic_trending_down));
                updatePortfo();
                infoStatsBinding.openPrice.setText(DataFormatter.toPriceFormat(data.getDouble("o")));
                infoStatsBinding.highPrice.setText(DataFormatter.toPriceFormat(data.getDouble("h")));
                infoStatsBinding.lowPrice.setText(DataFormatter.toPriceFormat(data.getDouble("l")));
                infoStatsBinding.prevPrice.setText(DataFormatter.toPriceFormat(data.getDouble("pc")));
                TextView tradePrice = dialog.findViewById(R.id.price);
                tradePrice.setText(DataFormatter.toPriceFormat(data.getDouble("c")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void historyChartHandler() {
        ViewPager2 viewPager = infoHistoricalBinding.viewPager;
        TabLayout tabLayout = infoHistoricalBinding.tabLayout;
        ChartAdapter chartAdapter = new ChartAdapter(getSupportFragmentManager(), getLifecycle(), ticker);
        viewPager.setAdapter(chartAdapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setIcon(getDrawable(chartIcon[position]))).attach();
        /*
        LocalDateTime today =  LocalDateTime.now();
        LocalDateTime before = today.minusYears(2);
        String from = String.valueOf(today.toEpochSecond(ZoneOffset.UTC));
        String to = String.valueOf(before.toEpochSecond(ZoneOffset.UTC));
        apiController.getCandle(ticker, "D", from, to, data -> {
            try {
                if (data.getString("s").equals("ok")) {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
         */
    }

    private void socialHandler() {
        apiController.getSocial(ticker, data -> {
            numOfRequest --;
            try {
                JSONArray reddit = data.getJSONArray("reddit");
                JSONArray twitter = data.getJSONArray("twitter");
                int[] redditRes = new int[3];
                int[] twitterRes = new int[3];
                DataFormatter.toTotal(redditRes, reddit);
                DataFormatter.toTotal(twitterRes, twitter);
                infoInsightsBinding.redditTotal.setText(Integer.toString(redditRes[0]));
                infoInsightsBinding.redditPos.setText(Integer.toString(redditRes[1]));
                infoInsightsBinding.redditNeg.setText(Integer.toString(redditRes[2]));
                infoInsightsBinding.twitterTotal.setText(Integer.toString(twitterRes[0]));
                infoInsightsBinding.twitterPos.setText(Integer.toString(twitterRes[1]));
                infoInsightsBinding.twitterNeg.setText(Integer.toString(twitterRes[2]));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void recommendHandler() {
        WebView webView = infoRecommendBinding.recommendView;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/recommend_chart.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                webView.loadUrl("javascript:loadData('"+ticker+"')");
            }
        });
    }

    private void earnHandler() {
        WebView webView = infoEarnBinding.earnView;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/earn_chart.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                webView.loadUrl("javascript:loadData('"+ticker+"')");
            }
        });
    }

    private void newsHandler() {
        RecyclerView recyclerView = (RecyclerView)infoNewsBinding.newsItemList;
        List<JSONObject> newSource = new ArrayList<>();
        apiController.getNews(ticker, data -> {
            numOfRequest --;
            try {
                for (int i = 0; i < data.length(); i++) {
                    if (!data.getJSONObject(i).getString("source").equals("")
                            && !data.getJSONObject(i).getString("headline").equals("")
                            && !data.getJSONObject(i).getString("image").equals("")) {
                        System.out.println(data.getJSONObject(i).getString("image"));
                        newSource.add(data.getJSONObject(i));
                    }
                    if (newSource.size() == NEWS_LIMIT)
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RecyclerNewAdapter recyclerNewAdapter = new RecyclerNewAdapter(newSource, this);
            LinearLayoutManager verticalLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(verticalLayout);
            recyclerView.setAdapter(recyclerNewAdapter);

        });
    }
}