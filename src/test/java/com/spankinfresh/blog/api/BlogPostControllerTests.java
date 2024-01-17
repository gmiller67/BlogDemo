package com.spankinfresh.blog.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogPostControllerTests {
    @Test
    @DisplayName("Post returns status code CREATED")
    public void test01(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(post("/api/articles")).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Post returns Location header")
    public void test02(@Autowired MockMvc mockMvc) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/articles")).andReturn();

        assertEquals("http://localhost/api/articles/1", result.getResponse().getHeader("Location"));
    }
}
