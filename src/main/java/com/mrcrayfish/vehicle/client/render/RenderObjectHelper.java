package com.mrcrayfish.vehicle.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mrcrayfish.vehicle.client.render.util.ColorHelper;
import com.mrcrayfish.vehicle.client.render.util.ModelQuadUtil;
import com.mrcrayfish.vehicle.client.util.OptifineHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;

/**
 * @author Mo0dss
 * Same as the old RenderUtil but alot more clean and useful
 */
@OnlyIn(Dist.CLIENT)
public class RenderObjectHelper
{
    protected static final RandomSource RANDOM = new XoroshiroRandomSource(42L);
    private static final float NORM = 1.0F / 127.0F;
    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    public static BakedModel getModel(ItemStack stack)
    {
        return MINECRAFT.getItemRenderer().getItemModelShaper().getItemModel(stack);
    }

    public static void renderColoredModel(BakedModel model, ItemTransforms.TransformType transformType, boolean leftHanded, PoseStack matrices, MultiBufferSource renderTypeBuffer, int color, int overlay, int light)
    {
        matrices.pushPose();
        {
            model = ForgeHooksClient.handleCameraTransforms(matrices, model, transformType, leftHanded);
            matrices.translate(-0.5, -0.5, -0.5);

            if(!model.isCustomRenderer())
            {
                PoseStack.Pose pose = matrices.last();
                RenderType type = Sheets.cutoutBlockSheet();
                VertexConsumer vertexBuilder = renderTypeBuffer.getBuffer(type);

                renderModel(pose, vertexBuilder, type, ItemStack.EMPTY, model, color, overlay, light);
            }
        }
        matrices.popPose();
    }

    public static void renderDamagedVehicleModel(BakedModel model, ItemTransforms.TransformType transformType, boolean leftHanded, PoseStack matrices, int stage, int color, int overlay, int light)
    {
        matrices.pushPose();
        {
            model = ForgeHooksClient.handleCameraTransforms(matrices, model, transformType, leftHanded);
            matrices.translate(-0.5, -0.5, -0.5);

            if(!model.isCustomRenderer())
            {
                PoseStack.Pose pose = matrices.last();
                RenderType type = ModelBakery.DESTROY_TYPES.get(stage);
                renderModel(pose, new SheetedDecalTextureGenerator(
                        MINECRAFT.renderBuffers().crumblingBufferSource().getBuffer(type),
                        pose.pose(), pose.normal()), type, ItemStack.EMPTY, model, color, overlay, light);
            }
        }
        matrices.popPose();
    }

