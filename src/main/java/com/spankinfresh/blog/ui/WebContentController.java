package com.spankinfresh.blog.ui;

import com.spankinfresh.blog.data.BlogPostRepository;
import com.spankinfresh.blog.domain.BlogPost;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class WebContentController {
    private final BlogPostRepository blogPostRepository;

    public WebContentController(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @GetMapping("/")
    public String getIndexPage(Model model) {
        model.addAttribute("articles", blogPostRepository.findAll());

        return "index";
    }

    @GetMapping("/articles")
    public String getAllArticlesPage(Model model) {
        return "toc";
    }

    @GetMapping("/category")
    public String getAllBlogPostsByCategory(
            Model model, @RequestParam("categoryName") String categoryName) {
        List<BlogPost> results =
                blogPostRepository.findByCategoryOrderByDatePostedDesc(categoryName);
        if (results.isEmpty()) {
            return "error/404";
        }
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("pageTitle", categoryName);
        model.addAttribute("articles", results);
        return "index";
    }

    @GetMapping("/article/{id}")
    public String getArticleById(@PathVariable Long id, Model model) {
        Optional<BlogPost> searchResult = blogPostRepository.findById(id);
        if (searchResult.isPresent()) {
            model.addAttribute("pageTitle", searchResult.get().getTitle());
            model.addAttribute("articles",
                    Collections.singletonList(searchResult.get()));
            return "index";
        }
        return "error/404";
    }
}