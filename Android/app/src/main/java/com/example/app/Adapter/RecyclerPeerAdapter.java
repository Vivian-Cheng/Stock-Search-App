package com.example.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.app.InfoActivity;
import com.example.app.R;

import java.util.List;

public class RecyclerPeerAdapter extends RecyclerView.Adapter<RecyclerPeerAdapter.ViewHolder> {
    private List<String> list;
    private Context context;
    private static final String EXTRA_TICKER = "com.example.app.MESSAGE";
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            textView = (TextView) view.findViewById(R.id.peer_item);
        }

        public TextView getTextView() {
            return textView;
        }
    }
    public RecyclerPeerAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_peers_item, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int pos) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextView().setText(Html.fromHtml("<u>"+list.get(pos)+"</u>"));
        viewHolder.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InfoActivity.class);
                intent.putExtra(EXTRA_TICKER, list.get(pos).replace(",",""));
                context.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list.size();
    }
}