    public static void renderModel(ItemStack stack, ItemTransforms.TransformType transformType, boolean leftHanded, PoseStack matrices, MultiBufferSource renderTypeBuffer, int overlay, int light, BakedModel model)
    {
        if(!stack.isEmpty())
        {
            matrices.pushPose();
            {
                boolean isGui = transformType == ItemTransforms.TransformType.GUI;
                boolean tridentFlag = isGui || transformType == ItemTransforms.TransformType.GROUND || transformType == ItemTransforms.TransformType.FIXED;

                if(stack.is(Items.TRIDENT) && tridentFlag)
                {
                    model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
                }
                else if (stack.is(Items.SPYGLASS))
                {
                    model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation("minecraft:spyglass#inventory"));
                }

                model = ForgeHooksClient.handleCameraTransforms(matrices, model, transformType, leftHanded);
                matrices.translate(-0.5, -0.5, -0.5);

                if(!model.isCustomRenderer() && (!stack.is(Items.TRIDENT) || tridentFlag))
                {
                    boolean fabulous = true;
                    if (!isGui && !transformType.firstPerson() && stack.getItem() instanceof BlockItem)
                    {
                        Block block = ((BlockItem) stack.getItem()).getBlock();
                        fabulous = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
                    }

                    PoseStack.Pose pose = matrices.last();
                    for (var subModel : model.getRenderPasses(stack, fabulous))
                    {
                        for (var subRenderType : subModel.getRenderTypes(stack, fabulous))
                        {
                            VertexConsumer buffer = fabulous ?
                                    ItemRenderer.getFoilBufferDirect(renderTypeBuffer, subRenderType, true, stack.hasFoil()) :
                                    ItemRenderer.getFoilBuffer(renderTypeBuffer, subRenderType, true, stack.hasFoil());

                            renderModel(pose, buffer, subRenderType, stack, model, -1, overlay, light);
                        }
                    }
                }
                else
                {
                    IClientItemExtensions.of(stack).getCustomRenderer().renderByItem(stack, transformType, matrices, renderTypeBuffer, light, overlay);
                }
            }
            matrices.popPose();
        }
    }

    private static void renderModel(PoseStack.Pose pose, VertexConsumer consumer, RenderType type, ItemStack stack, BakedModel model, int color, int overlay, int light)
    {
        RandomSource random = RANDOM;

        for(Direction direction : Direction.values())
        {
            random.setSeed(42L);
            renderQuadList(pose, consumer, model.getQuads(null, direction, random, ModelData.EMPTY, type), stack, color, overlay, light);
        }

        random.setSeed(42L);
        renderQuadList(pose, consumer, model.getQuads(null, null, random, ModelData.EMPTY, type), stack, color, overlay, light);
    }

    protected static void renderQuadList(PoseStack.Pose pose, VertexConsumer consumer, List<BakedQuad> quads, ItemStack stack, int color, int overlay, int light)
    {
        // This is a very hot allocation, iterate over it manually
        // noinspection ForLoopReplaceableByForEach
        for (int i = 0, quadsSize = quads.size(); i < quadsSize; i++)
        {
            BakedQuad quad = quads.get(i);

            if(OptifineHelper.isEmissiveTexturesEnabled())
            {
                quad = OptifineHelper.castAsEmissive(quad);

                if (quad == null)
                {
                    continue;
                }
            }

            if(quad.isTinted())
            {
                if(!stack.isEmpty() && color == -1)
                {
                    color = MINECRAFT.getItemColors().getColor(stack, quad.getTintIndex());
                }

                if (OptifineHelper.isCustomColorsEnabled())
                {
                    color = OptifineHelper.castAsCustomColor(stack, quad.getTintIndex(), color);
                }
            }

            renderQuad(consumer, pose, quad, color, overlay, light);
        }

    }

    protected static void renderQuad(VertexConsumer consumer, PoseStack.Pose pose,
                                     BakedQuad quad, int color, int overlay, int light)
    {
        for(int i = 0; i < 4; i++)
        {
            float x = ModelQuadUtil.getX(quad, i);
            float y = ModelQuadUtil.getX(quad, i);
            float z = ModelQuadUtil.getX(quad, i);

            int quadColor = ModelQuadUtil.getColor(quad, i);

            float oR = ColorHelper.normalize(ColorHelper.unpackARGBRed(quadColor));
            float oG = ColorHelper.normalize(ColorHelper.unpackARGBGreen(quadColor));
            float oB = ColorHelper.normalize(ColorHelper.unpackARGBBlue(quadColor));

            oR *= ColorHelper.normalize(ColorHelper.unpackARGBRed(color));
            oG *= ColorHelper.normalize(ColorHelper.unpackARGBGreen(color));
            oB *= ColorHelper.normalize(ColorHelper.unpackARGBBlue(color));

            float u = ModelQuadUtil.getTexU(quad, i);
            float v = ModelQuadUtil.getTexV(quad, i);

            int norm = ModelQuadUtil.getNormal(quad, i);

            consumer.vertex(pose.pose(), x, y, z)
                    .uv(u, v)
                    .color(ColorHelper.packARGBRed(oR, oG, oB, 1F))
                    .overlayCoords(overlay)
                    .uv2(light)
                    .normal(pose.normal(), (norm & 0xFF) * NORM, ((norm >> 8) * 0xFF) * NORM, ((norm >> 16) & 0xFF) * NORM)
                    .endVertex();
        }
    }
}
