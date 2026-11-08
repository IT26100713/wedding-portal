package com.WD08.WeddingMangementSystem.admin;

import com.WD08.WeddingMangementSystem.Repository.UserRepository;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Re-check account existence so deleting an account also ends its existing access.
 */
@Component
public class UserAccountGuard implements WebMvcConfigurer, HandlerInterceptor {
    private final UserRepository users;

    public UserAccountGuard(UserRepository users) {
        this.users = users;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns("/admin/**", "/customer/**");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var principal = request.getUserPrincipal();
        if (principal == null || !users.existsByEmail(principal.getName())) {
            var session = request.getSession(false);
            if (session != null) session.invalidate();
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}
