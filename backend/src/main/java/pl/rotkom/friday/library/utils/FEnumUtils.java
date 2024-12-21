package pl.rotkom.friday.library.utils;

import java.util.List;
import java.util.stream.Stream;

public class FEnumUtils {

    public static List<String> toStringList(Enum<?>[] values) {
        return Stream.of(values)
                .map(Enum::name)
                .toList();
    }
}
