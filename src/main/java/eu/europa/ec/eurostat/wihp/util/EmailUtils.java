package eu.europa.ec.eurostat.wihp.util;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class EmailUtils {

    private EmailUtils() {
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}
