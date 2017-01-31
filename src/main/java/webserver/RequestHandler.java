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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.HttpRequestInfo;

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
			HttpRequestInfo httpRequestInfo = new HttpRequestInfo();
			inputstreamPrintAndSetHttpRequest(httpRequestInfo, in);
			byte[] body = mappingUrl(httpRequestInfo);
			response200Header(dos, body.length);
			responseBody(dos, body);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void inputstreamPrintAndSetHttpRequest(HttpRequestInfo httpRequestInfo, InputStream in) {
		int count = 1;
		InputStreamReader inputStreamReader = new InputStreamReader(in);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		try {
			String line;
			while (!"".equals(line = bufferedReader.readLine())) {
				if (line == null) {
					return;
				}

				setHttpRequestInfo(httpRequestInfo, count++, line);
				log.debug("request Info : " + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] mappingUrl(HttpRequestInfo httpRequestInfo) {
		switch (httpRequestInfo.getUrl()) {
			case "/index.html":
				try {
					return Files.readAllBytes(new File("./webapp" + url).toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			default:
				return "Hello World".getBytes();
		}
		return "Hello World".getBytes();
	}

	private void setHttpRequestInfo(HttpRequestInfo httpRequestInfo, int count, String line) {
		String[] tokens;
		switch (count) {
			case 1:
				tokens = line.split(" ");
				httpRequestInfo.setHttpMethod(tokens[0]);
				httpRequestInfo.setUrl(tokens[1]);
				httpRequestInfo.setHttpVersion(tokens[2]);
				break;
			case 2:
				tokens = line.split(": ");
				httpRequestInfo.setHost(tokens[1]);
				break;
			case 3:
				tokens = line.split(": ");
				httpRequestInfo.setConnection(tokens[1]);
				break;
			case 4:
				tokens = line.split(": ");
				httpRequestInfo.setUserAgent(tokens[1]);
				break;
			case 5:
				tokens = line.split(": ");
				httpRequestInfo.setAccept(tokens[1]);
				break;
			case 6:
				tokens = line.split(": ");
				httpRequestInfo.setReferer(tokens[1]);
				break;
			case 7:
				tokens = line.split(": ");
				httpRequestInfo.setAcceptEncoding(tokens[1]);
				break;
			case 8:
				tokens = line.split(": ");
				httpRequestInfo.setAcceptLanguage(tokens[1]);
				break;
			case 9:
				tokens = line.split(": ");
				httpRequestInfo.setIfNoneMatch(tokens[1]);
				break;
			case 10:
				tokens = line.split(": ");
				httpRequestInfo.setIfModifiedSince(tokens[1]);
				break;
		}
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
