package com.thinkopen.servlet.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SignupFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = ((HttpServletRequest) servletRequest);
        final HttpServletResponse response = ((HttpServletResponse) servletResponse);
        final String method = request.getMethod();

        final HttpSession session = request.getSession(false);

        if(session != null && session.getAttribute("user") != null) {
            response.sendError(412, "Already logged. Cannot create a new account.");
            return;
        }

        if(method.equalsIgnoreCase("post")) {
            final String name = request.getParameter("name");
            final String email = request.getParameter("email");
            final String password = request.getParameter("password");
            final String age = request.getParameter("age");

            if(name == null || name.length() < 1 || name.length() > 50) {
                response.sendError(400, "Invalid name format.");
                return;
            }

            if(email == null || email.length() < 1 || email.length() > 30) {
                response.sendError(400, "Invalid email format.");
                return;
            }

            if(password == null || password.length() < 1 || password.length() > 20) {
                response.sendError(400, "Invalid password format.");
                return;
            }

            if(age == null) {
                response.sendError(400, "Invalid age format.");
                return;
            }

            try {
                Integer.parseInt(age);
            } catch (Exception e) {
                response.sendError(400, "Invalid age format.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
