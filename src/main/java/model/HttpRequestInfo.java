package model;

import lombok.Data;
import lombok.ToString;

/**
 *  @author young seok.kim
 */
@Data
@ToString
public class HttpRequestInfo {
	private String httpMethod;
	private String url;
	private String httpVersion;
	private String host;
	private String connection;
	private String userAgent;
	private String accept;
	private String referer;
	private String acceptEncoding;
	private String acceptLanguage;
	private String ifNoneMatch;
	private String ifModifiedSince;
}
