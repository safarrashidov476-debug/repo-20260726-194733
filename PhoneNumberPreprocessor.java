package com.github.olga_yakovleva.rhvoice.android;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RHVoice sintez qilishdan oldin matndagi raqamlarni o'qishga qulay
 * guruhlarga ajratadi. Har doim yoniq - o'chirib bo'lmaydi:
 *
 *   +998949835707      ->  +998 94 983 57 07   (telefon raqami)
 *   949835707          ->  94 983 57 07        (telefon raqami)
 *   9860082546068113   ->  98 60 08 25 46 06 81 13   (16 xonali karta raqami)
 *   9403               ->  94 03               (4 xonali SMS kod)
 */
final class PhoneNumberPreprocessor {

    // +998 bilan boshlangan telefon raqami
    private static final Pattern INTL_PATTERN =
        Pattern.compile("\\+998(\\d{2})(\\d{3})(\\d{2})(\\d{2})\\b");

    // Mahalliy 9 xonali telefon raqami
    private static final Pattern LOCAL_9_PATTERN =
        Pattern.compile("(?<!\\d)(\\d{2})(\\d{3})(\\d{2})(\\d{2})(?!\\d)");

    // 16 xonali karta raqami - 8 ta 2talik guruhga bo'linadi
    private static final Pattern CARD_16_PATTERN =
        Pattern.compile("(?<!\\d)(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(?!\\d)");

    // Mustaqil 4 xonali raqam - SMS tasdiqlash kodi
    private static final Pattern CODE_4_PATTERN =
        Pattern.compile("(?<!\\d)(\\d{2})(\\d{2})(?!\\d)");

    private PhoneNumberPreprocessor() {
    }

    static String process(String text) {
        if (text == null || text.isEmpty())
            return text;

        String result = applyGroups(text, INTL_PATTERN, "+998 ", 4);
        result = applyLocal9(result);
        result = applyCard16(result);
        result = apply4DigitCode(result);
        return result;
    }

    private static String applyGroups(String text, Pattern pattern, String prefix, int groupCount) {
        Matcher matcher = pattern.matcher(text);
        StringBuilder sb = new StringBuilder();
        int lastEnd = 0;
        while (matcher.find()) {
            sb.append(text, lastEnd, matcher.start());
            sb.append(prefix);
            for (int i = 1; i <= groupCount; i++) {
                sb.append(matcher.group(i));
                if (i < groupCount)
                    sb.append(' ');
            }
            lastEnd = matcher.end();
        }
        sb.append(text.substring(lastEnd));
        return sb.toString();
    }

    private static String applyLocal9(String text) {
        Matcher matcher = LOCAL_9_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        int lastEnd = 0;
        while (matcher.find()) {
            int start = matcher.start();
            if (start >= 5 && text.startsWith("+998 ", start - 5)) {
                sb.append(text, lastEnd, matcher.end());
                lastEnd = matcher.end();
                continue;
            }
            sb.append(text, lastEnd, start);
            sb.append(matcher.group(1)).append(' ')
              .append(matcher.group(2)).append(' ')
              .append(matcher.group(3)).append(' ')
              .append(matcher.group(4));
            lastEnd = matcher.end();
        }
        sb.append(text.substring(lastEnd));
        return sb.toString();
    }

    private static String applyCard16(String text) {
        Matcher matcher = CARD_16_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        int lastEnd = 0;
        while (matcher.find()) {
            sb.append(text, lastEnd, matcher.start());
            for (int i = 1; i <= 8; i++) {
                sb.append(matcher.group(i));
                if (i < 8)
                    sb.append(' ');
            }
            lastEnd = matcher.end();
        }
        sb.append(text.substring(lastEnd));
        return sb.toString();
    }

    private static String apply4DigitCode(String text) {
        Matcher matcher = CODE_4_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        int lastEnd = 0;
        while (matcher.find()) {
            sb.append(text, lastEnd, matcher.start());
            sb.append(matcher.group(1)).append(' ').append(matcher.group(2));
            lastEnd = matcher.end();
        }
        sb.append(text.substring(lastEnd));
        return sb.toString();
    }
}
