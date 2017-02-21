package model;

import lombok.Data;
import lombok.ToString;

/**
 *  @author young seok.kim
 */
@ToString
@Data
public class HttpResponse {
	private String protocol = "HTTP/1.1";
	private String contentType = "html";
	private StatusCode statusCode;
	
	public HttpResponse(StatusCode statusCode){
		this.statusCode = statusCode;
	}
}
