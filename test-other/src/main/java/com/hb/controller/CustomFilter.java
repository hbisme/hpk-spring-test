// package com.hb.controller;
//
// import java.io.IOException;
//
// import javax.servlet.Filter;
// import javax.servlet.FilterChain;
// import javax.servlet.FilterConfig;
// import javax.servlet.ServletException;
// import javax.servlet.ServletRequest;
// import javax.servlet.ServletResponse;
// import javax.servlet.annotation.WebFilter;
// import javax.servlet.http.HttpServletRequest;
//
// @WebFilter(urlPatterns = "/*")
// public class CustomFilter implements Filter {
//     @Override
//     public void doFilter(ServletRequest request, ServletResponse response,
//                          FilterChain chain) throws IOException, ServletException {
//         HttpServletRequest req = (HttpServletRequest) request;
//         MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(req);
//         mutableRequest.putHeader("x-custom-header", "custom value");
//         chain.doFilter(mutableRequest, response);
//     }
//
//     @Override
//     public void init(FilterConfig filterConfig) throws ServletException {
//
//     }
//
//     @Override
//     public void destroy() {
//
//     }
// }
