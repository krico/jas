package com.jasify.schedule.appengine.util;

import com.jasify.schedule.appengine.TestHelper;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.easymock.EasyMock.*;

@RunWith(EasyMockRunner.class)
public class ServletUtilTest {

    @Mock
    private HttpServletRequest request;

    @After
    public void verifyMock() {
        verify(request);
    }

    @Test
    public void wellDefined() throws Exception {
        replay(request);
        TestHelper.assertUtilityClassWellDefined(ServletUtil.class);
    }


    private void record() {
        expect(request.getHeaderNames()).andReturn(Collections.enumeration(Collections.singleton("X-Foo"))).once();
        expect(request.getHeader("X-Foo")).andReturn("Bar").once();

        expect(request.getAttributeNames()).andReturn(Collections.enumeration(Collections.singleton("FooAttribute"))).once();
        expect(request.getAttribute("FooAttribute")).andReturn("BarAttribute").once();

        expect(request.getParameterNames()).andReturn(Collections.enumeration(Collections.singleton("FooParam"))).once();
        expect(request.getParameter("FooParam")).andReturn("BarParam").once();

        expect(request.getAuthType()).andReturn("AT").once();
        expect(request.getQueryString()).andReturn("").once();
        expect(request.getRemoteUser()).andReturn("SOO").once();
        expect(request.getPathInfo()).andReturn("/path/info").once();
    }

    @Test
    public void testDebugRequest() {
        record();
        replay(request);
        StringBuilder debug = ServletUtil.debug(request);
        assertNotNull(debug);
    }

    @Test
    public void testDebugLazyRequest() {
        record();
        record();
        replay(request);
        LogUtil.Lazy<HttpServletRequest, StringBuilder> lazy = ServletUtil.debugLazy(request);
        assertEquals(ServletUtil.debug(request).toString(), lazy.toString());
    }

    @Test
    public void testDebugLazyRequestNotCalled() {
        replay(request);
        ServletUtil.debugLazy(request);
    }

    @Test
    public void testDebugNullRequest() {
        replay(request);
        StringBuilder debug = ServletUtil.debug(null);
        assertNotNull(debug);
    }
}