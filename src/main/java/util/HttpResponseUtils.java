package util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import model.HttpRequest;

/**
 *  @author young seok.kim
 */
public class HttpResponseUtils {

	public void responseSuccess(DataOutputStream dos, HttpRequest httpRequest, boolean logined) throws IOException{
		byte[] body = Files.readAllBytes(new File("./webapp" + httpRequest.getUrl()).toPath());
		responseSuccess(dos, body.length, logined);
		responseBody(dos, body);
	}

	public void response302(DataOutputStream dos, String location) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: " + location + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			e.getStackTrace();
		}

	}

	public void responseSuccess(DataOutputStream dos, int lengthOfBodyContent, boolean logined) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Set-Cookie: logined=" + logined + "\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			e.getStackTrace();
		}
	}

	public void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			e.getStackTrace();
		}
	}
}
