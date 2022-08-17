package extra;

public class StringMatcher {
    public static boolean isOk(String subString, String fullString){
        if (subString == null || subString.equals(""))return true;
        String sub = fullString.substring(0, subString.length());
        if (sub.equals(subString))return true;
        return false;
    }
}
