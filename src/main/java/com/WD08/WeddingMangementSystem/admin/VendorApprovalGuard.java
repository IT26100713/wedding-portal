package com.WD08.WeddingMangementSystem.admin;

import jakarta.servlet.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

@Component
public class VendorApprovalGuard implements WebMvcConfigurer, HandlerInterceptor {
    private final JdbcTemplate jdbc;

    public VendorApprovalGuard(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns("/vendor/**").excludePathPatterns("/vendor/login", "/vendor/register", "/vendor/logout", "/vendor/css/**", "/vendor/img/**");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var session = request.getSession(false);
        Object id = session == null ? null : session.getAttribute("vendorId");
        if (id == null) {
            response.sendRedirect("/vendor/login");
            return false;
        }
        var statuses = jdbc.queryForList("SELECT approval_status FROM vendors WHERE id=?", String.class, id);
        if (statuses.isEmpty() || !"APPROVED".equals(statuses.getFirst())) {
            session.removeAttribute("vendorId");
            response.sendRedirect("/vendor/login?approvalRequired");
            return false;
        }
        return true;
    }
}
