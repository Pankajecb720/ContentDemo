package com.example.pankajbagariya.contentdemo;

import android.provider.BaseColumns;

/* A contract class contains constants that define names for URIs, tables, and columns.  */
public final class NationContract {

    public static final class NationEntry implements BaseColumns {

        // Table Name
        public static final String TABLE_NAME = "countries";

        // Columns
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_COUNTRY = "country";
        public static final String COLUMN_CONTINENT = "continent";
    }
}

