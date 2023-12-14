package org.example.experiments;

import java.util.List;

public class MathUtil {
    public static double getAverage(List<Double> numbers) {
        return numbers.stream()
                .mapToDouble(x -> x)
                .average()
                .orElseThrow(() -> new IllegalArgumentException("Can't get average number from list!!!"));
    }
}
