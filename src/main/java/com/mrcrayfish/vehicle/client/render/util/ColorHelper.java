package com.mrcrayfish.vehicle.client.render.util;

public class ColorHelper
{
    public static final float COMPONENT_RANGE = 255.0F;
    public static final float NORM = 1.0F / COMPONENT_RANGE;

    public static int packARGBRed(float r, float g, float b, float a) {
        return packARGBRed((int) (r * COMPONENT_RANGE), (int) (g * COMPONENT_RANGE), (int) (b * COMPONENT_RANGE), (int) (a * COMPONENT_RANGE));
    }

    public static int packARGBRed(int red, int green, int blue, int alpha)
    {
        return (alpha & 0xFF) << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF);
    }

    public static int unpackARGBAlpha(int color)
    {
        return (color >> 24) & 0xFF;
    }

    public static int unpackARGBRed(int color)
    {
        return (color >> 16) & 0xFF;
    }

    public static int unpackARGBGreen(int color)
    {
        return (color >> 8) & 0xFF;
    }
    public static int unpackARGBBlue(int color)
    {
        return color & 0xFF;
    }

    public static float normalize(int component)
    {
        return (component & 0xFF) * NORM;
    }
}
