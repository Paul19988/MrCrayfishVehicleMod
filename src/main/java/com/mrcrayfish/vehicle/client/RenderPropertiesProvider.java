package com.mrcrayfish.vehicle.client;

import com.mojang.math.Vector3f;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.client.render.util.ColorHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;

public class RenderPropertiesProvider
{
    public static final IClientFluidTypeExtensions BLAZE_JUICE = new IClientFluidTypeExtensions()
    {
        static final ResourceLocation STILL_TEXTURE = new ResourceLocation(Reference.MOD_ID, "block/blaze_juice_still");
        static final ResourceLocation FLOWING_TEXTURE = new ResourceLocation(Reference.MOD_ID, "block/blaze_juice_flowing");
        static final int PACKED_COLOR = ColorHelper.packARGB(254, 198, 0, 0xFF);
        static final Vector3f COLOR = new Vector3f(0.9960784313725490196078431372549F, 0.77647058823529411764705882352941F, 0F);

        @Override
        public ResourceLocation getStillTexture()
        {
            return STILL_TEXTURE;
        }

        @Override
        public ResourceLocation getFlowingTexture()
        {
            return FLOWING_TEXTURE;
        }

        @Override
        @NotNull
        public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
        {
            return COLOR;
        }

        @Override
        public int getTintColor()
        {
            return PACKED_COLOR;
        }
    };

    public static final IClientFluidTypeExtensions ENDER_SAP = new IClientFluidTypeExtensions()
    {
        static final ResourceLocation STILL_TEXTURE = new ResourceLocation(Reference.MOD_ID, "block/ender_sap_still");
        static final ResourceLocation FLOWING_TEXTURE = new ResourceLocation(Reference.MOD_ID, "block/ender_sap_flowing");
        static final int PACKED_COLOR = ColorHelper.packARGB(10, 93, 80, 0xFF);
        static final Vector3f COLOR = new Vector3f(0.03921568627450980392156862745098F, 0.36470588235294117647058823529412F, 0.31372549019607843137254901960784F);

        @Override
        public ResourceLocation getStillTexture()
        {
            return STILL_TEXTURE;
        }

        @Override
        public ResourceLocation getFlowingTexture()
        {
            return FLOWING_TEXTURE;
        }

        @Override
        @NotNull
        public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
        {
            return COLOR;
        }

        @Override
        public int getTintColor()
        {
            return PACKED_COLOR;
        }
    };

    public static final IClientFluidTypeExtensions FUELIUM = new IClientFluidTypeExtensions()
    {
        static final ResourceLocation STILL_TEXTURE = new ResourceLocation(Reference.MOD_ID, "block/fuelium_still");
        static final ResourceLocation FLOWING_TEXTURE = new ResourceLocation(Reference.MOD_ID, "block/fuelium_flowing");
        static final int PACKED_COLOR = ColorHelper.packARGB(148, 242, 45, 0xFF);
        static final Vector3f COLOR = new Vector3f(0.58039215686274509803921568627451F, 0.94901960784313725490196078431373F, 0.17647058823529411764705882352941F);

        @Override
        public ResourceLocation getStillTexture()
        {
            return STILL_TEXTURE;
        }

        @Override
        public ResourceLocation getFlowingTexture()
        {
            return FLOWING_TEXTURE;
        }

        @Override
        @NotNull
        public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
        {
            return COLOR;
        }

        @Override
        public int getTintColor()
        {
            return PACKED_COLOR;
        }
    };
}
