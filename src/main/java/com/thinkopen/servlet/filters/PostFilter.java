package com.thinkopen.servlet.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PostFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = ((HttpServletRequest) servletRequest);
        final HttpServletResponse response = ((HttpServletResponse) servletResponse);
        final String method = request.getMethod();

        if(method.equalsIgnoreCase("get")) {
            final String userIdStr = request.getParameter("user-id");

            if(userIdStr != null) {
                try {
                    Integer.parseInt(userIdStr);
                } catch (Exception e) {
                    response.sendError(400, "Invalid User ID format.");
                    return;
                }
            }
        } else if(method.equalsIgnoreCase("post")) {
            final String path = request.getPathInfo();

            if(path == null && !validateInsertRequest(request, response)) {
                return;
            } else if(path != null) {
                if(path.equalsIgnoreCase("/edit") && !validateEditRequest(request, response)) {
                    return;
                } else if(path.equalsIgnoreCase("/delete") && !validateDeleteRequest(request, response)) {
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    private boolean validateInsertRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String title = request.getParameter("title");
        final String content = request.getParameter("content");

        if(title == null || title.length() < 1 || title.length() > 255) {
            response.sendError(400, "Invalid title format.");
            return false;
        }

        if(content == null || content.length() < 1 || content.length() > 1024) {
            response.sendError(400, "Invalid content format.");
            return false;
        }

        return true;
    }

    private boolean validateEditRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String postId = request.getParameter("post-id");
        final String title = request.getParameter("title");
        final String content = request.getParameter("content");

        if(postId == null) {
            response.sendError(400, "Invalid Post ID format.");
            return false;
        }

        try {
            Integer.parseInt(postId);
        } catch (Exception e) {
            response.sendError(400, "Invalid Post ID format.");
            return false;
        }

        if(title == null && (title.length() < 1 || title.length() > 255)) {
            response.sendError(400, "Invalid title format.");
            return false;
        }

        if(content == null && (content.length() < 1 || content.length() > 1024)) {
            response.sendError(400, "Invalid content format.");
            return false;
        }

        return true;
    }

    private boolean validateDeleteRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String postId = request.getParameter("post-id");

        if(postId == null) {
            response.sendError(400, "Invalid Post ID format.");
            return false;
        }

        try {
            Integer.parseInt(postId);
        } catch (Exception e) {
            response.sendError(400, "Invalid Post ID format.");
            return false;
        }

        return true;
    }

    @Override
    public void destroy() {

    }
}
