package com.thinkopen.servlet.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = ((HttpServletRequest) servletRequest);
        final HttpServletResponse response = ((HttpServletResponse) servletResponse);
        final String method = request.getMethod();

        final HttpSession session = request.getSession(false);

        if(session != null && session.getAttribute("user") != null) {
            response.sendError(412, "Already logged. Cannot login again.");
            return;
        }

        if(method.equalsIgnoreCase("post")) {
            final String username = request.getParameter("username");
            final String password = request.getParameter("password");

            if(username == null || password == null) {
                response.sendError(400, "Invalid login parameters.");
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
