package com.givekesh.baboon.Utils.Posts.POJOS;

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
    private String Author_avatar;
    private int comments_count;

    public String getAuthor_avatar() {
        return Author_avatar;
    }

    public void setAuthor_avatar(String author_avatar) {
        Author_avatar = author_avatar;
    }

    public String getPost() {
        return Post;
    }

    public void setPost(String post) {
        Post = post;
    }

    public int getId() {
        return id;
    }

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

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
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
        Author_avatar = in.readString();
        comments_count = in.readInt();
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
        parcel.writeString(Author_avatar);
        parcel.writeInt(comments_count);
    }
}
