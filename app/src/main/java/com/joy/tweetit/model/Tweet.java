package com.joy.tweetit.model;

import android.support.annotation.NonNull;

import com.joy.tweetit.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Minutes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by joy0520 on 2017/3/3.
 */

@Table(database = MyDatabase.class)
public class Tweet extends BaseModel implements Comparable<Tweet> {

    @PrimaryKey
    @Column
    private long id;
    @Column
    private String created_at;
    @Column
    private String text;
    @ForeignKey(saveForeignKeyModel = true)
    @Column
    private User user;

    public String getText() {
        return text;
    }

    public String getCreatedAtFormatString() {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
        Date date;
        try {
            date = format.parse(created_at);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        GregorianCalendar current = new GregorianCalendar();
        GregorianCalendar created = new GregorianCalendar();
        created.setTime(date);

        DateTime currentDT = new DateTime(current.getTimeInMillis());
        DateTime createdDT = new DateTime(created.getTimeInMillis());
        Duration diff = new Duration(createdDT, currentDT);


        return String.format(Locale.US, "%d h, %d m",
                diff.getStandardHours(),
                diff.getStandardMinutes() % 60);
    }

    public int getCreatedAgoMinutes() {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
        Date date;
        try {
            date = format.parse(created_at);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        GregorianCalendar current = new GregorianCalendar();
        GregorianCalendar created = new GregorianCalendar();
        created.setTime(date);

        DateTime currentDT = new DateTime(current.getTimeInMillis());
        DateTime createdDT = new DateTime(created.getTimeInMillis());
        return new Duration(createdDT, currentDT).toStandardMinutes().getMinutes();
    }

    public String getCreated_at() {
        return created_at;
    }

    public User getUser() {
        return user;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return user.name;
    }

    public String getScreenName() {
        return user.screen_name;
    }

    public String getProfileImageUrl() {
        return user.profile_image_url_https;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int compareTo(@NonNull Tweet o) {
        return getCreatedAgoMinutes() - o.getCreatedAgoMinutes();
    }
}
