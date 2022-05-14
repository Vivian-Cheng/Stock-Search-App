package com.example.app;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.format.DateUtils;
import android.util.TypedValue;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;

public class DataFormatter {
    public boolean isInteger(String number) {
        try {
            int value = Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }

    public String toDateFormat(String string) {
        String res = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        try {
            Date date = formatter.parse(string);
            res = outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String toDateNews(String datetime) {
        long unix_seconds = Long.parseLong(datetime);
        Date date = new Date(unix_seconds * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
        return formatter.format(date);
    }

    public String toDecimal(Double num) {
        DecimalFormat format = new DecimalFormat("0.00");
        return String.format("%.2f", num);
    }

    public String toPriceFormat(Double num) {
        //NumberFormat format = NumberFormat.getCurrencyInstance();
        //format.setMaximumFractionDigits(2);
        //format.setCurrency(Currency.getInstance("USD"));
        DecimalFormat format = new DecimalFormat("0.00");
        return "$"+format.format(num);
    }

    public String toPercentFormat(Double num) {
        //NumberFormat format = NumberFormat.getPercentInstance();
        //format.setMinimumFractionDigits(2);
        return "("+String.format("%.2f", num)+"%)";
    }

    public void toTotal(int[] res, JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                res[0] += jsonArray.getJSONObject(i).getInt("mention");
                res[1] += jsonArray.getJSONObject(i).getInt("positiveMention");
                res[2] += jsonArray.getJSONObject(i).getInt("negativeMention");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String toElapsedTime(String timestamp) {
        long endDate = Long.parseLong(timestamp);
        long startDate = System.currentTimeMillis() / 1000;
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(endDate, startDate, 1, DateUtils.FORMAT_ABBREV_RELATIVE);
        return timeAgo;
    }

    public String toHtmlLink(String url) {
        return "<a href='" + url + "'>" + url + "</a>";
    }

    public static int pxToDp(final Context context, final float px) {
        return (int)(px / context.getResources().getDisplayMetrics().density);
    }

    public static int dpToPx(final Context context, final float dp) {
        return (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
    }
}
