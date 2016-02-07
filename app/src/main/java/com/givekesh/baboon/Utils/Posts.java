package com.givekesh.baboon.Utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Posts {

    public final int id;
    public final String date;
    public final Title title;
    public final Excerpt excerpt;
    public final int author;
    public final Content content;
    public final Better_featured_image better_featured_image;

    @JsonCreator
    public Posts(@JsonProperty("id") int id, @JsonProperty("date") String date, @JsonProperty("title") Title title, @JsonProperty("excerpt") Excerpt excerpt, @JsonProperty("author") int author, @JsonProperty("content") Content content, @JsonProperty("better_featured_image") Better_featured_image better_featured_image) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.excerpt = excerpt;
        this.author = author;
        this.content = content;
        this.better_featured_image = better_featured_image;
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

    public static final class Better_featured_image {

        public final String source_url;

        @JsonCreator
        public Better_featured_image(@JsonProperty("source_url") String source_url) {
            this.source_url = source_url;
        }
    }
}