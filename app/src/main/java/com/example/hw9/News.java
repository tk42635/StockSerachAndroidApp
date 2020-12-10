package com.example.hw9;

import androidx.annotation.DrawableRes;

import java.util.Date;

final public class News {
    String title, source, url, urlToImage;
    String image;
    Date date;

    public News(String title, String source, String url, Date date, String image) {
        this.title = title;
        this.source = source;
        this.url = url;
        this.date = date;
        this.date = date;
        this.image = image;
    }
}
