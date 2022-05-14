package com.example.app.Section;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    final TextView itemTitle;
    final TextView itemPrice;
    final TextView itemNumShare;
    final TextView itemChange;
    final TextView itemChangePercent;
    final ImageView itemIcon;
    final Button button;

    public ItemViewHolder(View itemView) {
        super(itemView);
        itemTitle = (TextView) itemView.findViewById(R.id.item_title);
        itemPrice = (TextView) itemView.findViewById(R.id.item_price);
        itemNumShare = (TextView) itemView.findViewById(R.id.item_subtitle);
        itemChange = (TextView) itemView.findViewById(R.id.item_change);
        itemChangePercent = (TextView) itemView.findViewById(R.id.item_change_percent);
        itemIcon = (ImageView) itemView.findViewById(R.id.item_icon);
        button = (Button) itemView.findViewById(R.id.chevron_right);
    }
}
