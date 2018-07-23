package com.thinkopen.servlet.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MessageFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = ((HttpServletRequest) servletRequest);
        final HttpServletResponse response = ((HttpServletResponse) servletResponse);
        final String method = request.getMethod();

        if(method.equalsIgnoreCase("get")) {

        } else if(method.equalsIgnoreCase("post")) {
            final String path = request.getPathInfo();

            if(path == null && !validateInsertRequest(request, response)) {
                return;
            } else if(path != null) {
                if((path.equalsIgnoreCase("/trash") || path.equalsIgnoreCase("/untrash") ||
                        path.equalsIgnoreCase("/read") || path.equalsIgnoreCase("/unread")) &&
                        !validateSetFlagRequest(request, response)) {
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    private boolean validateInsertRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String content = request.getParameter("content");
        final String[] receiverIds = request.getParameterValues("receivers");

        if(content == null || content.length() < 1 || content.length() > 255) {
            response.sendError(400, "Invalid content format.");
            return false;
        }

        if(receiverIds == null || receiverIds.length < 1) {
            response.sendError(400, "Invalid number of receivers.");
        }

        for(int i = 0; i < receiverIds.length; ++i) {
            final String receiverId = receiverIds[i];

            if(receiverId == null) {
                response.sendError(400, String.format("Invalid User ID format on %s[%s].", "receivers", i));
                return false;
            }

            try {
                Integer.parseInt(receiverId);
            } catch (Exception e) {
                response.sendError(400, String.format("Invalid User ID format on %s[%s].", "receivers", i));
                return false;
            }
        }

        return true;
    }

    private boolean validateSetFlagRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String msgId = request.getParameter("msg-id");

        if(msgId == null || msgId.length() != 36) {
            response.sendError(400, "Invalid Message ID format.");
            return false;
        }

        return true;
    }
}
