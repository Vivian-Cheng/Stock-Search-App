package com.example.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.app.Section.Stock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocalStorage {
    private static final String DELIMITER = ",";
    private static final float initBalance = 25000.0f;
    private static LocalStorage instance;
    private static SharedPreferences portfoOwn;
    private static SharedPreferences portfoCost;
    private static SharedPreferences favorite;
    private static SharedPreferences wallet;
    private static SharedPreferences order;
    private static Context context;

    private LocalStorage(Context context) {
        this.context = context;
        order = context.getSharedPreferences(context.getString(R.string.local_storage_order), Context.MODE_PRIVATE);
        SharedPreferences.Editor orderEditor = order.edit();
        if (!order.contains("portfolio"))
            orderEditor.putString("portfolio", "");
        if (!order.contains("favorite"))
            orderEditor.putString("favorite", "");
        orderEditor.commit();

        portfoOwn = context.getSharedPreferences(context.getString(R.string.local_storage_portfo_own), Context.MODE_PRIVATE);
        portfoCost = context.getSharedPreferences(context.getString(R.string.local_storage_portfo_cost), Context.MODE_PRIVATE);

        // NOT USE
        favorite = context.getSharedPreferences(context.getString(R.string.local_storage_favorite), Context.MODE_PRIVATE);
        if (!favorite.contains("set")) {
            System.out.println("init favorite set");
            SharedPreferences.Editor favoriteEditor = favorite.edit();
            Set<String> set = new HashSet<>();
            favoriteEditor.putStringSet("set", set);
            favoriteEditor.commit();
        }

        wallet = context.getSharedPreferences(context.getString(R.string.local_storage_wallet), Context.MODE_PRIVATE);
        if (!wallet.contains("balance")) {
            SharedPreferences.Editor walletEditor = wallet.edit();
            walletEditor.putFloat("balance", initBalance);
            walletEditor.commit();
        }
    }

    public static LocalStorage getInstance(Context context) {
        if (instance == null)
            instance = new LocalStorage(context);
        return instance;
    }

    public List<String> getOrder(String key) {
        String res = order.getString(key, "");
        if (res.length() == 0) {
            return new ArrayList<String>();
        }
        String[] orderList = res.split(DELIMITER);
        //System.out.println("getOrder: " + key + orderList);
        return Arrays.asList(orderList);
    }

    public void updateOrderFromStock(String type, List<Stock> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1)
                sb.append(list.get(i).ticker);
            else
                sb.append(list.get(i).ticker+DELIMITER);
        }
        SharedPreferences.Editor editor = order.edit();
        editor.putString(type, sb.toString());
        editor.commit();
    }

    public void updateOrder(String type, List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1)
                sb.append(list.get(i));
            else
                sb.append(list.get(i)+DELIMITER);
        }
        SharedPreferences.Editor editor = order.edit();
        editor.putString(type, sb.toString());
        editor.commit();
    }

    /**
     *
     * @param type portfolio or favorite
     * @param ticker must not exist in the file
     */
    public void addToOrder(String type, String ticker) {
        SharedPreferences.Editor editor = order.edit();
        String prevOrder = order.getString(type, "");
        String currOrder = "";
        if (prevOrder.length() == 0)
            currOrder = ticker;
        else
            currOrder = prevOrder + DELIMITER + ticker;
        editor.putString(type, currOrder);
        editor.commit();
    }

    /**
     *
     * @param type portfolio or favorite
     * @param ticker must exist in the file
     */
    public void rmFromOrder(String type, String ticker) {
        List<String> prev = getOrder(type);
        List<String> curr = new ArrayList<>();
        for (int i = 0; i < prev.size(); i++) {
            if (prev.get(i).equals(ticker))
                continue;
            curr.add(prev.get(i));
        }
        updateOrder(type, curr);
    }

    /**
     *
     * @param symbol ticker of the stock
     * @param numOfShare buy (integer > 0), sell (integer < 0)
     * @param amount amount to buy (double > 0), amount to sell (double < 0)
     */
    public void toPortfo(String symbol, int numOfShare, Double amount) {
        SharedPreferences.Editor ownEditor = portfoOwn.edit();
        SharedPreferences.Editor costEditor = portfoCost.edit();
        if (portfoOwn.getInt(symbol, 0) == 0)
            addToOrder("portfolio", symbol);
        ownEditor.putInt(symbol, numOfShare + portfoOwn.getInt(symbol, 0));
        costEditor.putFloat(symbol, (float) (amount + portfoCost.getFloat(symbol, 0.0f)));
        ownEditor.commit();
        costEditor.commit();
        if (portfoOwn.getInt(symbol, 0) == 0) {
            ownEditor.remove(symbol);
            costEditor.remove(symbol);
            ownEditor.commit();
            costEditor.commit();
            rmFromOrder("portfolio", symbol);
        }
    }

    public int getPortfoOwned(String symbol) {
        return portfoOwn.getInt(symbol, 0);
    }

    public Double getPortfoCost(String symbol) {
        return Double.valueOf(portfoCost.getFloat(symbol, 0.0f));
    }
    public Double getPortfoAvg(String symbol) {
        int num = getPortfoOwned(symbol);
        if (num == 0)
            return 0.0;
        return getPortfoCost(symbol) / num;
    }

    /**
     * Add or remove stock in favorite set
     * @param symbol ticker of the stock
     */
    public void toFavorite(String symbol) {
        //SharedPreferences.Editor editor = favorite.edit();
        //Set<String> set = favorite.getStringSet("set", new HashSet<String>());
        if (inFavorite(symbol)) {
            //set.remove(symbol);
            rmFromOrder("favorite", symbol);
        } else {
            //set.add(symbol);
            addToOrder("favorite", symbol);
        }
        //System.out.println("toFavorite: " + set);
        //editor.putStringSet("set", new HashSet<String>(set));
        //editor.commit();
    }


    public boolean inFavorite(String symbol) {
        //System.out.println("infavorite: " + symbol + favorite.getStringSet("set", new HashSet<String>()).contains(symbol));
        //Set<String> set = favorite.getStringSet("set", new HashSet<String>());
        //System.out.println(set);
        List<String> list = getOrder("favorite");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(symbol))
                return true;
        }
        return false;
    }

    /**
     *
     * @param amount buy ( double < 0), sell (double > 0)
     */
    public void toBalance(Double amount) {
        SharedPreferences.Editor editor = wallet.edit();
        editor.putFloat("balance", (float) (amount + getBalance()));
        editor.commit();
    }

    public Double getBalance() {
        return Double.valueOf(wallet.getFloat("balance", 0.0f));
    }

}
