package com.givekesh.baboon.Utils.Posts.POJOS;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Posts {

    public final int id;
    public final String date;
    public final Title title;
    public final Excerpt excerpt;
    public final Content content;
    public final Post_Image image;
    public final Author_info author_info;

    @JsonCreator
    public Posts(@JsonProperty("id") int id, @JsonProperty("date") String date, @JsonProperty("title") Title title,
                 @JsonProperty("excerpt") Excerpt excerpt,
                 @JsonProperty("content") Content content, @JsonProperty("image") Post_Image image,
                 @JsonProperty("author_info") Author_info author_info) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.excerpt = excerpt;
        this.content = content;
        this.image = image;
        this.author_info = author_info;
    }

    public static final class Title {
        public final String rendered;

        @JsonCreator
        public Title(@JsonProperty("rendered") String rendered) {
            this.rendered = rendered;
        }
    }

    public static final class Excerpt {
        public final String rendered;

        @JsonCreator
        public Excerpt(@JsonProperty("rendered") String rendered) {
            this.rendered = rendered;
        }
    }

    public static final class Content {
        public final String rendered;

        @JsonCreator
        public Content(@JsonProperty("rendered") String rendered) {
            this.rendered = rendered;
        }
    }

    public static final class Post_Image {
        public final String source_url;

        @JsonCreator
        public Post_Image(@JsonProperty("source_url") String source_url) {
            this.source_url = source_url;
        }
    }

    public static final class Author_info {
        public final int author_id;
        public final String display_name;
        public final String author_avatar;

        public Author_info(@JsonProperty("id") int author_id, @JsonProperty("display_name") String display_name,
                           @JsonProperty("avatar_url") String author_avatar) {
            this.author_id = author_id;
            this.display_name = display_name;
            this.author_avatar = author_avatar;
        }
    }
}