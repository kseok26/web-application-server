package util;

import model.HttpRequest;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Created by young-seok on 2017. 3. 5..
 */
public class HttpRequestTest {
    private String testDirectory = "./src/test/resource/";

    @Test
    public void request_GET() throws Exception {
        InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
        HttpRequest request = new HttpRequest(in);

        assertEquals("GET", request.getMethod());
        assertEquals("/user/create",request.getUrl());
        assertEquals("keep-alive",request.getHeader("Connection"));
        assertEquals("javajigi",request.getParams("userId"));
    }

    @Test
    public void request_POST() throws Exception {
        InputStream in = new FileInputStream(new File(testDirectory + "Http_POST.txt"));
        HttpRequest request = new HttpRequest(in);

        assertEquals("POST", request.getMethod());
        assertEquals("/user/create",request.getUrl());
        assertEquals("keep-alive",request.getHeader("Connection"));
        assertEquals("javajigi",request.getParams("userId"));
    }
}
