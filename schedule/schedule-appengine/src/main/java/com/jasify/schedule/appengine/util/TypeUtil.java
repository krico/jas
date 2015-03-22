package com.jasify.schedule.appengine.util;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Text;

/**
 * @author krico
 * @since 11/11/14.
 */
public final class TypeUtil {
    private TypeUtil() {
    }

    public static Email toEmail(String email) {
        return email == null ? null : new Email(email);
    }

    public static String toString(Email email) {
        return email == null ? null : email.getEmail();
    }

    public static String toString(Text text) {
        return text == null ? null : text.getValue();
    }

    public static String toString(Link link) {
        return link == null ? null : link.getValue();
    }

    public static Text toText(String text) {
        return text == null ? null : new Text(text);
    }

    public static Link toLink(GenericUrl url) {
        return url == null ? null : toLink(url.build());
    }

    public static Link toLink(String url) {
        return url == null ? null : new Link(url);
    }


    public static byte[] toBytes(ShortBlob shortBlob) {
        return shortBlob == null ? null : shortBlob.getBytes();
    }

    public static ShortBlob toShortBlob(byte[] bytes) {
        return bytes == null ? null : new ShortBlob(bytes);
    }
}
