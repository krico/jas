package com.jasify.sandbox.appengine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krico on 29/10/14.
 */
class GuestBook {
    private String guestBook;
    private List<Greeting> greetings = new ArrayList<>();

    public String getGuestBook() {
        return guestBook;
    }

    public void setGuestBook(String guestBook) {
        this.guestBook = guestBook;
    }

    public List<Greeting> getGreetings() {
        return greetings;
    }

    public void setGreetings(List<Greeting> greetings) {
        this.greetings = greetings;
    }
}
