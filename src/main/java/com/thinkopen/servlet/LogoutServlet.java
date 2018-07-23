package com.thinkopen.servlet;

import com.thinkopen.servlet.dtos.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    private MySQLAccess dao = MySQLAccess.getInstance();

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.service(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logout(request, response);
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final HttpSession session = request.getSession();
        final User user = (User) session.getAttribute("user");

        session.removeAttribute("user");
        session.removeAttribute("responseType");

        response.getWriter().write(String.format("User %s logged out successfully.", user.getName()));
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
