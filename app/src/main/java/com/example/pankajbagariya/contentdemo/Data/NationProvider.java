package com.example.pankajbagariya.contentdemo.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import static com.example.pankajbagariya.contentdemo.Data.NationContract.NationEntry;

import static com.example.pankajbagariya.contentdemo.Data.NationContract.CONTENT_AUTHORITY;
import static com.example.pankajbagariya.contentdemo.Data.NationContract.NationEntry.TABLE_NAME;
import static com.example.pankajbagariya.contentdemo.Data.NationContract.PATH_COUNTRIES;
import static com.example.pankajbagariya.contentdemo.Data.NationContract.BASE_CONTENT_URI;

public class NationProvider extends ContentProvider {

    private static final String TAG = NationProvider.class.getSimpleName();
    private NationDbHelper databaseHelper;

    //constants for the operation
    private static final int COUNTRIES = 1;                     //For Whole table
    private static final int COUNTRIES_ID = 2;                  //For a specific row in  a table identified by _ID
    private static final int COUNTRIES_COUNTRY_NAME = 3;        //For a specific row in a table identified by COUNTRY NAME

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        uriMatcher.addURI(CONTENT_AUTHORITY,PATH_COUNTRIES,COUNTRIES);
        uriMatcher.addURI(CONTENT_AUTHORITY,PATH_COUNTRIES+"/#",COUNTRIES_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY,PATH_COUNTRIES+"/*",COUNTRIES_COUNTRY_NAME);


    }
    @Override
    public boolean onCreate() {
        databaseHelper = new NationDbHelper(getContext());
        return true;
    }


    public Cursor query(  Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // first parameter uri coming from mainactivity and this method also return uri
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)){
            case COUNTRIES:
                cursor = database.query(TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case COUNTRIES_ID:
                selection = NationEntry._ID + " = ?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(TAG+"Unknown uri: "+uri);
        }
        return cursor;
    }


    @Nullable
    @Override
    public String getType( @NonNull Uri uri) {
        return null;
    }


    @Nullable
    @Override
    public Uri insert( @NonNull Uri uri,  @Nullable ContentValues values) {

        switch (uriMatcher.match(uri)){

            case COUNTRIES:
                return insertRecord(uri,values,TABLE_NAME);
            default:
                throw new IllegalArgumentException(TAG+"Unknown uri "+uri);

        }

    }

    private Uri insertRecord(Uri uri, ContentValues values, String tableName) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
       long rowId = database.insert(tableName,null,values);

        if (rowId == -1){
            Log.e(TAG,"Insert error for uri "+uri);
            return null;
        }
        return ContentUris.withAppendedId(uri,rowId);
    }

    @Override
    public int delete( @NonNull Uri uri,  @Nullable String selection,  @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)){
            case COUNTRIES: // to delete whole table
                return deleteRecord(null,null,NationEntry.TABLE_NAME);
            case COUNTRIES_ID:  //to delete row by id
                selection = NationEntry._ID +" = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
             return  deleteRecord(selection,selectionArgs,NationEntry.TABLE_NAME);
            case COUNTRIES_COUNTRY_NAME: //to delete row by country
                selection = NationEntry.COLUMN_COUNTRY +" = ? ";
                selectionArgs = new String[]{String.valueOf(uri.getLastPathSegment())};
                                                                                           // last word of path if we get null by chance then whole row will delete
                return  deleteRecord(selection,selectionArgs,NationEntry.TABLE_NAME);
                //Above in case 2 and 3 selection and selectionArgs are again defined becoz if paramenter came null then whole will delete
            default:
                throw new IllegalArgumentException(TAG+"Unknown uri "+uri);
    }}

    private int deleteRecord(String selection,  String[] selectionArgs,String tableName){
        SQLiteDatabase database  = databaseHelper.getWritableDatabase();
        int rowDeleted= database.delete(tableName, selection, selectionArgs);
        return rowDeleted ;
    }
    @Override
    public int update( Uri uri,   ContentValues values,   String selection,  String[] selectionArgs) {
            switch (uriMatcher.match(uri)){
                case COUNTRIES:  // For countries table
                    return updateRecord(values,selection,selectionArgs,NationEntry.TABLE_NAME);

                default:
                    throw new IllegalArgumentException(TAG+"Unknown uri "+uri);
    }
}
    private int updateRecord(ContentValues values,String selection,  String[] selectionArgs,String tableName){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
       int rowUpdated = database.update(tableName, values, selection, selectionArgs);
        return rowUpdated;
    }

}
