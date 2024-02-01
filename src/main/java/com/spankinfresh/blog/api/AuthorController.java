package com.spankinfresh.blog.api;

import com.spankinfresh.blog.data.AuthorRepository;
import com.spankinfresh.blog.domain.Author;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {
    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @PostMapping
    public ResponseEntity<Author> createAuthor(
            @Valid @RequestBody Author author, UriComponentsBuilder uriComponentsBuilder) {
        Author savedAuthor = authorRepository.save(author);

        HttpHeaders headers = new HttpHeaders();
        UriComponents uriComponents = uriComponentsBuilder.path("/api/authors/{id}").buildAndExpand(savedAuthor.getId());
        headers.add("Location", uriComponents.toUri().toString());

        return new ResponseEntity<>(savedAuthor, headers, HttpStatus.CREATED);
    }

    @GetMapping
    public Iterable<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Iterable<Author>> getBlogEntryById(@PathVariable Long id) {
        Optional<Author> blogEntry = authorRepository.findById(id);
        if(blogEntry.isPresent()) {
            return new ResponseEntity<>(Collections.singletonList(blogEntry.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("{id}")
    public ResponseEntity<Iterable<Author>> putAuthorById(@PathVariable Long id, @RequestBody Author author) {
        if(id != author.getId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        boolean exists = authorRepository.existsById(id);
        if(exists) {
            authorRepository.save(author);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Iterable<Author>> deleteAuthorById(@PathVariable Long id) {
        Optional<Author> author = authorRepository.findById(id);
        if(author.isPresent()) {
            authorRepository.delete(author.get());
            return new ResponseEntity<>(Collections.singletonList(author.get()), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
