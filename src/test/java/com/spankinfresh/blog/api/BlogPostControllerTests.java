package com.spankinfresh.blog.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spankinfresh.blog.data.BlogPostRepository;
import com.spankinfresh.blog.domain.Author;
import com.spankinfresh.blog.domain.BlogPost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogPostControllerTests {

    public static final String RESOURCE_URI = "/api/articles";
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Author savedAuthor =
            new Author(1L, "first", "last", "email@cscc.edu");
    private static final BlogPost testPosting =
            new BlogPost(0L, "category", null, "title", "content", savedAuthor);
    private static final BlogPost savedPosting =
            new BlogPost(1L, "category", LocalDateTime.now(), "title", "content", savedAuthor);
    @MockBean
    private BlogPostRepository mockRepository;

    @Test
    @DisplayName("Post accepts and returns blog post representation")
    public void postCreatesNewBlogEntry(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.save(refEq(testPosting, "datePosted", "author"))).thenReturn(savedPosting);
        MockHttpServletRequestBuilder post = post(RESOURCE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testPosting));
        MvcResult result = mockMvc.perform(post)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedPosting.getId()))
                .andExpect(jsonPath("$.title").value(savedPosting.getTitle()))
                .andExpect(jsonPath("$.category").value(savedPosting.getCategory()))
                .andExpect(jsonPath("$.content").value(savedPosting.getContent()))
//                .andExpect(jsonPath("$.datePosted").value(savedPosting.getDatePosted().toString()))
                .andExpect(jsonPath("$.author.id").value(savedPosting.getAuthor().getId()))
                .andReturn();

        assertEquals(
                String.format("http://localhost/api/articles/%d", savedPosting.getId()),
                result.getResponse().getHeader("Location"));
        verify(mockRepository, times(1)).save(refEq(testPosting, "datePosted", "author"));
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    @DisplayName("Post returns 400 if required properties are not set")
    public void postReturns400MissingFields(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder post = post(RESOURCE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new BlogPost()));
        mockMvc.perform(post)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.category").value("must not be null"))
                .andExpect(jsonPath("$.fieldErrors.title").value("must not be null"))
                .andExpect(jsonPath("$.fieldErrors.content").value("must not be null"))
                .andReturn();

        verifyNoInteractions(mockRepository);
    }

    @Test
    @DisplayName("Post returns 400 if required properties are not the right length")
    public void postReturns400ForLength(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder post = post(RESOURCE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new BlogPost(0L, "", null, "", "", savedAuthor)));
        mockMvc.perform(post)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.category").value("Please enter a category name of up to 200 characters"))
                .andExpect(jsonPath("$.fieldErrors.title").value("Please enter a title up to 200 characters in length"))
                .andExpect(jsonPath("$.fieldErrors.content").value("Content is required"))
                .andReturn();

        verifyNoInteractions(mockRepository);
    }

    @Test
    @DisplayName("GET all returns empty list if no posts")
    void getAllReturnsNoPosts(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findAll()).thenReturn(new ArrayList<>());
        mockMvc.perform(get(RESOURCE_URI))
                .andExpect(jsonPath("$.length()").value(0))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(mockRepository, times(1)).findAll();
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    @DisplayName("GET all returns a list with a blog post")
    void getAllReturnsPosts(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findAll()).thenReturn(Collections.singletonList(savedPosting));
        mockMvc.perform(get(RESOURCE_URI))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(savedPosting.getId()))
                .andExpect(jsonPath("$.[0].title").value(savedPosting.getTitle()))
                .andExpect(jsonPath("$.[0].category").value(savedPosting.getCategory()))
                .andExpect(jsonPath("$.[0].content").value(savedPosting.getContent()))
//                .andExpect(jsonPath("$.[0].datePosted").value(savedPosting.getDatePosted().toString()))
                .andExpect(status().isOk());

        verify(mockRepository, times(1)).findAll();
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    @DisplayName("GET by ID returns not found for invalid ID")
    void getByIdReturnsNotFound(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get(RESOURCE_URI + "/1"))
                .andExpect(status().isNotFound());

        verify(mockRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    @DisplayName("GET by ID returns a blog post")
    void getByIdReturnsPost(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(savedPosting));
        mockMvc.perform(get(RESOURCE_URI + "/1"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(savedPosting.getId()))
                .andExpect(jsonPath("$.[0].title").value(savedPosting.getTitle()))
                .andExpect(jsonPath("$.[0].category").value(savedPosting.getCategory()))
                .andExpect(jsonPath("$.[0].content").value(savedPosting.getContent()))
//                .andExpect(jsonPath("$.[0].datePosted").value(savedPosting.getDatePosted().toString()))
                .andExpect(status().isOk());

        verify(mockRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    @DisplayName("PUT by ID saves to database")
    void putByIdSavesToDatabase(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.existsById(anyLong())).thenReturn(true);
        MockHttpServletRequestBuilder putRequest = put(RESOURCE_URI + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new BlogPost(1L, "category", null, "title", "content", savedAuthor)));
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());

        verify(mockRepository, times(1)).existsById(anyLong());
        verify(mockRepository, times(1)).save(any(BlogPost.class));
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    @DisplayName("PUT by ID returns not found if not exists")
    void putByIdReturnsNotFound(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.existsById(anyLong())).thenReturn(false);
        MockHttpServletRequestBuilder putRequest = put(RESOURCE_URI + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new BlogPost(1L, "category", null, "title", "content", savedAuthor)));
        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());

        verify(mockRepository, times(1)).existsById(anyLong());
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    @DisplayName("PUT by ID returns conflict if URI ID does not match body ID")
    void putByIdReturnsConflict(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder putRequest = put(RESOURCE_URI + "/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new BlogPost(1L, "category", null, "title", "content", savedAuthor)));
        mockMvc.perform(putRequest)
                .andExpect(status().isConflict());

        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    @DisplayName("DELETE by ID deletes from database")
    void deleteByIdDeletes(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(savedPosting));
        mockMvc.perform(delete(RESOURCE_URI + "/1"))
                .andExpect(status().isNoContent());

        verify(mockRepository, times(1)).findById(anyLong());
        verify(mockRepository, times(1)).delete(any(BlogPost.class));
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    @DisplayName("DELETE by ID returns not found if not exists")
    void deleteByIdReturnsNotFound(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(delete(RESOURCE_URI + "/1"))
                .andExpect(status().isNotFound());

        verify(mockRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    @DisplayName("GET requests have proper CORS headers")
    void getIncludesCors(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get(RESOURCE_URI))
                .andExpect(status().isOk())
                .andExpect(
                        header().stringValues(HttpHeaders.VARY,
                                hasItems("Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"))
                );
    }

    @Test
    @DisplayName("T14 - Get by category name returns expected data")
    public void test_14 (@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findByCategoryOrderByDatePostedDesc("foo"))
                .thenReturn(Collections.singletonList(savedPosting));

        MockHttpServletRequestBuilder request = get(RESOURCE_URI + "/category")
                .param("categoryName", "foo");
        mockMvc.perform(request)
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(savedPosting.getId()))
                .andExpect(jsonPath("$.[0].title").value(savedPosting.getTitle()))
//                .andExpect(jsonPath("$.[0].datePosted").value(savedPosting.getDatePosted().toString()))
                .andExpect(jsonPath("$.[0].category").value(savedPosting.getCategory()))
                .andExpect(jsonPath("$.[0].content").value(savedPosting.getContent()))
                .andExpect(status().isOk());

        verify(mockRepository, times(1)).findByCategoryOrderByDatePostedDesc("foo");
        verifyNoMoreInteractions(mockRepository);
    }

}
