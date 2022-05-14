package com.example.app.Adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class SuggestAdapter extends ArrayAdapter<String> {
    private List<String> list;

    public SuggestAdapter(Context context, int resource) {
        super(context, resource);
        list = new ArrayList<>();
    }

    // clear and update the list
    public void set(List<String> dataList) {
        list.clear();
        list.addAll(dataList);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int pos) {
        return list.get(pos);
    }

    public String getObj(int pos) {
        return list.get(pos).split(" ")[0];
    }
}
