package exception;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExceptionImpl {

    public static boolean isValidEmail(String email) {
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isValidNumber(String number) {
        String regex = "^[0-9]+(-)+[0-9]+(-)+[0-9]*$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(number);
        if (m.matches()) {
            return false;
        }
        return true;
    }

//    public static boolean isExistName(String name, Map<String, Address> people) {
//        Set<String> keySet = people.keySet();
//        for(String a : keySet) {
//            if(name.equals(people.get(a).getName())) return true;
//        }
//
//        return false;
//    }
//
//    public static boolean isExistNumber(String number, Map<String, Address> people) {
//        Set<String> keySet = people.keySet();
//        for(String a : keySet) {
//            if(number.equals(people.get(a).getPhonNumber())) return true;
//        }
//        return false;
//    }
}
