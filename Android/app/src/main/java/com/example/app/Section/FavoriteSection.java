package com.example.app.Section;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class FavoriteSection extends Section {
    Context context;
    List<String> list;

    public FavoriteSection(List<String> list, Context context) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.section_item)
                .headerResourceId(R.layout.section_header_favorite)
                .build());
        this.list = list;
        this.context = context;

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

        // bind your view here
        // itemHolder.tvItem.setText(itemList.get(position));
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        // return an empty instance of ViewHolder for the headers of this section
        return new FavoriteHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final FavoriteHeaderViewHolder headerHolder = (FavoriteHeaderViewHolder) holder;

        //headerHolder.tvTitle.setText(title);
    }
}
