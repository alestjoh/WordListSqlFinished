/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.wordlistsql;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static com.android.example.wordlistsql.Contract.AUTHORITY;
import static com.android.example.wordlistsql.Contract.CONTENT_PATH;

/**
 * Implements a simple Adapter for a RecyclerView.
 * Demonstrates how to add a click handler for each item in the ViewHolder.
 */
public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder> {

    /**
     *  Custom view holder with a text view and two buttons.
     */
    class WordViewHolder extends RecyclerView.ViewHolder {
        public final TextView wordItemView;
        Button delete_button;
        Button edit_button;

        public WordViewHolder(View itemView) {
            super(itemView);
            wordItemView = (TextView) itemView.findViewById(R.id.word);
            delete_button = (Button)itemView.findViewById(R.id.delete_button);
            edit_button = (Button)itemView.findViewById(R.id.edit_button);
        }
    }

    private static final String TAG = WordListAdapter.class.getSimpleName();

    public static final String EXTRA_ID = "ID";
    public static final String EXTRA_WORD = "WORD";
    public static final String EXTRA_POSITION = "POSITION";

    private final LayoutInflater mInflater;
    Context mContext;

    private String queryUri = Contract.CONTENT_URI.toString(); // base uri
    private static final String[] projection = new String[] {CONTENT_PATH}; //table
    private String selectionClause = null;
    private String selectionArgs[] = null;
    private String sortOrder = "ASC";

    public static final String COUNT = "count";
    public static final Uri ROW_COUNT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH + "/" + COUNT);

    public WordListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.wordlist_item, parent, false);
        return new WordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        String word = "";
        int id = -1;

        Cursor cursor = mContext.getContentResolver().query(
                Uri.parse(queryUri),
                null,
                null,
                null,
                sortOrder);

        // Keep a reference to the view holder for the click listener
        final WordViewHolder h = holder; // needs to be final for use in callback
        // Attach a click listener to the DELETE button.

        if (cursor != null) {
            if (cursor.moveToPosition(position)) {
                int indexWord = cursor.getColumnIndex(Contract.WordList.KEY_WORD);
                word = cursor.getString(indexWord);
                holder.wordItemView.setText(word);
                int indexId = cursor.getColumnIndex(Contract.WordList.KEY_ID);
                id = cursor.getInt(indexId);
            } else {
                holder.wordItemView.setText(R.string.error_no_word);
            }

            cursor.close();

        } else {
            Log.e (TAG, "onBindViewHolder: Cursor is null.");
        }

        holder.delete_button.setOnClickListener(new MyButtonOnClickListener(
                id, null)  {


            @Override
            public void onClick(View v ) {
                selectionArgs = new String[]{Integer.toString(id)};
                int deleted = mContext.getContentResolver().delete(
                        Contract.CONTENT_URI, CONTENT_PATH,selectionArgs);
                if (deleted > 0) {
                    // Need both calls
                    notifyItemRemoved(h.getAdapterPosition());
                    notifyItemRangeChanged(
                            h.getAdapterPosition(), getItemCount());
                } else {
                    Log.d (TAG, mContext.getString(R.string.not_deleted) + deleted);
                }
            }
        });

        // Attach a click listener to the EDIT button.
        holder.edit_button.setOnClickListener(new MyButtonOnClickListener(
                id, word) {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditWordActivity.class);

                intent.putExtra(EXTRA_ID, id);
                intent.putExtra(EXTRA_POSITION, h.getAdapterPosition());
                intent.putExtra(EXTRA_WORD, word);

                // Start an empty edit activity.
                ((Activity) mContext).startActivityForResult(intent, MainActivity.WORD_EDIT);
            }
        });
    }

    @Override
    public int getItemCount() {
        Cursor cursor = mContext.getContentResolver().query(
                Contract.ROW_COUNT_URI, new String[] {"count(*) AS count"},
                selectionClause, selectionArgs, sortOrder);
        try {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        } catch (Exception e){
            Log.d(TAG, "EXCEPTION getItemCount: " + e);
            return -1;
        }
    }
}


