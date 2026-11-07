package com.WD08.WeddingMangementSystem.review.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Review & Feedback pages (Thymeleaf templates in resources/templates/review-*.html). */
@Controller
public class ReviewPageController {

    @GetMapping({"/review", "/review/"})
    public String home() { return "review-home"; }

    @GetMapping("/review/packages")
    public String packageReviews() { return "review-packages"; }

    @GetMapping("/review/write")
    public String write() { return "review-write"; }

    @GetMapping("/review/mine")
    public String myReviews() { return "review-mine"; }

    @GetMapping("/review/edit")
    public String edit() { return "review-edit"; }

    @GetMapping("/review/admin")
    public String admin() { return "review-admin"; }
}
