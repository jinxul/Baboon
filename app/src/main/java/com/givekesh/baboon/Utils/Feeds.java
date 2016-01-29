package com.givekesh.baboon.Utils;

import android.os.Parcel;
import android.os.Parcelable;


public class Feeds implements Parcelable {

    private String ContentImage;
    private String Author;
    private String Date;
    private String Title;
    private String Excerpt;
    private int id;
    private String Post;

    public String getPost() {
        return Post;
    }

    public void setPost(String post) {
        Post = post;
    }

    /*
    public int getId() {
        return id;
    }*/

    public void setId(int id) {
        this.id = id;
    }

    public String getExcerpt() {
        return Excerpt;
    }

    public void setExcerpt(String excerpt) {
        Excerpt = excerpt;
    }

    public String getContentImage() {
        return ContentImage;
    }

    public void setContentImage(String contentImage) {
        ContentImage = contentImage;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }


    public Feeds() {

    }

    private Feeds(Parcel in) {
        ContentImage = in.readString();
        Author = in.readString();
        Date = in.readString();
        Title = in.readString();
        Excerpt = in.readString();
        id = in.readInt();
        Post = in.readString();
    }

    public static final Creator<Feeds> CREATOR = new Creator<Feeds>() {
        @Override
        public Feeds createFromParcel(Parcel in) {
            return new Feeds(in);
        }

        @Override
        public Feeds[] newArray(int size) {
            return new Feeds[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ContentImage);
        parcel.writeString(Author);
        parcel.writeString(Date);
        parcel.writeString(Title);
        parcel.writeString(Excerpt);
        parcel.writeInt(id);
        parcel.writeString(Post);
    }
}
