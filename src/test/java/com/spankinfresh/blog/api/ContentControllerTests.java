package com.spankinfresh.blog.api;

import com.spankinfresh.blog.data.BlogPostJdbcTemplateRepository;
import com.spankinfresh.blog.domain.BlogPost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.spankinfresh.blog.domain.Category;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ContentControllerTests {
   @MockBean
   BlogPostJdbcTemplateRepository mockRepository;
   private static final BlogPost savedPosting = new BlogPost(1L,
      "category", LocalDateTime.now(), "title", null, null);

   @Test
   @DisplayName("T01 - Get summary articles returns data ")
   public void test_01(@Autowired MockMvc mockMvc) throws Exception {
      when(mockRepository.getAllBlogPostingsOmittingContent()).thenReturn(Collections.singletonList(savedPosting));

      ResultActions result = mockMvc.perform(get("/api/summary/articles"));

      result
         .andExpect(jsonPath("$.length()").value(1))
         .andExpect(content().contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isOk());
      verify(mockRepository, times(1))
         .getAllBlogPostingsOmittingContent();
      verifyNoMoreInteractions(mockRepository);
   }

   @Test
   @DisplayName("T02 - Get categories returns data ")
   public void test_02(@Autowired MockMvc mockMvc) throws Exception {
      when(mockRepository.getCategoryList()).thenReturn(Collections.singletonList(new Category()));

      mockMvc.perform(get("/api/categories"))
              .andExpect(jsonPath("$.length()").value(1))
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk());

      verify(mockRepository, times(1)).getCategoryList();
      verifyNoMoreInteractions(mockRepository);
   }

}
