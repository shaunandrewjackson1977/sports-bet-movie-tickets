package au.com.sportsbet.movietickets.service;

record Range(int minInclusive, int maxInclusive) {
    Range {
        if (minInclusive > maxInclusive) {
            throw new IllegalArgumentException("min must be <= max");
        }
    }

    boolean contains(int value) {
        return value >= minInclusive && value <= maxInclusive;
    }
}
