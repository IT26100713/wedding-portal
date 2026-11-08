package com.WD08.WeddingMangementSystem.admin;

import com.WD08.WeddingMangementSystem.DTO.UserUpdateDto;
import com.WD08.WeddingMangementSystem.Service.UserService;
import com.WD08.WeddingMangementSystem.vendor.dto.VendorForm;
import com.WD08.WeddingMangementSystem.vendor.service.VendorService;
import com.WD08.WeddingMangementSystem.vendor.exception.VendorException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Locale;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AdminService service;
    private final UserService users;
    private final VendorService vendors;

    public AdminController(AdminService service, UserService users, VendorService vendors) {
        this.service = service;
        this.users = users;
        this.vendors = vendors;
    }

    @GetMapping({"", "/dashboard", "/users", "/vendors", "/activity"})
    public String dashboard(@RequestParam(defaultValue = "") String search, @RequestParam(defaultValue = "") String status, @RequestParam(defaultValue = "0") int page, Principal principal, Model model) {
        model.addAttribute("admin", users.getUserByEmail(principal.getName()));
        String term = search.trim().toLowerCase(Locale.ROOT);
        model.addAttribute("users", users.getAllUsers().stream().filter(u -> (u.getFullName() + " " + u.getEmail() + " " + u.getRole()).toLowerCase(Locale.ROOT).contains(term)).toList());
        model.addAttribute("vendors", service.vendors(search.trim(), status));
        model.addAttribute("activity", service.activity(page));
        model.addAttribute("counts", service.counts());
        model.addAttribute("recentActivity", service.recentActivity());
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("page", Math.max(0, page));
        return "admin-dashboard";
    }

    @PostMapping("/vendors/{id}/decision")
    public String decide(@PathVariable Long id, @RequestParam String status, @RequestParam(defaultValue = "") String reason, Principal p, RedirectAttributes r) {
        service.decide(id, status, reason, p.getName());
        r.addFlashAttribute("success", "Vendor decision saved.");
        return "redirect:/admin/dashboard#vendors";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model m) {
        var u = users.getUserById(id);
        var dto = new UserUpdateDto();
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setPhoneNumber(u.getPhoneNumber());
        m.addAttribute("user", u);
        m.addAttribute("updateDto", dto);
        return "edit-user";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("updateDto") UserUpdateDto dto, Principal p, Model m, RedirectAttributes r) {
        try {
            service.updateUser(id, dto, p.getName());
        } catch (IllegalArgumentException e) {
            dto.setPassword("");
            m.addAttribute("user", users.getUserById(id));
            m.addAttribute("error", e.getMessage());
            return "edit-user";
        }
        r.addFlashAttribute("success", "User updated.");
        return "redirect:/admin/dashboard#users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, Principal p, RedirectAttributes r) {
        service.deleteUser(id, p.getName());
        r.addFlashAttribute("success", "User deleted.");
        return "redirect:/admin/dashboard#users";
    }

    @GetMapping("/vendors/{id}/edit")
    public String editVendor(@PathVariable Long id, Model m) {
        var v = vendors.getById(id);
        m.addAttribute("vendor", v);
        m.addAttribute("form", vendors.toForm(v));
        return "admin-vendor-edit";
    }

    @PostMapping("/vendors/{id}/edit")
    public String updateVendor(@PathVariable Long id, @ModelAttribute("form") VendorForm form, Principal p, Model m, RedirectAttributes r) {
        try {
            service.updateVendor(id, form, p.getName());
        } catch (VendorException e) {
            m.addAttribute("vendor", vendors.getById(id));
            m.addAttribute("error", e.getMessage());
            return "admin-vendor-edit";
        }
        r.addFlashAttribute("success", "Vendor updated.");
        return "redirect:/admin/dashboard#vendors";
    }

    @PostMapping("/vendors/{id}/delete")
    public String deleteVendor(@PathVariable Long id, Principal p, RedirectAttributes r) {
        service.deleteVendor(id, p.getName());
        r.addFlashAttribute("success", "Vendor deleted.");
        return "redirect:/admin/dashboard#vendors";
    }

    @ExceptionHandler({IllegalArgumentException.class, VendorException.class})
    public String error(RuntimeException e, RedirectAttributes r) {
        r.addFlashAttribute("error", e.getMessage());
        return "redirect:/admin/dashboard";
    }
}
