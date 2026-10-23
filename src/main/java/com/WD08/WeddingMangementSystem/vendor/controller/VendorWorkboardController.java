package com.WD08.WeddingMangementSystem.vendor.controller;

import com.WD08.WeddingMangementSystem.vendor.exception.VendorException;
import com.WD08.WeddingMangementSystem.vendor.model.Vendor;
import com.WD08.WeddingMangementSystem.vendor.model.VendorPackage;
import com.WD08.WeddingMangementSystem.vendor.service.PackageService;
import com.WD08.WeddingMangementSystem.vendor.service.VendorService;
import com.WD08.WeddingMangementSystem.vendor.util.VendorSessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/vendor")

public class VendorWorkboardController {

    private final VendorService vendorService;
    private final PackageService packageService;

    public VendorWorkboardController(VendorService vendorService, PackageService packageService) {
        this.vendorService = vendorService;
        this.packageService = packageService;
    }

    // READ: list all my packages
    @GetMapping("/workboard")
    public String workboard(HttpSession session, Model model) {
        Long vendorId = VendorSessionUtil.currentVendorId(session);
        if (vendorId == null) {
            return "redirect:/vendor/login";
        }
        try {
            Vendor vendor = vendorService.getById(vendorId);
            List<VendorPackage> packages = packageService.listForVendor(vendorId);
            model.addAttribute("vendor", vendor);
            model.addAttribute("packages", packages);
            return "vendor-workboard";
        } catch (VendorException e) {
            VendorSessionUtil.logout(session);
            return "redirect:/vendor/login";
        }
    }

    // CREATE: show empty form
    @GetMapping("/packages/new")
    public String newPackage(HttpSession session, Model model) {
        if (VendorSessionUtil.currentVendorId(session) == null) {
            return "redirect:/vendor/login";
        }
        return showForm(model, new VendorPackage(), "/vendor/packages/create", "Add New Package");
    }

    // CREATE: save
    @PostMapping("/packages/create")
    public String create(@ModelAttribute("pkg") VendorPackage pkg,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Long vendorId = VendorSessionUtil.currentVendorId(session);
        if (vendorId == null) {
            return "redirect:/vendor/login";
        }
        try {
            packageService.create(vendorId, pkg);
            redirectAttributes.addFlashAttribute("success", "Package added.");
            return "redirect:/vendor/workboard";
        } catch (VendorException e) {
            model.addAttribute("error", e.getMessage());
            return showForm(model, pkg, "/vendor/packages/create", "Add New Package");
        }
    }

    // UPDATE: show form with current values
    @GetMapping("/packages/{id}/edit")
    public String edit(@PathVariable("id") Long packageId,
                       HttpSession session,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        Long vendorId = VendorSessionUtil.currentVendorId(session);
        if (vendorId == null) {
            return "redirect:/vendor/login";
        }
        try {
            VendorPackage pkg = packageService.getOwned(packageId, vendorId);
            return showForm(model, pkg, "/vendor/packages/" + packageId + "/update", "Edit Package");
        } catch (VendorException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/vendor/workboard";
        }
    }

    // UPDATE: save
    @PostMapping("/packages/{id}/update")
    public String update(@PathVariable("id") Long packageId,
                         @ModelAttribute("pkg") VendorPackage pkg,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Long vendorId = VendorSessionUtil.currentVendorId(session);
        if (vendorId == null) {
            return "redirect:/vendor/login";
        }
        pkg.setId(packageId);
        try {
            packageService.update(vendorId, pkg);
            redirectAttributes.addFlashAttribute("success", "Package updated.");
            return "redirect:/vendor/workboard";
        } catch (VendorException e) {
            model.addAttribute("error", e.getMessage());
            return showForm(model, pkg, "/vendor/packages/" + packageId + "/update", "Edit Package");
        }
    }

    // DELETE
    @PostMapping("/packages/{id}/delete")
    public String delete(@PathVariable("id") Long packageId,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        Long vendorId = VendorSessionUtil.currentVendorId(session);
        if (vendorId == null) {
            return "redirect:/vendor/login";
        }
        try {
            packageService.delete(packageId, vendorId);
            redirectAttributes.addFlashAttribute("success", "Package deleted.");
        } catch (VendorException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vendor/workboard";
    }

    private String showForm(Model model, VendorPackage pkg, String action, String heading) {
        model.addAttribute("pkg", pkg);
        model.addAttribute("formAction", action);
        model.addAttribute("heading", heading);
        return "vendor-package-form";
    }

}
