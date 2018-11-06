package utils;

public class DelimiterUtils {
	
	public static String addDelimiter(String arg) {
		return arg+"$_";
	}
	
	public static String removeDelimiter(String arg) {
		return arg.substring(0,arg.length()-2);
	}
}
