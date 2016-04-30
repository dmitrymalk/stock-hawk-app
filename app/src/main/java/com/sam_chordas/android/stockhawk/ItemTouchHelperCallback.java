package com.sam_chordas.android.stockhawk;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by sam_chordas on 10/6/15.
 * Credit to Paul Burke (ipaulpro).
 * <p/>
 * Updated By: Dmitry Malkovich.
 * <p/>
 * This class enables swipe to delete in RecyclerView.
 */
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final SwipeListener mAdapter;

    public interface SwipeListener {

        void onItemDismiss(int position);
    }

    public interface ItemTouchHelperViewHolder {
        void onItemSelected();

        void onItemClear();
    }

    public ItemTouchHelperCallback(SwipeListener adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder sourceViewHolder,
                          RecyclerView.ViewHolder targetViewHolder) {
        ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) sourceViewHolder;
        itemViewHolder.onItemClear();
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemSelected();
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
        itemViewHolder.onItemClear();
    }
}
