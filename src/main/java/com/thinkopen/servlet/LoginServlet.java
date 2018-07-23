package com.thinkopen.servlet;

import com.thinkopen.servlet.dtos.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.thinkopen.servlet.response.ResponseFactory.ResponseType;

public class LoginServlet extends HttpServlet {
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
        response.sendRedirect("login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String username = request.getParameter("username");
        final String password = request.getParameter("password");
        final String responseType = request.getParameter("response-type");

        try {
            User user = dao.login(username, password);

            if(user == null) {
                response.sendError(401, "Username and/or password are invalid.");
                return;
            }

            ResponseType rt = ResponseType.parse(responseType);

            if(rt == null)
                rt = ResponseType.JSON;

            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("responseType", rt);

            response.getWriter().write(String.format("User %s logged in successfully.", user.getName()));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
