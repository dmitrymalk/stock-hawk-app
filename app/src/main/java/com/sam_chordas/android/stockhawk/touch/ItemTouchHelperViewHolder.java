package com.sam_chordas.android.stockhawk.touch;

/**
 * Created by sam_chordas on 10/6/15.
 * credit to Paul Burke (ipaulpro)
 *
 * Updated By: Dmitry Malkovich.
 *
 * Interface for enabling swiping to delete.
 */
public interface ItemTouchHelperViewHolder {
    void onItemSelected();

    void onItemClear();
}
