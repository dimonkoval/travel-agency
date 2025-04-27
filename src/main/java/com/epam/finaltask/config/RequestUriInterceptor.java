package com.epam.finaltask.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class RequestUriInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView
    ) {
        if (modelAndView == null) {
            modelAndView = new ModelAndView();
        }
        modelAndView.addObject("requestURI", request.getRequestURI());
        modelAndView.addObject("contextPath", request.getContextPath());
    }
}
