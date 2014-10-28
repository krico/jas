package ch.findmyslot.tutorial.appengine;

import java.util.Date;

/**
* Created by krico on 29/10/14.
*/
class Greeting {
    private String content;
    private String user;
    private Date date;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
