package com.WD18.wedding.booking.system.contoller;

import com.WD18.wedding.booking.system.DTO.UserUpdateDto;
import com.WD18.wedding.booking.system.Service.UserService;
import com.WD18.wedding.booking.system.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class DashboardController {

    private final UserService userService;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    // --- CUSTOMER DASHBOARD ---
    @GetMapping("/customer/dashboard")
    public String customerDashboard(Principal principal, Model model) {
        User user = userService.getUserByEmail(principal.getName());
        model.addAttribute("user", user);
        return "customer-dashboard";
    }

    // --- VIEW CUSTOMER PROFILE ---
    @GetMapping("/customer/profile")
    public String showCustomerProfile(Principal principal, Model model) {
        User user = userService.getUserByEmail(principal.getName());
        model.addAttribute("user", user);
        return "customer-profile";
    }

    // --- SHOW CUSTOMER EDIT PROFILE FORM ---
    @GetMapping("/customer/profile/edit")
    public String showCustomerEditForm(Principal principal, Model model) {
        User user = userService.getUserByEmail(principal.getName());

        UserUpdateDto dto = new UserUpdateDto();
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());

        model.addAttribute("user", user);
        model.addAttribute("updateDto", dto);
        return "customer-edit-profile";
    }

    // --- PROCESS CUSTOMER EDIT PROFILE ---
    @PostMapping("/customer/profile/edit")
    public String updateCustomerProfile(Principal principal,
                                        @ModelAttribute("updateDto") UserUpdateDto dto,
                                        RedirectAttributes redirectAttributes) {
        User user = userService.getUserByEmail(principal.getName());
        try {
            userService.updateUser(user.getId(), dto);
            return "redirect:/customer/profile?updated";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/profile/edit";
        }
    }

    // --- ADMIN DASHBOARD ---
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Principal principal, Model model) {
        User admin = userService.getUserByEmail(principal.getName());
        model.addAttribute("admin", admin);
        model.addAttribute("users", userService.getAllUsers());
        return "admin-dashboard";
    }

    // --- DELETE USER (ADMIN) ---
    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/dashboard?deleted";
    }

    // --- SHOW EDIT USER FORM (ADMIN) ---
    @GetMapping("/admin/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        UserUpdateDto dto = new UserUpdateDto();
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());

        model.addAttribute("user", user);
        model.addAttribute("updateDto", dto);
        return "edit-user";
    }

    // --- PROCESS EDIT USER (ADMIN) ---
    @PostMapping("/admin/users/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("updateDto") UserUpdateDto dto,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(id, dto);
            return "redirect:/admin/dashboard?updated";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/users/edit/" + id;
        }
    }
}