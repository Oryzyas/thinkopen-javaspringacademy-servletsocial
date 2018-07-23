package com.thinkopen.servlet;

import com.thinkopen.servlet.dtos.Post;
import com.thinkopen.servlet.dtos.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.thinkopen.servlet.Utils.writeResponse;
import static com.thinkopen.servlet.response.ResponseFactory.ResponseType;

public class PostServlet extends HttpServlet {
    private MySQLAccess dao = MySQLAccess.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final HttpSession session = req.getSession();
        final User user = (User) session.getAttribute("user");
        final ResponseType rt = (ResponseType) session.getAttribute("responseType");

        final String userIdStr = req.getParameter("user-id");

        try {
            final List<Post> posts = dao.selectAllPosts(userIdStr == null ? null : Integer.parseInt(userIdStr), null, null, -1);
            final StringBuilder sb = new StringBuilder();

            for(Post post : posts) {
                sb.append(writeResponse(post, rt));
                sb.append('\n');
            }

            resp.getWriter().write(sb.toString());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final HttpSession session = req.getSession();
        final User user = (User) session.getAttribute("user");
        final ResponseType rt = (ResponseType) session.getAttribute("responseType");

        final String path = req.getPathInfo();

        if(path == null) {
            insert(req, resp, rt, user.getId());
        } else if(path.equalsIgnoreCase("/edit")) {
            edit(req, resp, rt, user.getId());
        } else if(path.equalsIgnoreCase("/delete")) {
            delete(req, resp, rt, user.getId());
        } else {
            resp.sendError(404, "Resource not available.");
        }
    }

    private void insert(HttpServletRequest req, HttpServletResponse resp, ResponseType rt, int userId) throws ServletException {
        final String title = (String) req.getParameter("title");
        final String content = (String) req.getParameter("content");
        final Boolean isClosed = Boolean.valueOf(req.getParameter("closed"));

        final Post post = new Post();
        post.setUserId(userId);
        post.setTitle(title);
        post.setContent(content);
        post.setClosed(isClosed);
        post.setDate(System.currentTimeMillis());

        try {
            int id = dao.insert(post);

            if(id == -1)
                throw new Exception("Post creation aborted.");

            resp.getWriter().write("Post inserted successfully: " + writeResponse(post, rt));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void edit(HttpServletRequest req, HttpServletResponse resp, ResponseType rt, int userId) throws ServletException {
        final Integer postId = Integer.parseInt(req.getParameter("post-id"));
        final String title = (String) req.getParameter("title");
        final String content = (String) req.getParameter("content");
        final Boolean isClosed = Boolean.valueOf(req.getParameter("closed"));

        try {
            final Post post = dao.selectPostById(postId);

            if(post.getUserId() != userId) {
                resp.sendError(403, "Cannot modify resources that are not of your own.");
                return;
            }

            if(title != null)
                post.setTitle(title);

            if(content != null)
                post.setContent(content);

            if(req.getParameter("closed") != null)
                post.setClosed(isClosed);

            post.setDate(System.currentTimeMillis());

            int nra = dao.update(post);

            if(nra < 1)
                throw new Exception("Post update aborted.");

            resp.getWriter().write("Post updated successfully: " + writeResponse(post, rt));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp, ResponseType rt, int userId) throws ServletException {
        final Integer postId = Integer.parseInt(req.getParameter("post-id"));

        try {
            final Post post = dao.selectPostById(postId);

            if(post.getUserId() != userId) {
                resp.sendError(403, "Cannot modify resources that are not of your own.");
                return;
            }

            int nra = dao.delete(post);

            if(nra < 1)
                throw new Exception("Post deletion aborted.");

            resp.getWriter().write("Post deleted successfully: " + writeResponse(post, rt));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
