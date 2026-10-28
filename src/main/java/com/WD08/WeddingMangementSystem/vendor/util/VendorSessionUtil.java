package com.WD08.WeddingMangementSystem.vendor.util;

import jakarta.servlet.http.HttpSession;

public class VendorSessionUtil {

    private static final String KEY = "vendorId";

    private VendorSessionUtil() {
    }

    public static void login(HttpSession session, Long vendorId) {
        session.setAttribute(KEY, vendorId);
    }

    public static Long currentVendorId(HttpSession session) {
        Object value = session.getAttribute(KEY);
        return (value instanceof Long) ? (Long) value : null;
    }

    public static void logout(HttpSession session) {
        session.invalidate();
    }

}
