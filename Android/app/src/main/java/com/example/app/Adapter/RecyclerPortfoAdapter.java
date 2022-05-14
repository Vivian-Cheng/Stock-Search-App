package com.example.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.app.DataFormatter;
import com.example.app.InfoActivity;
import com.example.app.LocalStorage;
import com.example.app.R;
import com.example.app.Section.Stock;
import com.example.app.Section.StockMoveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerPortfoAdapter extends RecyclerView.Adapter<RecyclerPortfoAdapter.ViewHolder> implements StockMoveCallback.ItemTouchHelperContract {
    private List<Stock> list = new ArrayList<>();
    private Context context;
    private static final String EXTRA_TICKER = "com.example.app.MESSAGE";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView itemTitle;
        final TextView itemPrice;
        final TextView itemNumShare;
        final TextView itemChange;
        final TextView itemChangePercent;
        final ImageView itemIcon;
        final Button button;
        View rowView;

        public ViewHolder(View view) {
            super(view);
            rowView = view;
            // Define click listener for the ViewHolder's View
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemPrice = (TextView) view.findViewById(R.id.item_price);
            itemNumShare = (TextView) view.findViewById(R.id.item_subtitle);
            itemChange = (TextView) view.findViewById(R.id.item_change);
            itemChangePercent = (TextView) view.findViewById(R.id.item_change_percent);
            itemIcon = (ImageView) view.findViewById(R.id.item_icon);
            button = (Button) view.findViewById(R.id.chevron_right);
        }

    }
    public RecyclerPortfoAdapter(Context context) {
        this.context = context;
    }

    public void set(List<Stock> list) {
        this.list.clear();
        this.list.addAll(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.section_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int pos) {
        System.out.println("onBindViewHolder: " +list.get(pos));
        Stock stock = (Stock) list.get(pos);
        String ticker = stock.ticker;
        int own = LocalStorage.getInstance(context).getPortfoOwned(ticker);
        Double value = own * stock.currPrice;
        Double change = Double.valueOf(DataFormatter.toDecimal((stock.currPrice - LocalStorage.getInstance(context).getPortfoAvg(ticker)) * own));
        Double changePercent = change / LocalStorage.getInstance(context).getPortfoCost(ticker) * 100;
        viewHolder.itemTitle.setText(stock.ticker);
        viewHolder.itemPrice.setText(DataFormatter.toPriceFormat(value));
        viewHolder.itemNumShare.setText(String.valueOf(own) + " shares");
        viewHolder.itemChange.setText(DataFormatter.toPriceFormat(change));
        viewHolder.itemChangePercent.setText(DataFormatter.toPercentFormat(changePercent));
        if (change > 0) {
            viewHolder.itemChange.setTextColor(context.getResources().getColor(R.color.lightgreen));
            viewHolder.itemChangePercent.setTextColor(context.getResources().getColor(R.color.lightgreen));
            viewHolder.itemIcon.setImageDrawable(context.getDrawable(R.drawable.ic_trending_up));
        }
        if (change < 0) {
            viewHolder.itemChange.setTextColor(context.getResources().getColor(R.color.lightred));
            viewHolder.itemChangePercent.setTextColor(context.getResources().getColor(R.color.lightred));
            viewHolder.itemIcon.setImageDrawable(context.getDrawable(R.drawable.ic_trending_down));
        }
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InfoActivity.class);
                intent.putExtra(EXTRA_TICKER, stock.ticker);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        System.out.println("move from: " + fromPosition);
        System.out.println("move to: " + toPosition);
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
        LocalStorage.getInstance(context).updateOrderFromStock("portfolio", list);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(RecyclerView.ViewHolder viewHolder) {
    }

    @Override
    public void onRowClear(RecyclerView.ViewHolder myViewHolder) {

    }

}
