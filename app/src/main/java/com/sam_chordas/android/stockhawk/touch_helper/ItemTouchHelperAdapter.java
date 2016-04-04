package com.sam_chordas.android.stockhawk.touch_helper;

import android.view.View;

/**
 * Created by sam_chordas on 10/6/15.
 * credit to Paul Burke (ipaulpro)
 * Interface to enable swipe to delete
 */
public interface ItemTouchHelperAdapter {

  void onItemDismiss(int position);
}
