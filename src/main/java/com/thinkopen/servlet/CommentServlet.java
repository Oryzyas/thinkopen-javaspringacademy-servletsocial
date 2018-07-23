package com.thinkopen.servlet;

import com.thinkopen.servlet.dtos.Comment;
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

public class CommentServlet extends HttpServlet {
    private MySQLAccess dao = MySQLAccess.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final HttpSession session = req.getSession();
        final User user = (User) session.getAttribute("user");
        final ResponseType rt = (ResponseType) session.getAttribute("responseType");

        final Integer postId = Integer.parseInt(req.getParameter("post-id"));

        try {
            final List<Comment> comments = dao.selectAllComments(postId, null, null, -1);
            final StringBuilder sb = new StringBuilder();

            for(Comment comment : comments) {
                sb.append(writeResponse(comment, rt));
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
        final Integer postId = Integer.parseInt(req.getParameter("post-id"));
        final String postContent = req.getParameter("content");

        try {
            final Post post = dao.selectPostById(postId);

            if(post.isClosed()) {
                resp.sendError(403, "Post is closed to comments.");
                return;
            }

            final Comment comment = new Comment();
            comment.setPostId(postId);
            comment.setUserId(userId);
            comment.setContent(postContent);
            comment.setDate(System.currentTimeMillis());

            int id = dao.insert(comment);

            if(id == -1)
                throw new Exception("Comment creation aborted.");

            resp.getWriter().write("Comment inserted successfully: " + writeResponse(comment, rt));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void edit(HttpServletRequest req, HttpServletResponse resp, ResponseType rt, int userId) throws ServletException {
        final Integer commentId = Integer.parseInt(req.getParameter("comment-id"));
        final String postContent = req.getParameter("content");

        try {
            final Comment comment = dao.selectCommentById(commentId);

            if(comment.getUserId() != userId) {
                resp.sendError(403, "Cannot modify resources that are not of your own.");
                return;
            }

            if(postContent != null)
                comment.setContent(postContent);

            comment.setDate(System.currentTimeMillis());

            int id = dao.update(comment);

            if(id < 1)
                throw new Exception("Comment update aborted.");

            resp.getWriter().write("Comment updated successfully: " + writeResponse(comment, rt));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp, ResponseType rt, int userId) throws ServletException {
        final Integer commentId = Integer.parseInt(req.getParameter("comment-id"));

        try {
            final Comment comment = dao.selectCommentById(commentId);

            if(comment.getUserId() != userId) {
                resp.sendError(403, "Cannot modify resources that are not of your own.");
                return;
            }

            int id = dao.delete(comment);

            if(id < 1)
                throw new Exception("Comment deletion aborted.");

            resp.getWriter().write("Comment deleted successfully: " + writeResponse(comment, rt));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
