package com.example.app.Section;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Adapter.RecyclerFavoriteAdapter;
import com.example.app.Adapter.RecyclerPortfoAdapter;
import com.example.app.R;

public class StockMoveCallback extends ItemTouchHelper.Callback {
    private final ItemTouchHelperContract adapter;
    private final boolean pressDragEnabled;
    private final boolean viewSwipeEnabled;
    Context context;
    private Paint clearPaint;
    private ColorDrawable background;
    private int backgroundColor;
    private Drawable deleteDrawable;
    private int intrinsicWidth;
    private int intrinsicHeight;

    public StockMoveCallback(Context context, ItemTouchHelperContract adapter, boolean pressDragEnabled, boolean viewSwipeEnabled) {
        this.context = context;
        this.adapter = adapter;
        this.pressDragEnabled = pressDragEnabled;
        this.viewSwipeEnabled = viewSwipeEnabled;
        background = new ColorDrawable();
        backgroundColor = R.color.lightred; // ?
        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        deleteDrawable = ContextCompat.getDrawable(this.context, R.drawable.ic_delete);
        intrinsicWidth = deleteDrawable.getIntrinsicWidth();
        intrinsicHeight = deleteDrawable.getIntrinsicHeight();
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return pressDragEnabled;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return viewSwipeEnabled;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        if (viewSwipeEnabled)
            return makeMovementFlags(dragFlags, ItemTouchHelper.LEFT);
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        adapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCancelled = dX == 0 && !isCurrentlyActive;

        if (isCancelled) {
            clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        background.setColor(context.getResources().getColor(backgroundColor));
        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);

        int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
        int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
        int deleteIconRight = itemView.getRight() - deleteIconMargin;
        int deleteIconBottom = deleteIconTop + intrinsicHeight;


        deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        deleteDrawable.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

    }

    private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
        c.drawRect(left, top, right, bottom, clearPaint);
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return 0.7f;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof RecyclerPortfoAdapter.ViewHolder) {
                RecyclerPortfoAdapter.ViewHolder portfoViewHolder= (RecyclerPortfoAdapter.ViewHolder) viewHolder;
                adapter.onRowSelected(portfoViewHolder);
            }
            if (viewHolder instanceof RecyclerFavoriteAdapter.ViewHolder) {
                RecyclerFavoriteAdapter.ViewHolder favoriteViewHolder= (RecyclerFavoriteAdapter.ViewHolder) viewHolder;
                adapter.onRowSelected(favoriteViewHolder);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof RecyclerPortfoAdapter.ViewHolder) {
            RecyclerPortfoAdapter.ViewHolder portfoViewHolder= (RecyclerPortfoAdapter.ViewHolder) viewHolder;
            adapter.onRowClear(portfoViewHolder);
        }

        if (viewHolder instanceof RecyclerFavoriteAdapter.ViewHolder) {
            RecyclerFavoriteAdapter.ViewHolder favoriteViewHolder= (RecyclerFavoriteAdapter.ViewHolder) viewHolder;
            adapter.onRowClear(favoriteViewHolder);
        }
    }

    public interface ItemTouchHelperContract {

        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(RecyclerView.ViewHolder viewHolder);
        void onRowClear(RecyclerView.ViewHolder viewHolder);

    }
}
