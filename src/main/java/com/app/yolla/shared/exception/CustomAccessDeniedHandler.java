//package com.app.yolla.shared.exception;
//
//import java.io.IOException;
//
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.web.access.AccessDeniedHandler;
//import org.springframework.stereotype.Component;
//
//import com.app.yolla.shared.dto.ApiResponse;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Component
//public class CustomAccessDeniedHandler implements AccessDeniedHandler {
//
//    @Override
//    public void handle(HttpServletRequest request,
//                       HttpServletResponse response,
//                       AccessDeniedException accessDeniedException) throws IOException {
//
//
//		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//		response.setContentType("application/json;charset=UTF-8");
//        String uri = request.getRequestURI();
//		String message = null;
//
//		String method = request.getMethod();
//
//		if (uri.startsWith("/api/v1/orders") && uri.contains("/ship")) {
//            message = "Bu əməliyyatı yalnız ADMIN və ya PREPARER rolu olan istifadəçilər edə bilər.";
//		} else if (uri.startsWith("/api/v1/orders") && uri.contains("/deliver")) {
//            message = "Bu əməliyyatı yalnız ADMIN və ya PREPARER rolu olan istifadəçilər edə bilər.";
//		} else if (uri.startsWith("/api/v1/orders") && uri.contains("/confirm")) {
//            message = "Bu əməliyyatı yalnız ADMIN və ya CUSTOMER rolu olan istifadəçilər yerinə yetirə bilər.";
//		} else if (uri.startsWith("/api/v1/orders")) {
//            message = "Bu əməliyyatı yalnız ADMIN və ya CUSTOMER rolu olan istifadəçilər yerinə yetirə bilər.";
//		} else if (uri.startsWith("/api/v1/products")) {
//			if (method.equals("POST") || method.equals("PUT") || method.equals("DELETE")) {
//				message = "Bu əməliyyatı yalnız ADMIN rolu olan istifadəçilər yerinə yetirə bilər.";
//			}
//		} else if (uri.startsWith("/api/v1/users")) {
//			if (uri.contains("/email") || uri.contains("/phone") || uri.contains("/role")) {
//				message = "Bu əməliyyatı yalnız PREPARER və ya ADMIN rolu olan istifadəçilər yerinə yetirə bilər.";
//			} else if (uri.contains("/search") || uri.contains("/reactivate") || method.equals("GET")) {
//				message = "Bu əməliyyatı yalnız ADMIN rolu olan istifadəçilər yerinə yetirə bilər.";
//			} else if (method.equals("POST")) {
//				message = "Bu əməliyyatı yalnız ADMIN rolu olan istifadəçilər yerinə yetirə bilər.";
//			} else if (method.equals("PUT") || method.equals("DELETE")) {
//				message = "Bu əməliyyatı yalnız ADMIN və ya öz məlumatlarını idarə edən CUSTOMER istifadəçilər yerinə yetirə bilər.";
//			}
//        }
//
//		if (message == null) {
//			message = "Bu əməliyyatı yerinə yetirmək üçün yetərli icazəniz yoxdur.";
//		}
//
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.findAndRegisterModules();
//
//		ApiResponse<String> apiResponse = new ApiResponse<>(false, message, null);
//
//		response.getWriter().write(mapper.writeValueAsString(apiResponse));
//    }
//}
//
