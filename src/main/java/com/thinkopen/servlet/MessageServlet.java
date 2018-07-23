package com.thinkopen.servlet;

import com.thinkopen.servlet.dtos.Message;
import com.thinkopen.servlet.dtos.MessageIO;
import com.thinkopen.servlet.dtos.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.thinkopen.servlet.Utils.writeResponse;
import static com.thinkopen.servlet.response.ResponseFactory.ResponseType;

public class MessageServlet extends HttpServlet {
    private MySQLAccess dao = MySQLAccess.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final HttpSession session = req.getSession();
        final User user = (User) session.getAttribute("user");
        final ResponseType rt = (ResponseType) session.getAttribute("responseType");

        final String path = req.getPathInfo();
        final String read = req.getParameter("read");
        final String trashed = req.getParameter("trashed");

        try {
            List<Message> messages;

            if(path == null) {
                messages = new ArrayList<>();
                messages.addAll(dao.selectAllSentMessages(user.getId(), trashed == null ? null : Boolean.valueOf(trashed),
                        null, null, -1));
                messages.addAll(dao.selectAllReceivedMessages(user.getId(), read == null ? null : Boolean.valueOf(read),
                        trashed == null ? null : Boolean.valueOf(trashed), null, null, -1));
            } else if(path.equalsIgnoreCase("/outbox")) {
                messages = dao.selectAllSentMessages(user.getId(), trashed == null ? null : Boolean.valueOf(trashed),
                        null, null, -1);
            } else if(path.equalsIgnoreCase("/inbox")) {
                messages = dao.selectAllReceivedMessages(user.getId(), read == null ? null : Boolean.valueOf(read),
                        trashed == null ? null : Boolean.valueOf(trashed), null, null, -1);
            } else {
                resp.sendError(404, "Resource not available.");
                return;
            }

            final StringBuilder sb = new StringBuilder();

            for(Message msg : messages) {
                sb.append(writeResponse(msg, rt));
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
        } else if(path.equalsIgnoreCase("/read")) {
            markAsRead(req, resp, rt, user.getId(), true);
        } else if(path.equalsIgnoreCase("/unread")) {
            markAsRead(req, resp, rt, user.getId(), false);
        } else if(path.equalsIgnoreCase("/trash")) {
            markAsTrashed(req, resp, rt, user.getId(), true);
        } else if(path.equalsIgnoreCase("/untrash")) {
            markAsTrashed(req, resp, rt, user.getId(), false);
        } else {
            resp.sendError(404, "Resource not available.");
        }
    }

    private void insert(HttpServletRequest req, HttpServletResponse resp, ResponseType rt, int userId) throws ServletException {
        final String content = req.getParameter("content");
        final String[] receivers = req.getParameterValues("receivers");
        final List<Integer> receiverList = new ArrayList<>();

        for(String id : receivers)
            receiverList.add(Integer.parseInt(id));

        Message msg = new Message();
        msg.setContent(content);
        msg.setSenderId(userId);
        msg.setDate(System.currentTimeMillis());

        try {
            msg = dao.send(msg, receiverList);

            if(msg.getId() == null)
                throw new Exception("Message sending aborted.");

            resp.getWriter().write("Message sent successfully: " + writeResponse(msg, rt));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void markAsTrashed(HttpServletRequest req, HttpServletResponse resp, ResponseType rt, int userId, boolean mark) throws ServletException {
        final String msgId = req.getParameter("msg-id");
        final String action = mark ? "trash" : "untrash";

        try {
            final MessageIO msgio = dao.selectMessageIOById(userId, msgId);
            final Message msg = dao.selectMessageById(msgId);

            if(msgio == null) {
                resp.sendError(403, "Cannot modify resources that are not of your own.");
                return;
            }

            final int nar = dao.setTrashFlag(msgio, mark);

            if(nar < 1)
                throw new Exception(String.format("Message %sing aborted.", action));

            resp.getWriter().write(String.format("Message %sed successfully: %s.", action, writeResponse(msg, rt)));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void markAsRead(HttpServletRequest req, HttpServletResponse resp, ResponseType rt, int userId, boolean mark) throws ServletException {
        final String msgId = req.getParameter("msg-id");
        final String action = mark ? "read" : "unread";

        try {
            final MessageIO msgio = dao.selectMessageIOById(userId, msgId);
            final Message msg = dao.selectMessageById(msgId);

            if(msgio == null) {
                resp.sendError(403, "Cannot modify resources that are not of your own.");
                return;
            }

            final int nar = dao.setReadFlag(msgio, mark);

            if(nar < 1)
                throw new Exception("Message marking aborted.");

            resp.getWriter().write(String.format("Message marked as '%s' successfully: %s.", action, writeResponse(msg, rt)));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
