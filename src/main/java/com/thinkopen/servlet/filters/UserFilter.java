package com.thinkopen.servlet.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = ((HttpServletRequest) servletRequest);
        final HttpServletResponse response = ((HttpServletResponse) servletResponse);
        final String method = request.getMethod();

        if(method.equalsIgnoreCase("post")) {
            final String name = request.getParameter("name");
            final String age = request.getParameter("age");
            final String email = request.getParameter("email");
            final String password = request.getParameter("password");

            if(name != null && (name.length() < 1 || name.length() > 50)) {
                response.sendError(400, "Invalid name format.");
                return;
            }

            if(email != null && (email.length() < 1 || email.length() > 30)) {
                response.sendError(400, "Invalid name format.");
                return;
            }

            if(password != null && (password.length() < 1 || password.length() > 20)) {
                response.sendError(400, "Invalid password format.");
                return;
            }

            if(age != null) {
                try {
                    Integer.parseInt(age);
                } catch (Exception e) {
                    response.sendError(400, "Invalid age format.");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
