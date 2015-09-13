package com.jasify.schedule.appengine.http.servlet;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.attachment.AttachmentDao;
import com.jasify.schedule.appengine.model.attachment.Attachment;
import com.jasify.schedule.appengine.util.KeyUtil;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author krico
 * @since 31/07/15.
 */
public class AttachmentServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(AttachmentServlet.class);
    private static final Pattern PATH_INFO_PATTERN = Pattern.compile("^/([^/]+)(/.*)?$");

    private AttachmentDao attachmentDao;

    private boolean canView(HttpServletRequest req, HttpServletResponse resp, Attachment attachment) {
        //TODO: We need to protect attachments from anonymous people grabbing urls
        log.info("We haven't secured access to: {}", attachment.getId());
        return true;
    }

    @Override
    public void init() throws ServletException {
        log.debug("init()");
        attachmentDao = new AttachmentDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //We fetch the ID from the path, so /thisServlet/XXXXX means ID=XXXXX and other information
        String pathInfo = req.getPathInfo();
        if (StringUtils.isBlank(pathInfo)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Attachment.ID is missing.");
            return;
        }

        Matcher matcher = PATH_INFO_PATTERN.matcher(pathInfo);
        if (!matcher.matches()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "pathInfo is invalid.");
            return;
        }

        String id = matcher.group(1);
        String pathFilename = StringUtils.substring(matcher.group(2), 1);

        boolean download = req.getServletPath().startsWith("/download");

        Key attachmentId = KeyUtil.stringToKey(id);
        if (attachmentId == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Attachment.ID is invalid.");
            return;
        }

        Attachment attachment = attachmentDao.getOrNull(attachmentId);
        if (attachment == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Attachment doesn't exist.");
            return;
        }

        if (!canView(req, resp, attachment)) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        byte[] data = TypeUtil.toBytes(attachment.getData());
        if (data == null) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Attachment has no data.");
            return;
        }

        if (StringUtils.isNotBlank(attachment.getMimeType())) {
            resp.setContentType(attachment.getMimeType());
        }
        String name = attachment.getName();
        if (StringUtils.isBlank(name)) {
            name = pathFilename == null ? "untitled" : pathFilename;
        }
        if (pathFilename != null && !Objects.equals(name, pathFilename)) {
            log.warn("Request for attachment: {} supplied path filename: {} but name: {}", id, pathFilename, name);
        }
        String disposition = download ? "attachment" : "inline";
        resp.addHeader("content-disposition", disposition + "; filename=\"" + name + "\"");

        resp.setContentLength(data.length);
        resp.getOutputStream().write(data);
        resp.getOutputStream().flush();

    }

}
