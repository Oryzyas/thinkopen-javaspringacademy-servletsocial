package com.thinkopen.servlet.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CommentFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = ((HttpServletRequest) servletRequest);
        final HttpServletResponse response = ((HttpServletResponse) servletResponse);
        final String method = request.getMethod();

        if(method.equalsIgnoreCase("get")) {
            final String postId = request.getParameter("post-id");

            if(postId == null) {
                response.sendError(400, "Invalid Post ID format.");
                return;
            }

            try {
                Integer.parseInt(postId);
            } catch (Exception e) {
                response.sendError(400, "Invalid Post ID format.");
                return;
            }
        } if(method.equalsIgnoreCase("post")) {
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
        final String postId = request.getParameter("post-id");
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

        if(content == null || content.length() < 1 || content.length() > 140) {
            response.sendError(400, "Invalid content format.");
            return false;
        }

        return true;
    }

    private boolean validateEditRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String commentId = request.getParameter("comment-id");
        final String content = request.getParameter("content");

        if(commentId == null) {
            response.sendError(400, "Invalid Comment ID format.");
            return false;
        }

        try {
            Integer.parseInt(commentId);
        } catch (Exception e) {
            response.sendError(400, "Invalid Comment ID format.");
            return false;
        }

        if(content != null && (content.length() < 1 || content.length() > 140)) {
            response.sendError(400, "Invalid content format.");
            return false;
        }

        return true;
    }

    private boolean validateDeleteRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String commentId = request.getParameter("comment-id");

        if(commentId == null) {
            response.sendError(400, "Invalid Comment ID format.");
            return false;
        }

        try {
            Integer.parseInt(commentId);
        } catch (Exception e) {
            response.sendError(400, "Invalid Comment ID format.");
            return false;
        }

        return true;
    }

    @Override
    public void destroy() {

    }
}
