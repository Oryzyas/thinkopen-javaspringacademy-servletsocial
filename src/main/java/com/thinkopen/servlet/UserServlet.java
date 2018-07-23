package com.thinkopen.servlet;

import com.thinkopen.servlet.dtos.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.thinkopen.servlet.Utils.writeResponse;
import static com.thinkopen.servlet.response.ResponseFactory.ResponseType;

public class UserServlet extends HttpServlet {
    private MySQLAccess dao = MySQLAccess.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doGet(req, resp);
        final HttpSession session = req.getSession();
        final User user = (User) session.getAttribute("user");
        final ResponseType rt = (ResponseType) session.getAttribute("responseType");

        resp.getWriter().write(writeResponse(user, rt));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final HttpSession session = req.getSession();
        final User user = (User) session.getAttribute("user");
        final ResponseType rt = (ResponseType) session.getAttribute("responseType");

        final String name = req.getParameter("name");
        final String email = req.getParameter("email");
        final String password = req.getParameter("password");
        final String age = req.getParameter("age");

        try {
            if(name != null)
                user.setName(name);

            if(email != null)
                user.setEmail(email);

            if(age != null)
                user.setAge(Integer.parseInt(age));

            int nar = dao.update(user);

            if(nar < 1)
                throw new Exception("User update aborted.");

            if(password != null) {
                nar = dao.update(user.getId(), password);

                if(nar < 1)
                    throw new Exception("Password update aborted.");
            }

            resp.getWriter().write("User updated successfully: " + writeResponse(user, rt));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
