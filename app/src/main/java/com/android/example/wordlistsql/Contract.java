package com.android.example.wordlistsql;

import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

public final class Contract {

    public static final String
            DATABASE_NAME = "wordlist",
            AUTHORITY = "com.android.example.wordlistsqlfinished.provider",
            CONTENT_PATH = "words",
            COUNT = "count",
            SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.com.example.provider.words",
            MULTIPLE_RECORDS_MIME_TYPE = "vnd.android.cursor.item/vnd.com.example.provider.words";

    public static final Uri
            CONTENT_URI = Uri.parse("content://" + AUTHORITY +
            "/" + CONTENT_PATH),
            ROW_COUNT_URI = Uri.parse("content://" + AUTHORITY +
            "/" + CONTENT_PATH + "/" + COUNT);

    public static final int ALL_ITEMS = -2;

    private Contract() { }

    public static abstract class WordList implements BaseColumns {
        public static final String WORD_LIST_TABLE = "word_entries";

        // Column names...
        public static final String KEY_ID = "_id";
        public static final String KEY_WORD = "word";
    }
}
