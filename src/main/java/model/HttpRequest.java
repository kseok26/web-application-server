package model;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author young seok.kim
 */
@Data
@ToString
public class HttpRequest {
    private BufferedReader bufferedReader;

    private String url;
    private String method;
    private String httpVersion;
    private String host;
    private String connection;
    private String userAgent;
    private String accept;
    private String acceptEncoding;
    private String acceptLanguage;
    private String referer;
    private int contentLength;

    private Map<String, String> header;
    private Map<String, String> cookies;
    private Map<String, String> params;

    public HttpRequest() {
        super();
    }

    public HttpRequest(InputStream in) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(in));

        try {
            setRequestLine(bufferedReader);
            setRequestHeader(bufferedReader);
            setRequestParameter(bufferedReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRequestLine(BufferedReader bufferedReader) throws Exception {
        String line = bufferedReader.readLine();
        if (StringUtils.isNotEmpty(line)) {
            String[] tokens = StringUtils.split(line, " ");
            this.setMethod(tokens[0]);
            this.setUrl(tokens[1]);
            this.setHttpVersion(tokens[2]);
        }
    }

    private void setRequestHeader(BufferedReader bufferedReader) throws Exception {
        String line;
        Map<String, String> requestHeader = new HashMap<String, String>();
        while (StringUtils.isNotEmpty(line = bufferedReader.readLine())) {
            HttpRequestUtils.Pair tempPair = HttpRequestUtils.parseHeader(line);
            requestHeader.put(tempPair.getKey(), tempPair.getValue());
        }
        this.setHeader(requestHeader);
    }

    private void setRequestParameter(BufferedReader bufferedReader) throws Exception {
        if (StringUtils.equals(this.getMethod(), "POST")) {
            int contentLength = Integer.parseInt(this.getHeader().get("Content-Length"));
            String body = IOUtils.readData(bufferedReader, contentLength);
            this.setParams(HttpRequestUtils.parseQueryString(body));
            return;
        }

        if (StringUtils.contains(this.getUrl(), '?')) {
            String[] seperateUrlAndParam = StringUtils.split(this.getUrl(), "?");
            this.setUrl(seperateUrlAndParam[0]);
            this.setParams(HttpRequestUtils.parseQueryString(seperateUrlAndParam[1]));
        }
    }

    public String getHeader(String key) {
        return this.getHeader().get(key);
    }

    public String getParams(String key) {
        return this.getParams().get(key);
    }

}
