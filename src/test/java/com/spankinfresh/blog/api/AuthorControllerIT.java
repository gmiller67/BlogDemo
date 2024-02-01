package com.spankinfresh.blog.api;

import com.spankinfresh.blog.domain.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthorControllerIT {
    @LocalServerPort
    private int localServerPort;
    @Autowired
    private TestRestTemplate restTemplate;
    private static final String RESOURCE_URI = "http://localhost:%d/api/authors";
    private static final Author testPosting =
            new Author(0L, "first", "last", "email@cscc.edu");
    private static final Author savedPosting =
            new Author(1L, "first", "last", "email@cscc.edu");

    @Test
    @DisplayName("POST Location includes server port")
    public void test01() {
        ResponseEntity<Author> responseEntity =
                this.restTemplate.postForEntity(String.format(RESOURCE_URI, localServerPort), testPosting, Author.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(localServerPort, responseEntity.getHeaders().getLocation().getPort());
    }

    @Test
    @DisplayName("POST generates nonzero ID")
    public void test02() {
        ResponseEntity<Author> responseEntity =
                this.restTemplate.postForEntity(String.format(RESOURCE_URI, localServerPort), testPosting, Author.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        Author actualPost = responseEntity.getBody();
        assertNotEquals(testPosting.getId(), actualPost.getId());
        assertEquals(String.format(RESOURCE_URI + "/%d", localServerPort, actualPost.getId()),
                responseEntity.getHeaders().getLocation().toString());
    }

    @Test
    @DisplayName("POST automatically adds datePosted")
    public void test03() {
        ResponseEntity<Author> responseEntity =
                this.restTemplate.postForEntity(String.format(RESOURCE_URI, localServerPort), testPosting, Author.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

}
