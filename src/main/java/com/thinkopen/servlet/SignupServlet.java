package com.thinkopen.servlet;

import com.thinkopen.servlet.dtos.User;
import com.thinkopen.servlet.response.ResponseFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.thinkopen.servlet.Utils.writeResponse;

public class SignupServlet extends HttpServlet {
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
        response.sendRedirect("signup.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String name = request.getParameter("name");
        final String email = request.getParameter("email");
        final String password = request.getParameter("password");
        final Integer age = Integer.parseInt(request.getParameter("age"));
        final String responseType = request.getParameter("response-type");

        final User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        try {
            int id = dao.insert(user, password);

            if(id < 1)
                throw new Exception("Account creation aborted.");

            user.setId(id);

            ResponseFactory.ResponseType rt = ResponseFactory.ResponseType.parse(responseType);

            if(rt == null)
                rt = ResponseFactory.ResponseType.JSON;

            response.getWriter().write("Account created successfully: " + writeResponse(user, rt));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
