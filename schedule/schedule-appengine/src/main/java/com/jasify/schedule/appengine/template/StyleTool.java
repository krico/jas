package com.jasify.schedule.appengine.template;

import com.jasify.schedule.appengine.util.EnvironmentUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author krico
 * @since 31/08/15.
 */
public class StyleTool implements Styles {
    private static final Logger log = LoggerFactory.getLogger(StyleTool.class);

    private static final Pattern CSS_PATTERN = Pattern.compile("\\s*([^\\s]+)\\s*\\{\\s*([^}]*)\\s*}");

    private final Map<String, String> styles;

    public StyleTool(String stylesPath) throws TemplateEngineException {
        styles = Collections.unmodifiableMap(parse(stylesPath));
    }

    @Override
    public String get(String style) {
        if (EnvironmentUtil.isDevelopment() && !styles.containsKey(style))
            throw new RuntimeException("Style not found [" + style + "]");
        return styles.get(style);
    }

    /* I can't believe I'm writing a css parser!!! */
    private Map<String, String> parse(String stylesPath) throws TemplateEngineException {
        String data = readData(stylesPath);
        Map<String, String> styles = new HashMap<>();
        Matcher matcher = CSS_PATTERN.matcher(data);
        while (matcher.find()) {
            String styleName = matcher.group(1);
            String styleData = matcher.group(2);
            String[] styleEntries = StringUtils.split(styleData, ';');
            StringBuilder style = new StringBuilder();
            String first = "";
            for (String styleEntry : styleEntries) {
                String[] kvp = StringUtils.split(styleEntry, ':');
                String name = StringUtils.trimToNull(kvp[0]);
                String value = StringUtils.trimToNull(kvp[1]);
                if (name == null || value == null) {
                    log.warn("BAD STYLE:[{}] K:[{}] V:[{}]", styleEntry, name, value);
                    continue;
                }
                style.append(first).append(name).append(':').append(value);
                first = ";";
            }

            styles.put(styleName, style.toString());
            if (log.isDebugEnabled()) log.debug("Style[{}]=[{}]", styleName, style);
        }
        return styles;
    }

    private String readData(String stylesPath) throws TemplateEngineException {
        String data;
        try (InputStream is = StyleTool.class.getResourceAsStream(stylesPath)) {
            if (is == null) throw new TemplateEngineException("Styles not found [" + stylesPath + "]");
            data = StringUtils.trimToEmpty(IOUtils.toString(is));
        } catch (IOException e) {
            throw new TemplateEngineException("Failed to parse: " + stylesPath, e);
        }
        data = StringUtils.remove(data, '\r');
        data = StringUtils.remove(data, '\n');
        data = StringUtils.replace(data, "*/", "*/ "); //Fix comments..
        return data;
    }
}
