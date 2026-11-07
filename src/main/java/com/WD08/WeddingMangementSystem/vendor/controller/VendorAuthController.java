package com.WD08.WeddingMangementSystem.vendor.controller;

import com.WD08.WeddingMangementSystem.vendor.dto.VendorForm;
import com.WD08.WeddingMangementSystem.vendor.exception.VendorException;
import com.WD08.WeddingMangementSystem.vendor.model.Vendor;
import com.WD08.WeddingMangementSystem.vendor.service.VendorService;
import com.WD08.WeddingMangementSystem.vendor.util.VendorSessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vendor")
public class VendorAuthController {

    private final VendorService vendorService;

    public VendorAuthController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (VendorSessionUtil.currentVendorId(session) != null) {
            return "redirect:/vendor/workboard";
        }
        return "vendor-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {
        try {
            Vendor vendor = vendorService.login(email, password);
            VendorSessionUtil.login(session, vendor.getId());
            return "redirect:/vendor/workboard";
        } catch (VendorException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "vendor-login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new VendorForm());
        model.addAttribute("categories", vendorService.getCategories());
        return "vendor-register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("form") VendorForm form,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            vendorService.register(form);
            redirectAttributes.addFlashAttribute("success",
                    "Registration successful! Please log in.");
            return "redirect:/vendor/login";
        } catch (VendorException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", vendorService.getCategories());
            return "vendor-register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        VendorSessionUtil.logout(session);
        return "redirect:/vendor/login?loggedout";
    }
}
