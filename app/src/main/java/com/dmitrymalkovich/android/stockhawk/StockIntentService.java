/*
 * Copyright 2016.  Dmitry Malkovich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dmitrymalkovich.android.stockhawk;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.TaskParams;

/**
 * Created by sam_chordas on 10/1/15.
 * <p/>
 * Updated By: Dmitry Malkovich.
 */
public class StockIntentService extends IntentService {

    public static final String EXTRA_TAG = "tag";
    public static final String EXTRA_SYMBOL = "symbol";

    public static final String ACTION_INIT = "init";
    public static final String ACTION_ADD = "add";

    public StockIntentService() {
        super(StockIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle args = new Bundle();
        if (intent.getStringExtra(EXTRA_TAG).equals(ACTION_ADD)) {
            args.putString(EXTRA_SYMBOL, intent.getStringExtra(EXTRA_SYMBOL));
        }

        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        StockTaskService stockTaskService = new StockTaskService(this);
        stockTaskService.onRunTask(new TaskParams(intent.getStringExtra(EXTRA_TAG), args));
    }
}
