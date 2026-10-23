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
@RequestMapping("/vendor/profile")

public class VendorProfileController {

    private final VendorService vendorService;

    public VendorProfileController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    // READ
    @GetMapping
    public String profile(HttpSession session, Model model) {
        Long vendorId = VendorSessionUtil.currentVendorId(session);
        if (vendorId == null) {
            return "redirect:/vendor/login";
        }
        try {
            Vendor vendor = vendorService.getById(vendorId);
            model.addAttribute("vendor", vendor);
            model.addAttribute("form", vendorService.toForm(vendor));
            return "vendor-profile";
        } catch (VendorException e) {
            VendorSessionUtil.logout(session);
            return "redirect:/vendor/login";
        }
    }

    // UPDATE
    @PostMapping("/update")
    public String update(@ModelAttribute("form") VendorForm form,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Long vendorId = VendorSessionUtil.currentVendorId(session);
        if (vendorId == null) {
            return "redirect:/vendor/login";
        }
        try {
            vendorService.updateProfile(vendorId, form);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
            return "redirect:/vendor/profile";
        } catch (VendorException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vendor", vendorService.getById(vendorId));
            return "vendor-profile";
        }
    }

    // POLYMORPHISM demo: quote calculator
    @PostMapping("/quote")
    public String quote(@RequestParam("units") int units,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        Long vendorId = VendorSessionUtil.currentVendorId(session);
        if (vendorId == null) {
            return "redirect:/vendor/login";
        }
        if (units < 1) {
            redirectAttributes.addFlashAttribute("error", "Enter a quantity of 1 or more.");
            return "redirect:/vendor/profile";
        }
        Vendor vendor = vendorService.getById(vendorId);
        double total = vendorService.calculateQuote(vendorId, units);
        redirectAttributes.addFlashAttribute("quote",
                "Estimated quote for " + units + " (" + vendor.getPricingLabel() + "): LKR "
                        + String.format("%,.2f", total));
        return "redirect:/vendor/profile";
    }

    // DELETE
    @PostMapping("/delete")
    public String delete(HttpSession session) {
        Long vendorId = VendorSessionUtil.currentVendorId(session);
        if (vendorId == null) {
            return "redirect:/vendor/login";
        }
        vendorService.deleteAccount(vendorId);
        VendorSessionUtil.logout(session);
        return "redirect:/vendor/login?deleted";
    }
}
