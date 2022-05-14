package com.example.app.Section;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class PortfoSection extends Section {
    Context context;
    List<Stock> list;

    public PortfoSection(List<Stock> list, Context context) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.section_item)
                .headerResourceId(R.layout.section_header_portfolio)
                .build());
        this.list = list;
        this.context = context;

    }

    public void set(List<Stock> list) {
        this.list.clear();
        this.list.addAll(list);
    }

    @Override
    public int getContentItemsTotal() {
        return list.size(); // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        System.out.println("onBindItem: " + list.get(position));

        // bind your view here
        // itemHolder.tvItem.setText(itemList.get(position));
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        // return an empty instance of ViewHolder for the headers of this section
        return new PortfoHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final PortfoHeaderViewHolder headerHolder = (PortfoHeaderViewHolder) holder;
        for (int i = 0; i < list.size(); i++) {
            System.out.println("onBindHeader: " + list.get(i));
        }

        //headerHolder.tvTitle.setText(title);
    }
}
