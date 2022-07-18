package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.entity.EntityJack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Author: MrCrayfish
 */
public class JackRenderer extends EntityRenderer<EntityJack>
{
    public JackRenderer(EntityRendererManager renderManager)
    {
        super(renderManager);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityJack entity)
    {
        return null;
    }

    @Override
    public void render(@NotNull EntityJack jack, float p_225623_2_, float partialTicks, @NotNull MatrixStack matrixStack, @NotNull IRenderTypeBuffer renderTypeBuffer, int light) {}
}
