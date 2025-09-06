package com.goormthon.hero_home.domain.common;

public class PercentUtils {
    public static int calculatePercent(int currentAmount, int targetAmount) {
        if (targetAmount <= 0) return 0;
        return (int) Math.min(100, Math.floor(((double) currentAmount / targetAmount) * 100));
    }
}
