package com.spankinfresh.blog.ui;

import com.spankinfresh.blog.data.BlogPostJdbcTemplateRepository;
import com.spankinfresh.blog.domain.BlogPost;
import com.spankinfresh.blog.domain.Category;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice(basePackages = "com.spankinfresh.blog.ui")
public class CommonModelAttributeProvider {
    private final BlogPostJdbcTemplateRepository blogPostJdbcTemplateRepository;
    private final MarkdownFormatter markdownFormatter;

    public CommonModelAttributeProvider(
            BlogPostJdbcTemplateRepository blogPostJdbcTemplateRepository, MarkdownFormatter markdownFormatter) {
        this.blogPostJdbcTemplateRepository = blogPostJdbcTemplateRepository;
        this.markdownFormatter = markdownFormatter;
    }

    @ModelAttribute("categoryList")
    List<Category> getAllCategories() {
        return blogPostJdbcTemplateRepository.getCategoryList();
    }

    @ModelAttribute("articlesList")
    public List<BlogPost> getAll() {
        return blogPostJdbcTemplateRepository.getAllBlogPostingsOmittingContent();
    }

    @ModelAttribute("markdownFormatter")
    public MarkdownFormatter getMarkdownFormatter() {
        return markdownFormatter;
    }
}