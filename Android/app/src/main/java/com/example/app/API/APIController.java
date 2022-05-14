package com.example.app.API;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class APIController {
    //private static final String URL = "http://192.168.0.115:3000/";
    private static final String URL = "http://csci571nodejs-env.eba-sx8smmpb.us-west-1.elasticbeanstalk.com/";
    private static final String API_DESCRIPT = "descript/";
    private static final String API_CANDLE = "candle/";
    private static final String API_QUOTE = "quote/";
    private static final String API_QUERY = "q/";
    private static final String API_NEWS = "news/";
    private static final String API_RECOM = "recommendation/";
    private static final String API_SOCIAL = "sentiment/";
    private static final String API_PEERS = "peers/";
    private static final String API_EARN = "earnings/";

    private Context context;
    private RequestQueue requestQueue;

    public APIController(Context context) {
        this.context = context;
        requestQueue = VolleySingleton.getInstance(this.context).getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void jsonObjRequester(String url, Listener<JSONObject> listener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            listener.onResponse(response);
        }, error -> {
            System.out.println(error.getMessage());
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        requestQueue.add(jsonObjectRequest);
        /*
        try {
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                listener.onResponse(response);
            }, error -> {
                System.out.println(error.getMessage());
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
         */
    }

    public void jsonArrRequester(String url, Listener<JSONArray> listener) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            listener.onResponse(response);
        }, error -> {
            System.out.println(error.getMessage());
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        requestQueue.add(jsonArrayRequest);
        /*
        try {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
                listener.onResponse(response);
            }, error -> {
                System.out.println(error.getMessage());
            });
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
         */
    }

    // GET request for company description
    public void getDescript(String ticker, Listener<JSONObject> listener) {
        String url = URL + API_DESCRIPT + ticker;
        jsonObjRequester(url, listener);
    }

    // GET request for company historical data
    public void getCandle(String ticker, String resolution, String from, String to, Listener<JSONObject> listener) {
        String url = URL + API_CANDLE + ticker + '/' + resolution + '/' + from + '/' + to;
        jsonObjRequester(url, listener);
    }

    // GET request for latest price of stock
    public void getQuote(String ticker, Listener<JSONObject> listener) {
        String url = URL + API_QUOTE + ticker;
        jsonObjRequester(url, listener);
    }

    // GET request for autocomplete
    public void getQuery(String ticker, Listener<JSONObject> listener) {
        String url = URL + API_QUERY + ticker;
        jsonObjRequester(url, listener);
    }

    // GET request for company news
    public void getNews(String ticker, Listener<JSONArray> listener) {
        String url = URL + API_NEWS + ticker;
        jsonArrRequester(url, listener);
    }

    // GET request for company recommendation trends
    public void getRecom(String ticker, Listener<JSONArray> listener) {
        String url = URL + API_RECOM + ticker;
        jsonArrRequester(url, listener);
    }

    // GET request for company social sentiment
    public void getSocial(String ticker, Listener<JSONObject> listener) {
        String url = URL + API_SOCIAL + ticker;
        jsonObjRequester(url, listener);
    }

    // GET request for company peers
    public void getPeers(String ticker, Listener<JSONArray> listener) {
        String url = URL + API_PEERS + ticker;
        jsonArrRequester(url, listener);
    }

    // GET request for company earnings
    public void getEarn(String ticker, Listener<JSONArray> listener) {
        String url = URL + API_EARN + ticker;
        jsonArrRequester(url, listener);
    }

}
