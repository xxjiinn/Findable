package com.capstone1.findable.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** 브라우저의 자동 요청(com.chrome.devtools.json 등) 무시 처리용 필터 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)  // 가장 먼저 실행되도록 설정 :contentReference[oaicite:1]{index=1}
public class DevToolsIgnoreFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String uri = request.getRequestURI();

        // 크롬 개발자도구 자동 호출 및 appspecific 경로 무시
        if (uri.contains(".well-known")
                || uri.contains("com.chrome.devtools.json")
                || uri.startsWith("/appspecific/")) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"status\":\"ok\",\"message\":\"ignored by DevToolsIgnoreFilter\"}");
            return;
        }

        // 나머지 요청은 정상 처리
        chain.doFilter(request, response);
    }
}
