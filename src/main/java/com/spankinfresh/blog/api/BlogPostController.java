package com.spankinfresh.blog.api;

import com.spankinfresh.blog.data.BlogPostRepository;
import com.spankinfresh.blog.domain.BlogPost;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/articles")
public class BlogPostController {
    private final BlogPostRepository blogPostRepository;

    public BlogPostController(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @PostMapping
    public ResponseEntity<BlogPost> createBlogEntry(
            @RequestBody BlogPost blogPost, UriComponentsBuilder uriComponentsBuilder) {
        blogPost.setDatePosted(LocalDateTime.now());
        BlogPost savedPost = blogPostRepository.save(blogPost);

        HttpHeaders headers = new HttpHeaders();
        UriComponents uriComponents = uriComponentsBuilder.path("/api/articles/{id}").buildAndExpand(savedPost.getId());
        headers.add("Location", uriComponents.toUri().toString());

        return new ResponseEntity<>(savedPost, headers, HttpStatus.CREATED);
    }

    @GetMapping
    public Iterable<BlogPost> getAllBlogEntries() {
        return blogPostRepository.findAll();
    }
}
