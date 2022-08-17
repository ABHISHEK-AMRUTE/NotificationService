package com.abhishek.notificationservice.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberHelper {
    /**
     *
     * @param phoneNumber
     * @return boolean value
     * Returns true if the phoneNumber is a valid phone numbers else false
     * Phone number can start with country code having 1-3 digit, followed by 10 digit mobile Number
     * Some valid numbers +91-2323443422, +91-(232)-(344)-3422 etc...
     *
     */
    public static Boolean isValidPhoneNumber( String phoneNumber ){
        Pattern mobilePatter = Pattern.compile("^(\\+\\d{1,3}( )?)?[- .]?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$");
        Matcher matcher = mobilePatter.matcher(phoneNumber);
        return matcher.matches();
    }
}
