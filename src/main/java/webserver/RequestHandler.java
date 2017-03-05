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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.HttpRequest;
import model.User;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
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
			mappingUrl(dos, getHttpRequestByInputStream(in));
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private HttpRequest getHttpRequestByInputStream(InputStream in) throws IOException {
		return HttpRequestUtils.parseHttpRequest(new BufferedReader(new InputStreamReader(in)));
	}

	private void mappingUrl(DataOutputStream dos, HttpRequest httpRequest) throws IOException {

		if (StringUtils.equals(httpRequest.getUrl(), "/user/create")) {
			Map<String, String> parameters = httpRequest.getParams();
			User user = new User(parameters.get("userId"), parameters.get("password"), parameters.get("name"),
					parameters.get("email"));
			DataBase.addUser(user);
			httpRequest.setUrl("/index.html");
			response302(dos, "/index.html");
			return;
		}

		if (StringUtils.equals(httpRequest.getUrl(), "/user/login")) {
			Map<String, String> parameters = httpRequest.getParams();
			User loginUser = new User(parameters.get("userId"), parameters.get("password"), parameters.get("name"),
					parameters.get("email"));

			if (StringUtils.equals(DataBase.findUserById(loginUser.getUserId()).getPassword(),
					loginUser.getPassword())) {

				httpRequest.setUrl("/index.html");
				response200Logined(dos, httpRequest, true);
				return;
			}

			httpRequest.setUrl("/user/login_fail.html");
			response200Logined(dos, httpRequest, false);
			return;
		}

		if (StringUtils.equals(httpRequest.getUrl(), "/user/list")) {
			if (Boolean.parseBoolean(httpRequest.getCookies().get("logined"))) {
				response200UserList(dos);
				return;
			}

			httpRequest.setUrl("/user/login.html");
			response302(dos, "/user/login");
			return;
		}

		if (StringUtils.contains(httpRequest.getUrl(), "css")) {
			response200(dos, httpRequest, "css");
			return;
		}
		
		if(StringUtils.contains(httpRequest.getUrl(),"js")){
			response200(dos, httpRequest, "js");
			return;
		}

		response200(dos, httpRequest,"html");
	}

	private void response200(DataOutputStream dos, HttpRequest httpRequest, String contentType) throws IOException {
		byte[] body = "HelloWorld".getBytes();
		if (StringUtils.isNotEmpty(httpRequest.getUrl())) {
			body = Files.readAllBytes(new File("./webapp" + httpRequest.getUrl()).toPath());
		}

		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/" + contentType + ";charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + body.length + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		responseBody(dos, body);
	}

	private void response302(DataOutputStream dos, String location) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: " + location + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			e.getStackTrace();
			log.error(e.getMessage());
		}
	}

	private void response200Logined(DataOutputStream dos, HttpRequest httpRequest, boolean isLogin) throws IOException {
		byte[] body = Files.readAllBytes(new File("./webapp" + httpRequest.getUrl()).toPath());
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Set-Cookie: logined=" + isLogin + "\r\n");
			dos.writeBytes("Content-Length: " + body.length + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		responseBody(dos, body);
	}

	private void response200UserList(DataOutputStream dos) throws IOException {
		StringBuilder userListHtml = new StringBuilder();
		userListHtml.append("<!DOCTYPE html><html lang='kr'><head></head><body>");
		for (User user : DataBase.findAll()) {
			userListHtml.append("<tr><td>").append(user.getUserId()).append("</td>");
			userListHtml.append("<td>").append(user.getName()).append("</td>");
			userListHtml.append("<td>").append(user.getEmail()).append("</td></tr>");
		}
		userListHtml.append("</body></html>");

		byte[] body = userListHtml.toString().getBytes();
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + body.length + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		responseBody(dos, body);
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			e.getStackTrace();
			log.error(e.getMessage());
		}
	}
}
