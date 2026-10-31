package org.javahub.spamshield.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Author: Mohammed Anas Date: 30/10/2026 */
public class PhoneNumberExtractor {
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\+[1-9]\\d{1,14}");

    public static List<String> getPhoneNumbers(String text) {

        List<String> phoneNumbers = new ArrayList<>();
        Matcher matcher = PHONE_PATTERN.matcher(text);
        while(matcher.find()){
            phoneNumbers.add(matcher.group());
        }
        return phoneNumbers;
    }
}
