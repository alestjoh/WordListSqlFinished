package com.android.example.wordlistsql;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.android.example.wordlistsql.Contract.CONTENT_URI;
import static com.android.example.wordlistsql.Contract.MULTIPLE_RECORDS_MIME_TYPE;
import static com.android.example.wordlistsql.Contract.SINGLE_RECORD_MIME_TYPE;

public class WordListContentProvider extends ContentProvider {

    public static final String LOG_TAG = "WordListContentProvider";
    private static final int
            URI_ALL_ITEMS_CODE = 10,
            URI_ONE_ITEM_CODE = 20,
            URI_COUNT_CODE = 30;
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private WordListOpenHelper db;

    @Override
    public boolean onCreate() {
        db = new WordListOpenHelper(getContext());
        initializeUriMatching();

        return true;
    }

    private void initializeUriMatching() {
        uriMatcher.addURI(
                Contract.AUTHORITY,
                Contract.CONTENT_PATH,
                URI_ALL_ITEMS_CODE);
        uriMatcher.addURI(
                Contract.AUTHORITY,
                Contract.CONTENT_PATH + "/#",
                URI_ONE_ITEM_CODE);
        uriMatcher.addURI(
                Contract.AUTHORITY,
                Contract.CONTENT_PATH + "/" + Contract.COUNT,
                URI_COUNT_CODE);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor cursor = null;

        switch (uriMatcher.match(uri)) {
            case URI_ALL_ITEMS_CODE:
                cursor = db.query(Contract.ALL_ITEMS);
                break;
            case URI_COUNT_CODE:
                cursor = db.count();
                break;
            case URI_ONE_ITEM_CODE:
                cursor = db.query(Integer.parseInt(uri.getLastPathSegment()));
                break;
            case UriMatcher.NO_MATCH:
                Log.e(LOG_TAG, "No match for this Uri in scheme: " + uri);
                break;
            default:
                Log.e(LOG_TAG, "Invalid URI not recognized: " + uri);
                break;
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_ALL_ITEMS_CODE:
                return MULTIPLE_RECORDS_MIME_TYPE;
            case URI_ONE_ITEM_CODE:
                return SINGLE_RECORD_MIME_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long id = db.insert(contentValues);
        return Uri.parse(CONTENT_URI + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return db.delete(Integer.parseInt(strings[0]));
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String s, @Nullable String[] strings) {
        return db.update(Integer.parseInt(strings[0]),
                values.getAsString(Contract.WordList.KEY_WORD));
    }
}
