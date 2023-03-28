package eu.europa.ec.eurostat.wihp.faker;

import java.util.Random;

public class NameFaker {

    private static int LEFT_ALPHA_LIMIT = 97; // letter 'a'

    private static int RIGHT_ALPHA_LIMIT = 122; // letter 'z'

    private static Random random = new Random();

    public static String generateAlphabeticString(int length) {
        return random.ints(LEFT_ALPHA_LIMIT, RIGHT_ALPHA_LIMIT + 1)
            .limit(length)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }
}
