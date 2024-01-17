package com.spankinfresh.blog.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spankinfresh.blog.domain.BlogPost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogPostControllerTests {

    public static final String RESOURCE_URI = "/api/articles";
    private final ObjectMapper mapper = new ObjectMapper();
    private static final BlogPost testPosting =
            new BlogPost(0L, "category", null, "title", "content");

    @Test
    @DisplayName("Post accepts and returns blog post representation")
    public void postCreatesNewBlogEntry(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder post = post(RESOURCE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testPosting));
        MvcResult result = mockMvc.perform(post)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testPosting.getId()))
                .andExpect(jsonPath("$.title").value(testPosting.getTitle()))
                .andExpect(jsonPath("$.category").value(testPosting.getCategory()))
                .andExpect(jsonPath("$.content").value(testPosting.getContent()))
                .andReturn();

        assertEquals(
                String.format("http://localhost/api/articles/%d", testPosting.getId()),
                result.getResponse().getHeader("Location"));
    }

    @Test
    @DisplayName("Post returns status code CREATED")
    public void test01(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(post(RESOURCE_URI)).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Post returns Location header")
    public void test02(@Autowired MockMvc mockMvc) throws Exception {
        MvcResult result = mockMvc.perform(post(RESOURCE_URI)).andReturn();

        assertEquals("http://localhost/api/articles/1", result.getResponse().getHeader("Location"));
    }
}
