package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.example.app.API.APIController;
import com.example.app.Adapter.RecyclerFavoriteAdapter;
import com.example.app.Adapter.RecyclerPortfoAdapter;
import com.example.app.Adapter.SuggestAdapter;
import com.example.app.Section.Stock;
import com.example.app.Section.StockMoveCallback;
import com.example.app.databinding.ActivityMainBinding;
import com.example.app.databinding.SectionFavoriteBinding;
import com.example.app.databinding.SectionPortfolioBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private static final int AUTO_REFRESH_DELAY = 15000;
    public static final String EXTRA_TICKER = "com.example.app.MESSAGE";
    public DataFormatter dataFormatter = new DataFormatter();
    private APIController apiController;
    private Handler handler;
    private SuggestAdapter suggestAdapter;
    private RecyclerPortfoAdapter sectionAdapterPotfo;
    private RecyclerFavoriteAdapter sectionAdapterFavorite;
    private View view;
    ProgressBar progressBar;
    ActivityMainBinding activityMainBinding;
    SectionPortfolioBinding sectionPortfolioBinding;
    SectionFavoriteBinding sectionFavoriteBinding;
    Handler refreshHandler = new Handler();
    Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_App);
        super.onCreate(savedInstanceState);

        // setup viewbinding
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        sectionPortfolioBinding = activityMainBinding.sectionPortfolio;
        sectionFavoriteBinding = activityMainBinding.sectionFavarite;
        progressBar = activityMainBinding.spinner;
        progressBar.setVisibility(View.VISIBLE);
        view = activityMainBinding.getRoot();
        setContentView(view);

        // setup Portfolio RecyclerView
        RecyclerView viewPortfo = (RecyclerView)sectionPortfolioBinding.recyclePortfolio;
        sectionAdapterPotfo = new RecyclerPortfoAdapter(this);
        ItemTouchHelper.Callback portfoCallback = new StockMoveCallback(this, sectionAdapterPotfo, true, false);
        ItemTouchHelper portfoTouchHelper = new ItemTouchHelper(portfoCallback);
        portfoTouchHelper.attachToRecyclerView(viewPortfo);
        viewPortfo.setLayoutManager(new LinearLayoutManager(MainActivity.this,
                LinearLayoutManager.VERTICAL, false));
        viewPortfo.setAdapter(sectionAdapterPotfo);

        // setup Favorite RecyclerView
        RecyclerView viewFavorite = (RecyclerView) sectionFavoriteBinding.recycleFavorite;
        sectionAdapterFavorite = new RecyclerFavoriteAdapter(this);
        ItemTouchHelper.Callback favoriteCallback = new StockMoveCallback(this, sectionAdapterFavorite, true, true) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                //final Stock item = sectionAdapterFavorite.getList().get(position);
                sectionAdapterFavorite.removeItem(position);

                /*
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mAdapter.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
                 */

            }
        };
        ItemTouchHelper favoriteTouchHelper = new ItemTouchHelper(favoriteCallback);
        favoriteTouchHelper.attachToRecyclerView(viewFavorite);
        viewFavorite.setLayoutManager(new LinearLayoutManager(MainActivity.this,
                LinearLayoutManager.VERTICAL, false));
        viewFavorite.setAdapter(sectionAdapterFavorite);

        // setup footer
        setFooter();

        apiController = new APIController(this);

    }

    @Override
    protected void onResume() {
        System.out.println("onResume");
        super.onResume();
        // setup date
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        activityMainBinding.date.setText(formatter.format(date));

        // Refresh
        fetch();
        refreshHandler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                fetch();
                refreshHandler.postDelayed(runnable, AUTO_REFRESH_DELAY);
            }
        }, AUTO_REFRESH_DELAY);

    }

    @Override
    protected void onPause() {
        System.out.println("onpause");
        refreshHandler.removeCallbacks(runnable); //stop refresh when activity not visible super.onPause();
        super.onPause();
    }

    // Reference: https://www.truiton.com/2018/06/android-autocompletetextview-suggestions-from-webservice-call/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //final TextView selectedText = findViewById(R.id.selected_item);
        getMenuInflater().inflate(R.menu.menu, menu);

        // Link search bar with search autocomplete
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        AutoCompleteTextView searchAutoComplete = (AutoCompleteTextView)searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        // Set up the SuggestAdapter for autocomplete object
        suggestAdapter = new SuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);

        // Set up SearchAutoComplete
        searchAutoComplete.setThreshold(1);
        searchAutoComplete.setAdapter(suggestAdapter);
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String queryString = suggestAdapter.getObj(i);
                searchView.setQuery(queryString, true);
                // Open info screen
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                intent.putExtra(EXTRA_TICKER, queryString);
                startActivity(intent);
            }
        });
        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Set up handler
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if(message.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAutoComplete.getText())) {
                        callApi(searchAutoComplete.getText().toString());
                    }
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void callApi(String queryString) {
        apiController.getQuery(queryString, data -> {
            System.out.println(queryString + data);
            List<String> res = new ArrayList<>();
            try {
                JSONArray jsonArray = data.getJSONArray("result");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    // Filter out invalid options
                    if (obj.getString("type").equals("Common Stock") && !obj.getString("symbol").contains(".")) {
                        String suggestion = obj.getString("symbol") + " | " + obj.getString("description");
                        res.add(suggestion);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Set data into SuggestAdapter and notify
            suggestAdapter.set(res);
            suggestAdapter.notifyDataSetChanged();
        });
    }

    // Reference: https://stackoverflow.com/questions/4096851/remove-underline-from-links-in-textview-android
    private void setFooter() {
        TextView footer = (TextView) activityMainBinding.footer;
        footer.setClickable(true);
        footer.setMovementMethod(LinkMovementMethod.getInstance());
        String content = "<a href='https://www.finnhub.io'>Powered by Finnhub</a>";
        Spannable s = (Spannable) Html.fromHtml(content);
        for (URLSpan u: s.getSpans(0, s.length(), URLSpan.class)) {
            s.setSpan(new UnderlineSpan() {
                public void updateDrawState(TextPaint tp) {
                    tp.setUnderlineText(false);
                }
            }, s.getSpanStart(u), s.getSpanEnd(u), 0);
        }
        footer.setText(s);
    }

    private void fetch() {
        List<String> portfoList = LocalStorage.getInstance(this).getOrder("portfolio");
        List<String> favoriteList = LocalStorage.getInstance(this).getOrder("favorite");

        Set<String> set = new HashSet<>();
        set.addAll(new HashSet<>(portfoList));
        set.addAll(new HashSet<>(favoriteList));

        Map<String, Stock> map = new HashMap<>();
        AtomicInteger requestNum = new AtomicInteger(set.size());

        apiController.getRequestQueue().addRequestEventListener((request, event) -> {
            if (event == RequestQueue.RequestEvent.REQUEST_FINISHED) {
                if (requestNum.get() == 0) {
                    //Toast.makeText(this, "Update 15 secs", Toast.LENGTH_SHORT).show();
                    updateWallet(portfoList, map);
                    updatePortfoSection(portfoList, map);
                    updateFavoriteSection(favoriteList, map);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        System.out.println("portfolio size: " + portfoList.size());
        System.out.println("favorite size: " + favoriteList.size());


        for (String ticker: set) {
            if (ticker == null || ticker.equals(""))
                continue;
            apiController.getDescript(ticker, info -> {
                try {
                    String company = info.getString("name");
                    apiController.getQuote(ticker, data -> {
                        try {
                            System.out.println("Update: " + ticker);
                            Double price = data.getDouble("c");
                            Double change = data.getDouble("d");
                            Double changePercent = data.getDouble("dp");
                            map.put(ticker, new Stock(ticker, company, price, change, changePercent));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        requestNum.getAndDecrement();
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
        if (portfoList.size() == 0 && favoriteList.size() == 0) {
            updateWallet(portfoList, map);
            updatePortfoSection(portfoList, map);
            updateFavoriteSection(favoriteList, map);
            progressBar.setVisibility(View.GONE);
        }/*
        if (portfoList.get(0).equals("") && favoriteList.get(0).equals("")) {
            updateWallet(portfoList, map);
            updatePortfoSection(portfoList, map);
            updateFavoriteSection(favoriteList, map);
            progressBar.setVisibility(View.GONE);
        }*/
    }

    private void updateWallet(List<String> list, Map<String, Stock> map) {
        Double balance = LocalStorage.getInstance(this).getBalance();
        Double value = 0.0;
        for (String ticker: list) {
            if (ticker == null || ticker.equals(""))
                continue;
            if (map.containsKey(ticker))
                value += map.get(ticker).currPrice * LocalStorage.getInstance(this).getPortfoOwned(ticker);
        }
        sectionPortfolioBinding.portfolioHeader.netWorth.setText(dataFormatter.toPriceFormat(value+balance));
        sectionPortfolioBinding.portfolioHeader.cashBalance.setText(dataFormatter.toPriceFormat(balance));
    }

    private void updatePortfoSection(List<String> list, Map<String, Stock> map) {
        List<Stock> currList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null || list.get(i).equals(""))
                continue;
            System.out.println("update: " + list.get(i));
            currList.add(map.get(list.get(i)));
        }
        sectionAdapterPotfo.set(currList);
        sectionAdapterPotfo.notifyDataSetChanged();
    }

    private void updateFavoriteSection(List<String> list, Map<String, Stock> map) {
        List<Stock> currList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null || list.get(i).equals("")) {
                continue;
            }

            //System.out.println("update: " + list.get(i));
            currList.add(map.get(list.get(i)));
        }
        sectionAdapterFavorite.set(currList);
        sectionAdapterFavorite.notifyDataSetChanged();
    }

    /*
    private void setPortfoSection() {
        List<String> list = new ArrayList<>();
        sectionAdapterPotfo.addSection(new PortfoSection(list, this));
        // Set up your RecyclerView with the SectionedRecyclerViewAdapter
        RecyclerView recyclerView = sectionPortfolioBinding.recyclePortfolio;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sectionAdapterPotfo);

        sectionAdapterFavorite.addSection(new FavoriteSection(list, this));
        RecyclerView favoriteRecycleView = sectionFavoriteBinding.recycleFavorite;
        favoriteRecycleView.setLayoutManager(new LinearLayoutManager(this));
        favoriteRecycleView.setAdapter(sectionAdapterFavorite);
    }

     */

    /*
    private void fetchPortfolio() {
        List<String> list = LocalStorage.getInstance(this).getOrder("portfolio");
        Item[] itemArr = new Item[list.size()];
        AtomicInteger requestNum = new AtomicInteger(list.size());
        for (int i = 0; i < list.size(); i++) {
            int index = i;
            apiController.getQuote(list.get(i), data -> {
                requestNum.getAndDecrement();
                try {
                    Double price = data.getDouble("c");
                    Double change = data.getDouble("d");
                    Double changePercent = data.getDouble("dp");
                    itemArr[index] = new Item(list.get(index), price, change, changePercent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }

     */
}