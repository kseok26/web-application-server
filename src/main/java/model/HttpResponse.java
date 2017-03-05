package model;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author young seok.kim
 */
@ToString
@Data
public class HttpResponse {
    private DataOutputStream outputStream;

    private Map<String, String> header = new HashMap<>();

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = new DataOutputStream(outputStream);
    }

    public void forward(String url) {
        try {
            byte[] body = "HelloWorld".getBytes();
            String contentType = "html";
            if (StringUtils.isNotEmpty(url)) {
                body = Files.readAllBytes(new File("./webapp" + url).toPath());
                contentType = url.split("\\.")[1];
            }

            outputStream.writeBytes("HTTP/1.1 200 OK \r\n");
            outputStream.writeBytes("Content-Type: text/" + contentType + ";charset=utf-8\r\n");
            outputStream.writeBytes("Content-Length: " + body.length + "\r\n");
            outputStream.writeBytes("\r\n");
            outputStream.write(body, 0, body.length);
            outputStream.flush();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    public void sendRedirect(String url) {
        try {
            outputStream.writeBytes("HTTP/1.1 302 Found \r\n");
            outputStream.writeBytes("Location: " + url + "\r\n");
            if (MapUtils.isNotEmpty(header)) {
                Iterator<String> iterator = header.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    outputStream.writeBytes(key + ": " + header.get(key) + "\r\n");
                }
            }
            outputStream.writeBytes("\r\n");
            outputStream.flush();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }
}
