package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.users.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class JasUserListTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasUserList.class);
    }

    @Test
    public void testUsers() {
        JasUserList jasUserList = new JasUserList();
        List<User> userList = new ArrayList<>();
        jasUserList.setUsers(userList);
        assertEquals(userList, jasUserList.getUsers());
    }

    @Test
    public void testTotal() {
        JasUserList jasUserList = new JasUserList();
        int total = 5;
        jasUserList.setTotal(total);
        assertEquals(total, jasUserList.getTotal());
    }

    @Test
    public void testAddAll() {
        JasUserList jasUserList = new JasUserList();
        List<User> userList = new ArrayList<>();
        User user = new User();
        userList.add(user);
        jasUserList.addAll(userList);
        assertEquals(userList.size(), jasUserList.size());
        assertEquals(user, jasUserList.get(0));
    }
}