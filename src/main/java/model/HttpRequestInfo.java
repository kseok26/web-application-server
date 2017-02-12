package model;

import lombok.Data;
import lombok.ToString;

/**
 *  @author young seok.kim
 */
@Data
@ToString
public class HttpRequestInfo {
	private String url;
	private String method;
}
