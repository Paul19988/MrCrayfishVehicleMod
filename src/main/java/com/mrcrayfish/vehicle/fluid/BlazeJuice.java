package com.mrcrayfish.vehicle.fluid;

import com.mojang.math.Vector3f;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.client.render.util.ColorHelper;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModFluidTypes;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public abstract class BlazeJuice extends ForgeFlowingFluid
{
    public BlazeJuice()
    {
        super(new Properties(ModFluidTypes.BLAZE_JUICE, ModFluids.BLAZE_JUICE, ModFluids.FLOWING_BLAZE_JUICE)
                .block(ModBlocks.BLAZE_JUICE));
    }

    @Override
    public Item getBucket()
    {
        return ModItems.BLAZE_JUICE_BUCKET.get();
    }

    public static class Source extends BlazeJuice
    {
        @Override
        public boolean isSource(@NotNull FluidState state)
        {
            return true;
        }

        @Override
        public int getAmount(@NotNull FluidState state)
        {
            return 8;
        }
    }

    public static class Flowing extends BlazeJuice
    {
        @Override
        protected void createFluidStateDefinition(StateDefinition.@NotNull Builder<Fluid, FluidState> builder)
        {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state)
        {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(@NotNull FluidState state)
        {
            return false;
        }
    }

    public static class FluidType extends net.minecraftforge.fluids.FluidType
    {
        public FluidType()
        {
            super(EnderSap.FluidType.Properties.create()
                    .viscosity(800)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY));
        }

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
        {
            consumer.accept(new ClientFluidType());
        }

        @OnlyIn(Dist.CLIENT)
        public static class ClientFluidType implements IClientFluidTypeExtensions
        {
            protected static final ResourceLocation STILL_TEXTURE = new ResourceLocation(Reference.MOD_ID, "block/blaze_juice_still");
            protected static final ResourceLocation FLOWING_TEXTURE = new ResourceLocation(Reference.MOD_ID, "block/blaze_juice_flowing");
            protected static final int PACKED_COLOR = ColorHelper.packARGBRed(254, 198, 0, 0xFF);
            protected static final Vector3f COLOR = new Vector3f(0.9960784313725490196078431372549F, 0.77647058823529411764705882352941F, 0F);

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
        }
    }
}
