package com.joy.tweetit;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = MyDatabase.NAME, version = MyDatabase.VERSION)
public class MyDatabase {

    public static final String NAME = "TweetDB";

    public static final int VERSION = 1;
}
