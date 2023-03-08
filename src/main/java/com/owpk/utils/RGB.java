package com.owpk.utils;

/**
 * Convert com.owpk.RGB colors to palette terminal colors
 */
public class RGB {
    private static final int[] offset = {0, 95, 135, 175, 215, 255};
    private final int[] targets;

    public RGB(float red, float green, float blue) {
        targets = new int[]{Math.round(blue), Math.round(green), Math.round(red)};
    }

    public int convertToXTermColor() {
        int result = 16;
        int deep = 0;
        for (int i = targets.length - 1; i >= 0; i--) {
            if (targets[i] >= offset[1] && i != 0) {
                int multiplier = deep(i, offset.length);
                int localResult = multiplier * findOffset(targets[i]);
                deep += localResult;
            } else if (targets[i] >= offset[i] && i == 0) {
                deep += findOffset(targets[i]);
            }
        }
        return deep + result;
    }

    private int findOffset(int target) {
        int k = 20;
        for (int j = 0; j < offset.length; j++) {
            if (j < offset.length - 1)
                k = (offset[j + 1] - offset[j]) / 2;
            if (target - k < offset[j]) return j;
        }
        return 0;
    }

    private int deep(int ind, int deep) {
        if (ind <= 1) return deep;
        return deep(--ind, deep * 6);
    }

    public String toString() {
        return String.format("R:%d, G:%d, B:%d ", targets[2], targets[1], targets[0]);
    }
}

