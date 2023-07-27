package com.shortener.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidatorUtil {

    private static final String URL_REGEX_PATTERN =  "((http|https)://)(www.)?"
        + "[a-zA-Z0-9@:%._\\+~#?&//=]"
        + "{2,256}\\.[a-z]"
        + "{2,6}\\b([-a-zA-Z0-9@:%"
        + "._\\+~#?&//=]*)";

    public boolean isValidUrl(String longUrl)
    {
        Pattern pattern = Pattern.compile(URL_REGEX_PATTERN);
        Matcher matcher = pattern.matcher(longUrl);
        return matcher.matches();
    }

}
