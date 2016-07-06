package com.givekesh.baboon.Utils.Comments.POJOS;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {

    private int id;
    private int parent_id;
    private int padding;
    private String date;
    private String content;
    private String display_name;
    private String author_avatar;

    public Comment() {
    }

    protected Comment(Parcel in) {
        id = in.readInt();
        parent_id = in.readInt();
        padding = in.readInt();
        date = in.readString();
        content = in.readString();
        display_name = in.readString();
        author_avatar = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getAuthor_avatar() {
        return author_avatar;
    }

    public void setAuthor_avatar(String author_avatar) {
        this.author_avatar = author_avatar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(parent_id);
        parcel.writeInt(padding);
        parcel.writeString(date);
        parcel.writeString(content);
        parcel.writeString(display_name);
        parcel.writeString(author_avatar);
    }
}
