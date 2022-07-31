package com.mrcrayfish.vehicle.client.render.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class RenderUtil
{
    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    /**
     * Draws a rectangle with a horizontal gradient between the specified colors (ARGB format).
     */
    public static void drawGradientRectHorizontal(int left, int top, int right, int bottom, int leftColor, int rightColor)
    {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(right, top, 0).color(rightColor).endVertex();
        bufferbuilder.vertex(left, top, 0).color(leftColor).endVertex();
        bufferbuilder.vertex(left, bottom, 0).color(leftColor).endVertex();
        bufferbuilder.vertex(right, bottom, 0).color(rightColor).endVertex();

        tesselator.end();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void scissor(int x, int y, int width, int height) //TODO might need fixing. I believe I rewrote this in a another mod
    {
        Minecraft mc = Minecraft.getInstance();
        int scale = (int) mc.getWindow().getGuiScale();
        GL11.glScissor(x * scale, mc.getWindow().getScreenHeight() - y * scale - height * scale, Math.max(0, width * scale), Math.max(0, height * scale));
    }

    public static List<Component> lines(FormattedText text, int maxWidth)
    {
        List<FormattedText> lines = MINECRAFT.font.getSplitter().splitLines(text, maxWidth, Style.EMPTY);
        return lines.stream().map(t -> Component.literal(t.getString()).withStyle(ChatFormatting.GRAY)).collect(Collectors.toList());
    }
}
