package com.spankinfresh.blog.api;

import com.spankinfresh.blog.domain.BlogPost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BlogPostControllerIT {
    @LocalServerPort
    private int localServerPort;
    @Autowired
    private TestRestTemplate restTemplate;
    private static final String RESOURCE_URI = "http://localhost:%d/api/articles";
    private static final BlogPost testPosting =
            new BlogPost(0L, "category", null, "title", "content");
    private static final BlogPost savedPosting =
            new BlogPost(1L, "category", LocalDateTime.now(), "title", "content");

    @Test
    @DisplayName("POST Location includes server port")
    public void test01() {
        ResponseEntity<BlogPost> responseEntity =
                this.restTemplate.postForEntity(String.format(RESOURCE_URI, localServerPort), testPosting, BlogPost.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(localServerPort, responseEntity.getHeaders().getLocation().getPort());
    }

    @Test
    @DisplayName("POST generates nonzero ID")
    public void test02() {
        ResponseEntity<BlogPost> responseEntity =
                this.restTemplate.postForEntity(String.format(RESOURCE_URI, localServerPort), testPosting, BlogPost.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        BlogPost actualPost = responseEntity.getBody();
        assertNotEquals(testPosting.getId(), actualPost.getId());
        assertEquals(String.format(RESOURCE_URI + "/%d", localServerPort, actualPost.getId()),
                responseEntity.getHeaders().getLocation().toString());
    }

    @Test
    @DisplayName("POST automatically adds datePosted")
    public void test03() {
        ResponseEntity<BlogPost> responseEntity =
                this.restTemplate.postForEntity(String.format(RESOURCE_URI, localServerPort), testPosting, BlogPost.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody().getDatePosted());
    }

}
