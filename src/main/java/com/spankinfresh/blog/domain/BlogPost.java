package com.spankinfresh.blog.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime datePosted;
    @NotNull
    @Size(min = 1, max = 200, message = "Please enter a category name of up to 200 characters")
    private String category;
    @NotNull
    @Size(min = 1, max = 200, message = "Please enter a title up to 200 characters in length")
    private String title;
    @NotNull
    @Size(min = 1, max = 500000, message = "Content is required")
    private String content;

    public BlogPost() {
    }

    public BlogPost(long id, String category, LocalDateTime datePosted, String title, String content) {
        this.id = id;
        this.category = category;
        this.datePosted = datePosted;
        this.title = title;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "BlogPost{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", datePosted=" + datePosted +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
