package com.example.app.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app.DataFormatter;
import com.example.app.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RecyclerNewAdapter extends RecyclerView.Adapter<RecyclerNewAdapter.ViewHolder> {
    private List<JSONObject> list;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.news_item);
        }

        public CardView getCardView() {return cardView; }
    }
    public RecyclerNewAdapter(List<JSONObject> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        else return 2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_item_large, viewGroup, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_item, viewGroup, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int pos) {
        DataFormatter dataFormatter = new DataFormatter();
        TextView source = viewHolder.getCardView().findViewById(R.id.news_source);
        TextView elapsed = viewHolder.getCardView().findViewById(R.id.news_elapsed);
        TextView headline = viewHolder.getCardView().findViewById(R.id.news_headline);
        ImageView img = viewHolder.getCardView().findViewById(R.id.news_img);
        try {
            source.setText(list.get(pos).getString("source"));
            elapsed.setText(dataFormatter.toElapsedTime(list.get(pos).getString("datetime")));
            headline.setText(list.get(pos).getString("headline"));
            //Picasso.get().load(list.get(pos).getString("image")).into(img);
            Glide.with(context).load(list.get(pos).getString("image")).into(img);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        viewHolder.getCardView().setOnClickListener(view -> {
            String webUrl = "";
            String text = "";
            final Dialog dialog = new Dialog(context);
            //Window window = dialog.getWindow();
            //window.requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.news_dialog);
            //window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView diaSource = dialog.findViewById(R.id.source);
            TextView diaDate = dialog.findViewById(R.id.timestamp);
            TextView diaHeadline = dialog.findViewById(R.id.headline);
            TextView diaSummary = dialog.findViewById(R.id.summary);
            Button chromeBtn = dialog.findViewById(R.id.chrome);
            Button twitterBtn = dialog.findViewById(R.id.twitter);
            Button fbBtn = dialog.findViewById(R.id.facebook);
            try {
                text = list.get(pos).getString("headline");
                diaSource.setText(list.get(pos).getString("source"));
                diaDate.setText(dataFormatter.toDateNews(list.get(pos).getString("datetime")));
                diaHeadline.setText(list.get(pos).getString("headline"));
                diaSummary.setText(list.get(pos).getString("summary"));
                webUrl = list.get(pos).getString("url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String chromeUrl = webUrl;
            chromeBtn.setOnClickListener(chromeView -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(chromeUrl));
                context.startActivity(intent);
            });
            String twUrl = "https://twitter.com/intent/tweet?text=" + text + "&url=" + webUrl;
            twitterBtn.setOnClickListener(twView -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twUrl));
                context.startActivity(intent);
            });
            String fbUrl = "https://www.facebook.com/sharer/sharer.php?u=" + webUrl + "&src=sdkpreparse";
            fbBtn.setOnClickListener(fbView -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl));
                context.startActivity(intent);
            });
            dialog.show();

        });

    }

    @Override
    public int getItemCount() {return list.size();}
}
