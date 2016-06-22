package models;

public class Util {
	public static String replaceString(String str){
		str = str.replace("%", "\\%");
		str = str.replace("_", "\\_");
		str = str.replace("'", "''");
		return str;
	}
}
