package com.spankinfresh.blog.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spankinfresh.blog.data.AuthorRepository;
import com.spankinfresh.blog.domain.Author;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorControllerTests {

    public static final String RESOURCE_URI = "/api/authors";
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Author testAuthor =
            new Author(0L, "first", "last", "email@cscc.edu");
    private static final Author savedAuthor =
            new Author(1L, "first", "last", "email@cscc.edu");
    @MockBean
    private AuthorRepository mockRepository;

    @Test
    @DisplayName("Post accepts and returns author representation")
    public void postCreatesNewAuthor(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.save(refEq(testAuthor))).thenReturn(savedAuthor);
        MockHttpServletRequestBuilder post = post(RESOURCE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testAuthor));
        MvcResult result = mockMvc.perform(post)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedAuthor.getId()))
                .andExpect(jsonPath("$.firstName").value(savedAuthor.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(savedAuthor.getLastName()))
                .andExpect(jsonPath("$.emailAddress").value(savedAuthor.getEmailAddress()))
                .andReturn();

        assertEquals(
                String.format("http://localhost/api/authors/%d", savedAuthor.getId()),
                result.getResponse().getHeader("Location"));
        verify(mockRepository, times(1)).save(refEq(testAuthor));
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    @DisplayName("Post returns 400 if required properties are not set")
    public void postReturns400MissingFields(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder post = post(RESOURCE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Author(0L, null, null, null)));
        mockMvc.perform(post)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.firstName").value("must not be null"))
                .andExpect(jsonPath("$.fieldErrors.lastName").value("must not be null"))
                .andExpect(jsonPath("$.fieldErrors.emailAddress").value("must not be null"))
                .andReturn();

        verifyNoInteractions(mockRepository);
    }

    @Test
    @DisplayName("Post returns 400 if required properties are not the right length")
    public void postReturns400ForLength(@Autowired MockMvc mockMvc) throws Exception {
        MockHttpServletRequestBuilder post = post(RESOURCE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Author(0L, "", "", "")));
        mockMvc.perform(post)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.firstName").value("First name should be between 1 and 80 characters"))
                .andExpect(jsonPath("$.fieldErrors.lastName").value("Last name should be between 1 and 80 characters"))
                .andReturn();

        verifyNoInteractions(mockRepository);
    }

    @Test
    @DisplayName("GET all returns empty list if no authors")
    void getAllReturnsNoAuthors(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findAll()).thenReturn(new ArrayList<>());
        mockMvc.perform(get(RESOURCE_URI))
                .andExpect(jsonPath("$.length()").value(0))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(mockRepository, times(1)).findAll();
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    @DisplayName("GET all returns a list with a author")
    void getAllReturnsAuthors(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findAll()).thenReturn(Collections.singletonList(savedAuthor));
        mockMvc.perform(get(RESOURCE_URI))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(savedAuthor.getId()))
                .andExpect(jsonPath("$.[0].firstName").value(savedAuthor.getFirstName()))
                .andExpect(jsonPath("$.[0].lastName").value(savedAuthor.getLastName()))
                .andExpect(jsonPath("$.[0].emailAddress").value(savedAuthor.getEmailAddress()))
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
    @DisplayName("GET by ID returns a author")
    void getByIdReturnsAuthor(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(savedAuthor));
        mockMvc.perform(get(RESOURCE_URI + "/1"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(savedAuthor.getId()))
                .andExpect(jsonPath("$.[0].firstName").value(savedAuthor.getFirstName()))
                .andExpect(jsonPath("$.[0].lastName").value(savedAuthor.getLastName()))
                .andExpect(jsonPath("$.[0].emailAddress").value(savedAuthor.getEmailAddress()))
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
                .content(mapper.writeValueAsString(new Author(1L, "first", "last", "email@cscc.edu")));
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());

        verify(mockRepository, times(1)).existsById(anyLong());
        verify(mockRepository, times(1)).save(any(Author.class));
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    @DisplayName("PUT by ID returns not found if not exists")
    void putByIdReturnsNotFound(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.existsById(anyLong())).thenReturn(false);
        MockHttpServletRequestBuilder putRequest = put(RESOURCE_URI + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Author(1L, "first", "last", "email@cscc.edu")));
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
                .content(mapper.writeValueAsString(new Author(1L, "first", "last", "email@cscc.edu")));
        mockMvc.perform(putRequest)
                .andExpect(status().isConflict());

        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    @DisplayName("DELETE by ID deletes from database")
    void deleteByIdDeletes(@Autowired MockMvc mockMvc) throws Exception {
        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(savedAuthor));
        mockMvc.perform(delete(RESOURCE_URI + "/1"))
                .andExpect(status().isNoContent());

        verify(mockRepository, times(1)).findById(anyLong());
        verify(mockRepository, times(1)).delete(any(Author.class));
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
}
