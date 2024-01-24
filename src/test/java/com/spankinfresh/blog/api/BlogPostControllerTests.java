package com.spankinfresh.blog.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spankinfresh.blog.data.BlogPostRepository;
import com.spankinfresh.blog.domain.BlogPost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
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
    private static final BlogPost savedPosting =
            new BlogPost(1L, "category", null, "title", "content");
    @MockBean
    private BlogPostRepository mockRepository;

    @Test
    @DisplayName("Post accepts and returns blog post representation")
    public void postCreatesNewBlogEntry(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.save(refEq(testPosting, "datePosted"))).thenReturn(savedPosting);
        MockHttpServletRequestBuilder post = post(RESOURCE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testPosting));
        MvcResult result = mockMvc.perform(post)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedPosting.getId()))
                .andExpect(jsonPath("$.title").value(savedPosting.getTitle()))
                .andExpect(jsonPath("$.category").value(savedPosting.getCategory()))
                .andExpect(jsonPath("$.content").value(savedPosting.getContent()))
// todo: is this really testing anything?
//                .andExpect(jsonPath("$.datePosted").value(savedPosting.getDatePosted().toString()))
                .andReturn();

        assertEquals(
                String.format("http://localhost/api/articles/%d", savedPosting.getId()),
                result.getResponse().getHeader("Location"));
        verify(mockRepository, times(1)).save(refEq(testPosting, "datePosted"));
        verifyNoMoreInteractions(mockRepository);
    }
}
