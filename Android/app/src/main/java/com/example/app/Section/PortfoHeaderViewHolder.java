package com.example.app.Section;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;

public class PortfoHeaderViewHolder extends RecyclerView.ViewHolder {
    final TextView netWorth;
    final TextView cashBalance;
    public PortfoHeaderViewHolder(View view) {
        super(view);
        netWorth = (TextView) view.findViewById(R.id.net_worth);
        cashBalance = (TextView) view.findViewById(R.id.cash_balance);
    }
}
