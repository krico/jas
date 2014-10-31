package ch.findmyslot.tutorial.appengine.model;

import com.google.appengine.api.datastore.Key;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

/**
 * Created by krico on 31/10/14.
 */
@Model
public class GuestBook {
    @Attribute(primaryKey = true)
    private Key key;

    public String value;

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
