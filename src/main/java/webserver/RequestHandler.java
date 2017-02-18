package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.HttpRequest;
import model.User;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private static final String url = File.separator + "index.html";
	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
			connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
			DataOutputStream dos = new DataOutputStream(out);
			byte[] body = mappingUrl(getHttpRequestInfoByInputStream(in));
			response200Header(dos, body.length);
			responseBody(dos, body);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private HttpRequest getHttpRequestInfoByInputStream(InputStream in) throws IOException {
		return HttpRequestUtils.parseHttpRequest(new BufferedReader(new InputStreamReader(in)));
	}

	private byte[] mappingUrl(HttpRequest httpRequest) throws IOException {
		Map<String, String> httpRequestParameter = new HashMap<String, String>();
		if (StringUtils.isEmpty(httpRequest.getUrl())) {
			return "HelloWorld".getBytes();
		}

		if (StringUtils.contains(httpRequest.getUrl(), "/user/create") && StringUtils.equals(httpRequest.getMethod(), "GET")) {
				String[] tokens = StringUtils.split(httpRequest.getUrl(), "?");
				httpRequestParameter = HttpRequestUtils.parseQueryString(tokens[1]);
				User user = new User(httpRequestParameter.get("userId"), httpRequestParameter.get("password"),
					httpRequestParameter.get("name"), httpRequestParameter.get("email"));
				return "Join Success Get Method".getBytes();
		}

		return Files.readAllBytes(new File("./webapp" + httpRequest.getUrl()).toPath());
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
