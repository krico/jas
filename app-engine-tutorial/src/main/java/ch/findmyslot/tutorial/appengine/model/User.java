package ch.findmyslot.tutorial.appengine.model;

import com.google.appengine.api.datastore.Key;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import java.util.Date;

/**
 * Created by krico on 01/11/14.
 */
@Model
public class User {
    @Attribute(primaryKey = true)
    private Key key;

    private String name;

    private Date creationDate;


    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "key=" + key +
                ", name='" + name + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}
