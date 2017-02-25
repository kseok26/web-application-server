package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import model.HttpRequest;

public class HttpRequestUtils {
	private static final Logger log = LoggerFactory.getLogger(HttpRequestUtils.class);
	/**
	 * @param queryString은
	 *            URL에서 ? 이후에 전달되는 field1=value1&field2=value2 형식임
	 * @return
	 */
	public static Map<String, String> parseQueryString(String queryString) {
		return parseValues(queryString, "&");
	}

	/**
	 * @param 쿠키
	 *            값은 name1=value1; name2=value2 형식임
	 * @return
	 */
	public static Map<String, String> parseCookies(String cookies) {
		return parseValues(cookies, ";");
	}

	private static Map<String, String> parseValues(String values, String separator) {
		if (Strings.isNullOrEmpty(values)) {
			return Maps.newHashMap();
		}

		String[] tokens = values.split(separator);
		return Arrays.stream(tokens).map(t -> getKeyValue(t, "=")).filter(p -> p != null)
				.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
	}

	static Pair getKeyValue(String keyValue, String regex) {
		if (Strings.isNullOrEmpty(keyValue)) {
			return null;
		}

		String[] tokens = keyValue.split(regex);
		if (tokens.length != 2) {
			return null;
		}

		return new Pair(tokens[0], tokens[1]);
	}

	public static HttpRequest parseHttpRequest(BufferedReader bufferedReader) throws IOException {

		String line;
		boolean isFirstLine = true;
		HttpRequest httpRequest = new HttpRequest();

		while (StringUtils.isEmpty(line = bufferedReader.readLine()) == false) {
			System.out.println(line);
			if (isFirstLine) {
				String[] tokens = StringUtils.split(line, " ");
				httpRequest.setMethod(tokens[0]);
				
				if (StringUtils.equals(tokens[0], "POST")) {
					log.debug(tokens[1]);
					httpRequest.setUrl(tokens[1]);
				} else {
					if (StringUtils.contains(tokens[1], '?')) {
						String[] seperateUrlAndParam = StringUtils.split(tokens[1], "?");
						httpRequest.setUrl(seperateUrlAndParam[0]);
						httpRequest.setParams(parseQueryString(seperateUrlAndParam[1]));
					} else {
						httpRequest.setUrl(tokens[1]);
					}
				}

				httpRequest.setHttpVersion(tokens[2]);
				isFirstLine = false;
				continue;
			}
			
			Pair pair = parseHeader(line);
			switch (pair.getKey()) {
			case "Host":
				httpRequest.setHost(pair.getValue());
				break;
			case "Connection":
				httpRequest.setConnection(pair.getValue());
				break;
			case "User-Agent":
				httpRequest.setUserAgent(pair.getValue());
				break;
			case "Accept":
				httpRequest.setAccept(pair.getValue());
				break;
			case "Referer":
				httpRequest.setReferer(pair.getValue());
				break;
			case "Accept-Encoding":
				httpRequest.setAcceptEncoding(pair.getValue());
				break;
			case "Accept-Language":
				httpRequest.setAcceptLanguage(pair.getValue());
				break;
			case "Content-Length":
				httpRequest.setContentLength(Integer.parseInt(pair.getValue()));
				break;
			case "Cookie":
				httpRequest.setCookies(parseCookies(pair.getValue()));
				break;
			}
		}

		if (StringUtils.equals(httpRequest.getMethod(), "POST") && httpRequest.getContentLength() != 0) {
			String body = IOUtils.readData(bufferedReader, httpRequest.getContentLength());
			System.out.println(body);
			httpRequest.setParams(parseQueryString(body));
		}

		return httpRequest;
	}

	public static Pair parseHeader(String header) {
		return getKeyValue(header, ": ");
	}

	public static class Pair {
		String key;
		String value;

		Pair(String key, String value) {
			this.key = key.trim();
			this.value = value.trim();
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Pair [key=" + key + ", value=" + value + "]";
		}
	}
}
