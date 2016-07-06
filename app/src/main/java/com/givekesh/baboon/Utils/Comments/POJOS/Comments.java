package com.givekesh.baboon.Utils.Comments.POJOS;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Comments {
    public final int id;
    public final int parent_id;
    public final String date;
    public final String content;
    public final Author_info author_info;

    @JsonCreator
    public Comments(@JsonProperty("id") int id, @JsonProperty("parent") int parent,
                    @JsonProperty("date") String date, @JsonProperty("content") String content,
                    @JsonProperty("author_info") Author_info author_info) {
        this.id = id;
        this.parent_id = parent;
        this.date = date;
        this.content = content;
        this.author_info = author_info;
    }


    public static final class Author_info {
        public final String display_name;
        public final String author_avatar;

        public Author_info(@JsonProperty("display_name") String display_name,
                           @JsonProperty("avatar_url") String author_avatar) {
            this.display_name = display_name;
            this.author_avatar = author_avatar;
        }
    }
}
