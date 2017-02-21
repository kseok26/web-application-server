package model;

/**
 *  @author young seok.kim
 */
public enum StatusCode {
	OK(200,"Ok"),
	REDIRECT(302,"Found");

	private int code;
	private String status;

	private StatusCode(int code,  String status){
		this.code = code;
		this.status = status;
	}

	@Override
	public String toString(){
		return code + " " +status;
	}
}
