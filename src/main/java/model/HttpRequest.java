package model;

import java.util.Map;

import lombok.Data;
import lombok.ToString;

/**
 *  @author young seok.kim
 */
@Data
@ToString
public class HttpRequest {
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
	private Map<String, String> cookies;
	private Map<String, String> params;
	
	
}
