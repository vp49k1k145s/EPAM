package enums;

import java.util.Arrays;
import java.util.Optional;

public enum Browser {
    CHROME("chrome"),
    FIREFOX("firefox"),
    OPERA("opera");

    private final String value;

    public String getValue() {
        return value;
    }

    Browser(String value) {
        this.value = value;
    }

    public static Browser getByValue(String value) {
        Optional<Browser> browser = filterByValue(value);
        return browser.orElseGet(browser::get);
    }

    private static Optional<Browser> filterByValue(String value) {
        return Arrays.stream(Browser.values()).filter(browser -> browser.getValue().equalsIgnoreCase(value)).findAny();
    }
}