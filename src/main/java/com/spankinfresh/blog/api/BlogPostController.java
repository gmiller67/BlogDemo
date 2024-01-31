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
import java.util.Collections;
import java.util.Optional;

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

    @GetMapping("{id}")
    public ResponseEntity<Iterable<BlogPost>> getBlogEntryById(@PathVariable Long id) {
        Optional<BlogPost> blogEntry = blogPostRepository.findById(id);
        if(blogEntry.isPresent()) {
            return new ResponseEntity<>(Collections.singletonList(blogEntry.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("{id}")
    public ResponseEntity<Iterable<BlogPost>> putBlogEntryById(@PathVariable Long id, @RequestBody BlogPost post) {
        if(id != post.getId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        boolean exists = blogPostRepository.existsById(id);
        if(exists) {
            blogPostRepository.save(post);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
