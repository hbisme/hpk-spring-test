package priv.hb.sample.asserts;

import priv.hb.sample.exception.IllegalArgumentException;

public abstract class ArgumentAssert {
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }


    public static void notNull(Object object) {
        notNull(object, "[Illegal Argument] - this argument is required; it must not be null");
    }

    public static void notBlank(String text, String message) {
        int strLen;
        if (text == null || (strLen = text.length()) == 0) {
            throw new IllegalArgumentException(message);
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(text.charAt(i)) == false)) {
                return;
            }
        }
        throw new IllegalArgumentException(message);
    }


    public static void notBlank(String text) {
        notBlank(text, "[Illegal Argument] - this argument is required; it must not be null");
    }

    public static void notNumber(String value) {
        try {
            Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("value can't to int");
        }
    }


}
