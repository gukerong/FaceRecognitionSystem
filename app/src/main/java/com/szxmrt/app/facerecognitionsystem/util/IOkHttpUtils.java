package com.szxmrt.app.facerecognitionsystem.util;

/**
 *
 * Created by Administrator on 2018-06-23
 */

public interface IOkHttpUtils {
	public static String HTML = "text/html";     //html格式
	public static String PLAIN = "text/plain";        //纯文本格式
	public static String TEXT_XML = "text/xml";     //xml格式
	public static String GIF = "image/gif";      //gif图片格式
	public static String JPG = "image/jpeg";      //jpg图片格式
	public static String PNG = "image/png";      //png图片格式
	//<form encType="">中默认的encType，form表单数据被编码为key/value格式发送到服务器（表单默认的提交数据的格式）
	public static String URLENCODED = "application/x-www-form-urlencoded; charset=utf-8";
	public static String DATA = "multipart/form-data";     //当你需要在表单中进行文件上传时，就需要使用该格式
	public static String XHTML = "application/xhtml+xml";        //xhtml格式
	public static String APPLICATION_XML = "application/xml";      //xml格式
	public static String ATOM_XML = "application/atom+xml";     //atom xml聚合格式
	public static String JSON = "application/json; charset=utf-8";     //json数据格式
	public static String PDF = "application/pdf";      //pdf格式
	public static String MSWORD = "application/msword";       //word格式
	public static String OCTET_STREAM = "application/octet-stream";     //二进制流数据（如常见的文件下载）
	public static String MARKDOWN = "text/x-markdown; charset=utf-8";
}
