package com.joy.tweetit.model;

import com.joy.tweetit.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by joy0520 on 2017/3/5.
 */

@Table(database = MyDatabase.class)
public class User extends BaseModel {

    @PrimaryKey
    @Column
    String name;
    @Column
    String screen_name;
    @Column
    String profile_image_url_https;

    public String getName() {
        return name;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public String getProfile_image_url_https() {
        return profile_image_url_https;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public void setProfile_image_url_https(String profile_image_url_https) {
        this.profile_image_url_https = profile_image_url_https;
    }
}
