package com.mrcrayfish.vehicle.client.render.util;

import net.minecraft.client.renderer.block.model.BakedQuad;

public class ModelQuadUtil
{
    public static float getX(BakedQuad quad, int idx)
    {
        return Float.intBitsToFloat(quad.getVertices()[idx * 8]);
    }

    public static float getY(BakedQuad quad, int idx)
    {
        return Float.intBitsToFloat(quad.getVertices()[idx * 8 + 1]);
    }

    public static float getZ(BakedQuad quad, int idx)
    {
        return Float.intBitsToFloat(quad.getVertices()[idx * 8 + 2]);
    }

    public static float getTexU(BakedQuad quad, int idx)
    {
        return Float.intBitsToFloat(quad.getVertices()[idx * 8 + 4]);
    }

    public static float getTexV(BakedQuad quad, int idx)
    {
        return Float.intBitsToFloat(quad.getVertices()[idx * 8 + 5]);
    }

    public static int getColor(BakedQuad quad, int idx)
    {
        return quad.getVertices()[idx * 8 + 3];
    }

    public static int getNormal(BakedQuad quad, int idx)
    {
        return quad.getVertices()[idx * 8 + 7];
    }
}
