package com.jasify.schedule.appengine.util;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Objects;

/**
 * @author krico
 * @since 29/12/14.
 */
public final class ServletUtil {
    private static final Function<HttpServletRequest, StringBuilder> toString = new Function<HttpServletRequest, StringBuilder>() {
        @Nullable
        @Override
        public StringBuilder apply(HttpServletRequest req) {
            StringBuilder builder = new StringBuilder();

            builder.append("+=============================================================");
            if (req == null) {
                builder.append("\n| NULL request");
            } else {
                Enumeration headerNames = req.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    Object element = headerNames.nextElement();
                    builder.append("\n| Header[").append(element).append("] = '").append(req.getHeader(Objects.toString(element))).append("'");
                }
                Enumeration attributeNames = req.getAttributeNames();
                while (attributeNames.hasMoreElements()) {
                    Object element = attributeNames.nextElement();
                    builder.append("\n| Attribute[").append(element).append("] = '").append(req.getAttribute(Objects.toString(element))).append("'");
                }
                Enumeration parameterNames = req.getParameterNames();
                while (parameterNames.hasMoreElements()) {
                    Object element = parameterNames.nextElement();
                    builder.append("\n| Parameter[").append(element).append("] = '").append(req.getParameter(Objects.toString(element))).append("'");
                }
                builder.append("\n| AuthType = '").append(req.getAuthType()).append("'");
                builder.append("\n| Query = '").append(req.getQueryString()).append("'");
                builder.append("\n| RemoteUser = '").append(req.getRemoteUser()).append("'");
                builder.append("\n| PathInfo = '").append(req.getPathInfo()).append("'");
            }
            builder.append("\n+=============================================================");
            return builder;
        }
    };

    private ServletUtil() {
    }

    public static StringBuilder debug(HttpServletRequest req) {
        return toString.apply(req);
    }

    public static LogUtil.Lazy<HttpServletRequest, StringBuilder> debugLazy(HttpServletRequest req) {
        return LogUtil.toLazyString(toString, req);
    }
}
