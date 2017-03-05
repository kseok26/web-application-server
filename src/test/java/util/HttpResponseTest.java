package util;

import model.HttpResponse;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by young-seok on 2017. 3. 5..
 */
public class HttpResponseTest {
    private String testDirectory = "./src/test/resource/";

    @Test
    public void responseForward() throws Exception {
        HttpResponse httpResponse = new HttpResponse(createOutputStream("Http_Forward.txt"));

        httpResponse.forward("/index.html");
    }

    @Test
    public void responseForwardCSS() throws Exception {
        HttpResponse httpResponse = new HttpResponse(createOutputStream("Http_ForwardCSS.txt"));

        httpResponse.forward("/css/styles.css");
    }

    @Test
    public void responseForwardJS() throws Exception {
        HttpResponse httpResponse = new HttpResponse(createOutputStream("Http_ForwardJS.txt"));

        httpResponse.forward("/js/scripts.js");
    }

    @Test
    public void responseSendRedirect() throws Exception {
        HttpResponse httpResponse = new HttpResponse(createOutputStream("Http_Redirect.txt"));

        httpResponse.sendRedirect("/index.html");
    }


    @Test
    public void responseCookies() throws Exception {
        HttpResponse httpResponse = new HttpResponse(createOutputStream("Http_Cookie.txt"));
        httpResponse.addHeader("Set-Cookie","logined=true");
        httpResponse.sendRedirect("/index.html");
    }



    private OutputStream createOutputStream(String filename) throws FileNotFoundException{
        return new FileOutputStream(new File(testDirectory + filename));
    }
}
