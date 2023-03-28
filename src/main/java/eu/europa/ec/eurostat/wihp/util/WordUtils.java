package eu.europa.ec.eurostat.wihp.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordUtils {

    private WordUtils() {
    }

    public static String capitalizeFirstLetter(String word) {
        return StringUtils.capitalize(StringUtils.lowerCase(word));
    }

    public static Optional<String> extractWordInBetween(String input, String leftText, String rightText) {
        String pattern = "(?<=\\b" + leftText + "\\b).*?(?=\\b" + rightText + "\\b)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(input);
        if (m.find()) {
            return Optional.of(m.group());
        }
        return Optional.empty();
    }

    public static List<String> toListOfStrings(String value) {
        return Arrays.stream(value.split("-"))
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
    }

}
