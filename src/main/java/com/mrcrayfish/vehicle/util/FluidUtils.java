package com.mrcrayfish.vehicle.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mrcrayfish.vehicle.client.render.util.ColorHelper;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.EnumMap;

/**
 * Author: MrCrayfish
 */
public class FluidUtils
{
    private static final Object2IntMap<ResourceLocation> CACHE_FLUID_COLOR = Util.make(() -> {
        Object2IntMap<ResourceLocation> map = new Object2IntOpenHashMap<>();
        map.defaultReturnValue(-1);
        return map;
    });

    @OnlyIn(Dist.CLIENT)
    public static void clearCacheFluidColor()
    {
        CACHE_FLUID_COLOR.clear();
    }

    @OnlyIn(Dist.CLIENT)
    public static int getAverageFluidColor(Fluid fluid)
    {
        ResourceLocation key = ForgeRegistries.FLUIDS.getKey(fluid);
        int color = CACHE_FLUID_COLOR.getInt(key);

        if(color == -1)
        {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluid).getStillTexture());
            if(sprite != null)
            {
                int area = sprite.getWidth() * sprite.getHeight();

                int r = 0;
                int g = 0;
                int b = 0;

                int maxX = sprite.getHeight();
                int maxY = sprite.getWidth();

                for (int x2 = 0; x2 <= maxX; x2++)
                {
                    for (int y2 = 0; y2 <= maxY; y2++)
                    {
                        int pixel = sprite.getPixelRGBA(0, x2, y2);

                        r += ColorHelper.unpackARGBRed(pixel);
                        g += ColorHelper.unpackARGBGreen(pixel);
                        b += ColorHelper.unpackARGBBlue(pixel);
                    }
                }

                CACHE_FLUID_COLOR.put(key, color = ColorHelper.packARGBRed(r / area, g / area, b / area, 0xFF));
            }
        }

        return color;
    }

    public static int transferFluid(IFluidHandler source, IFluidHandler target, int maxAmount)
    {
        FluidStack drained = source.drain(maxAmount, IFluidHandler.FluidAction.SIMULATE);
        if(drained.getAmount() > 0)
        {
            int filled = target.fill(drained, IFluidHandler.FluidAction.SIMULATE);
            if(filled > 0)
            {
                drained = source.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                return target.fill(drained, IFluidHandler.FluidAction.EXECUTE);
            }
        }
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawFluidTankInGUI(FluidStack fluid, double x, double y, double percent, int height)
    {
        if(fluid == null || fluid.isEmpty())
            return;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture());
        if(sprite != null)
        {
            float minU = sprite.getU0();
            float maxU = sprite.getU1();
            float minV = sprite.getV0();
            float maxV = sprite.getV1();
            float deltaV = maxV - minV;
            double tankLevel = percent * height;

            Minecraft.getInstance().getTextureManager().bindForSetup(InventoryMenu.BLOCK_ATLAS);

            RenderSystem.enableBlend();
            int count = 1 + ((int) Math.ceil(tankLevel)) / 16;
            for(int i = 0; i < count; i++)
            {
                double subHeight = Math.min(16.0, tankLevel - (16.0 * i));
                double offsetY = height - 16.0 * i - subHeight;
                drawQuad(x, y + offsetY, 16, subHeight, minU, (float) (maxV - deltaV * (subHeight / 16.0)), maxU, maxV);
            }
            RenderSystem.disableBlend();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void drawQuad(double x, double y, double width, double height, float minU, float minV, float maxU, float maxV)
    {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(x, y + height, 0).uv(minU, maxV).endVertex();
        buffer.vertex(x + width, y + height, 0).uv(maxU, maxV).endVertex();
        buffer.vertex(x + width, y, 0).uv(maxU, minV).endVertex();
        buffer.vertex(x, y, 0).uv(minU, minV).endVertex();
        tessellator.end();
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawFluidInWorld(FluidTank tank, Level world, BlockPos pos, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, float x, float y, float z, float width, float height, float depth, int light, FluidSides sides)
    {
        if(tank.isEmpty())
            return;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(tank.getFluid().getFluid()).getStillTexture());

        int waterColor = IClientFluidTypeExtensions.of(tank.getFluid().getFluid()).getTintColor(tank.getFluid().getFluid().defaultFluidState(), world, pos);

        float red = ColorHelper.normalize(ColorHelper.unpackARGBRed(waterColor));
        float green = ColorHelper.normalize(ColorHelper.unpackARGBGreen(waterColor));
        float blue = ColorHelper.normalize(ColorHelper.unpackARGBBlue(waterColor));

        float side = 0.9F;
        float minU = sprite.getU0();
        float maxU = Math.min(minU + (sprite.getU1() - minU) * depth, sprite.getU1());
        float minV = sprite.getV0();
        float maxV = Math.min(minV + (sprite.getV1() - minV) * height, sprite.getV1());

        VertexConsumer buffer = renderTypeBuffer.getBuffer(RenderType.translucent());
        Matrix4f matrix = matrixStack.last().pose();

        //left side
        if(sides.test(Direction.WEST))
        {
            buffer.vertex(matrix, x + width, y, z)
                    .color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F)
                    .uv(maxU, minV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();

            buffer.vertex(matrix, x, y, z)
                    .color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F)
                    .uv(minU, minV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();

            buffer.vertex(matrix, x, y + height, z)
                    .color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F)
                    .uv(minU, maxV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();

            buffer.vertex(matrix, x + width, y + height, z)
                    .color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F)
                    .uv(maxU, maxV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
        }

        //right side
        if(sides.test(Direction.EAST))
        {
            buffer.vertex(matrix, x, y, z + depth)
                    .color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F)
                    .uv(maxU, minV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();

            buffer.vertex(matrix, x + width, y, z + depth)
                    .color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F)
                    .uv(minU, minV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();

            buffer.vertex(matrix, x + width, y + height, z + depth)
                    .color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F)
                    .uv(minU, maxV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();

            buffer.vertex(matrix, x, y + height, z + depth)
                    .color(red - 0.25F, green - 0.25F, blue - 0.25F, 1.0F)
                    .uv(maxU, maxV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
        }

        maxU = Math.min(minU + (sprite.getU1() - minU) * depth, sprite.getU1());

        if(sides.test(Direction.SOUTH))
        {
            buffer.vertex(matrix, x + width, y, z + depth)
                    .color(red * side, green * side, blue * side, 1.0F)
                    .uv(maxU, minV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();

            buffer.vertex(matrix, x + width, y, z)
                    .color(red * side, green * side, blue * side, 1.0F)
                    .uv(minU, minV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();

            buffer.vertex(matrix, x + width, y + height, z)
                    .color(red * side, green * side, blue * side, 1.0F)
                    .uv(minU, maxV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();

            buffer.vertex(matrix, x + width, y + height, z + depth)
                    .color(red * side, green * side, blue * side, 1.0F)
                    .uv(maxU, maxV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
        }

        if(sides.test(Direction.NORTH))
        {
            buffer.vertex(matrix, x, y, z)
                    .color(red * side, green * side, blue * side, 1.0F)
                    .uv(minU, minV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();

            buffer.vertex(matrix, x, y, z + depth)
                    .color(red * side, green * side, blue * side, 1.0F)
                    .uv(maxU, minV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();

            buffer.vertex(matrix, x, y + height, z + depth)
                    .color(red * side, green * side, blue * side, 1.0F)
                    .uv(maxU, maxV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();

            buffer.vertex(matrix, x, y + height, z)
                    .color(red * side, green * side, blue * side, 1.0F)
                    .uv(minU, maxV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
        }

        maxV = Math.min(minV + (sprite.getV1() - minV) * width, sprite.getV1());

        if(sides.test(Direction.UP))
        {
            buffer.vertex(matrix, x, y + height, z)
                    .color(red, green, blue, 1.0F)
                    .uv(maxU, minV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();

            buffer.vertex(matrix, x, y + height, z + depth)
                    .color(red, green, blue, 1.0F)
                    .uv(minU, minV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();

            buffer.vertex(matrix, x + width, y + height, z + depth)
                    .color(red, green, blue, 1.0F)
                    .uv(minU, maxV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();

            buffer.vertex(matrix, x + width, y + height, z)
                    .color(red, green, blue, 1.0F)
                    .uv(maxU, maxV)
                    .uv2(light)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
        }
    }

    public static class FluidSides
    {
        private final EnumMap<Direction, Boolean> map = new EnumMap<>(Direction.class);

        public FluidSides(Direction ... sides)
        {
            for(Direction direction : Direction.values())
            {
                this.map.put(direction, false);
            }

            for(Direction side : sides)
            {
                this.map.put(side, true);
            }
        }

        public boolean test(Direction direction)
        {
            return this.map.get(direction);
        }
    }
}
