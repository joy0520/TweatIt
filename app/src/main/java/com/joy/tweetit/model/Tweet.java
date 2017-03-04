package com.joy.tweetit.model;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by joy0520 on 2017/3/3.
 */

public class Tweet {
    class User {
        String name;
        String screen_name;
        String profile_image_url_https;
    }

    private String created_at;
    private String text;
    private long id;
    private User user;

    public String getText() {
        return text;
    }

    public String getCreated_at() {
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
}
