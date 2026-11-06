package com.WD08.WeddingMangementSystem.review.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Review & Feedback pages (Thymeleaf templates in resources/templates/review-*.html). */
@Controller
public class ReviewPageController {

    @GetMapping({"/com/WD08/WeddingMangementSystem/review", "/com/WD08/WeddingMangementSystem/review/"})
    public String home() { return "review-home"; }

    @GetMapping("/com/WD08/WeddingMangementSystem/review/packages")
    public String packageReviews() { return "review-packages"; }

    @GetMapping("/com/WD08/WeddingMangementSystem/review/write")
    public String write() { return "review-write"; }

    @GetMapping("/com/WD08/WeddingMangementSystem/review/mine")
    public String myReviews() { return "review-mine"; }

    @GetMapping("/com/WD08/WeddingMangementSystem/review/edit")
    public String edit() { return "review-edit"; }

    @GetMapping("/com/WD08/WeddingMangementSystem/review/admin")
    public String admin() { return "review-admin"; }
}
