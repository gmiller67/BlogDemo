package com.spankinfresh.blog.ui;

import com.spankinfresh.blog.data.BlogPostJdbcTemplateRepository;
import com.spankinfresh.blog.data.BlogPostRepository;
import com.spankinfresh.blog.domain.Author;
import com.spankinfresh.blog.domain.BlogPost;
import com.spankinfresh.blog.domain.Category;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure
        .web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WebContentControllerTests {
    // In WebContentControllerTests
    @MockBean
    private BlogPostRepository blogPostRepository;
    @MockBean
    private BlogPostJdbcTemplateRepository blogPostJdbcTemplateRepository;

    private static final Author savedAuthor =
            new Author(1L, "Jane", "Doe", "jane@doe.com");
    private static final BlogPost testPosting =
            new BlogPost(0L, "category", null, "title", "content",
                    savedAuthor);
    private static final Collection<BlogPost> emptyList = new ArrayList<>();
    private static final Collection<BlogPost> populatedList =
            Collections.singletonList(testPosting);
    private static final List<BlogPost> tocArticlesList =
            Collections.singletonList(testPosting);

    @Test
    @DisplayName("T01: GET to / returns index view")
    public void test01(@Autowired MockMvc mockMvc)
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("index"));
    }

    @Test
    @DisplayName("T02: index view has articles model data")
    public void test02(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("articles"))
                .andExpect(MockMvcResultMatchers.view().name("index"));
    }

    @Test
    @DisplayName("T03: index view model has list of articles")
    public void test03(@Autowired MockMvc mockMvc) throws Exception {
        when(blogPostRepository.findAll()).thenReturn(emptyList);
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("articles"))
                .andExpect(MockMvcResultMatchers.model().attribute("articles", equalTo(emptyList)))
                .andExpect(MockMvcResultMatchers.view().name("index"));
        verify(blogPostRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("T04: index view returns populated list of articles")
    public void test04(@Autowired MockMvc mockMvc) throws Exception {
        when(blogPostRepository.findAll()).thenReturn(populatedList);
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.model()
                        .attribute("articles", equalTo(populatedList)));
        verify(blogPostRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("T05: index model has instanceOf MarkdownFormatter")
    public void test05(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.model()
                        .attributeExists("markdownFormatter"))
                .andExpect(MockMvcResultMatchers.model()
                        .attribute("markdownFormatter",
                                instanceOf(MarkdownFormatter.class)));
    }

    @Test
    @DisplayName("T06: index view includes category list")
    public void test06(@Autowired MockMvc mockMvc) throws Exception {
        List<Category> categoryList = Collections.singletonList(new Category());
        when(blogPostJdbcTemplateRepository.getCategoryList()).thenReturn(categoryList);

        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.model().attribute("categoryList", equalTo(categoryList)));

        verify(blogPostJdbcTemplateRepository, times(1)).getCategoryList();
    }

    @Test
    @DisplayName("T07: index view includes article list")
    public void test07(@Autowired MockMvc mockMvc) throws Exception {
        when(blogPostJdbcTemplateRepository.getAllBlogPostingsOmittingContent())
                .thenReturn(tocArticlesList);
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.model()
                        .attribute("articlesList", equalTo(tocArticlesList)));
        verify(blogPostJdbcTemplateRepository,
                times(1)).getAllBlogPostingsOmittingContent();
    }

    @Test
    @DisplayName("T08: GET to /articles returns toc view")
    public void test08(@Autowired MockMvc mockMvc)
            throws Exception {
        when(blogPostJdbcTemplateRepository.getAllBlogPostingsOmittingContent())
                .thenReturn(tocArticlesList);
        mockMvc.perform(MockMvcRequestBuilders.get("/articles"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute(
                        "articlesList", equalTo(tocArticlesList)))
                .andExpect(MockMvcResultMatchers.view().name("toc"));
    }

    @Test
    @DisplayName("T09: get by valid category works")
    public void test09(@Autowired MockMvc mockMvc) throws Exception {
        when(blogPostRepository.findByCategoryOrderByDatePostedDesc("foo"))
                .thenReturn(tocArticlesList);
        mockMvc.perform(MockMvcRequestBuilders.get("/category")
                        .param("categoryName", "foo"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute(
                        "articles", equalTo(tocArticlesList)))
                .andExpect(MockMvcResultMatchers.model().attribute(
                        "categoryName", equalTo("foo")))
                .andExpect(MockMvcResultMatchers.model().attribute(
                        "pageTitle", equalTo("foo")))
                .andExpect(MockMvcResultMatchers.view().name("index"));
    }

    @Test
    @DisplayName("T10: get by invalid category works")
    public void test10(@Autowired MockMvc mockMvc)
            throws Exception {
        when(blogPostRepository.findByCategoryOrderByDatePostedDesc("foo"))
                .thenReturn(new ArrayList<>());
        mockMvc.perform(MockMvcRequestBuilders.get("/category")
                        .param("categoryName", "foo"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("error/404"));
    }

    @Test
    @DisplayName("T11: get by valid ID works")
    public void test11 (@Autowired MockMvc mockMvc)
            throws Exception {
        when(blogPostRepository.findById(1L))
                .thenReturn(Optional.of(testPosting));
        mockMvc.perform(MockMvcRequestBuilders.get("/article/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute(
                        "articles", Matchers.contains(testPosting)))
                .andExpect(MockMvcResultMatchers.model().attribute(
                        "pageTitle", equalTo(testPosting.getTitle())))
                .andExpect(MockMvcResultMatchers.view().name("index"));
    }

    @Test
    @DisplayName("T12: get by invalid ID works")
    public void test12 (@Autowired MockMvc mockMvc)
            throws Exception {
        when(blogPostRepository.findById(1L))
                .thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/article/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("error/404"));
    }
}
