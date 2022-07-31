package com.mrcrayfish.vehicle.fluid;

import com.mojang.math.Vector3f;
import com.mrcrayfish.vehicle.Reference;
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
public abstract class Fuelium extends ForgeFlowingFluid
{
    public Fuelium()
    {
        super(new Properties(ModFluidTypes.FUELIUM, ModFluids.FUELIUM, ModFluids.FLOWING_FUELIUM)
                .block(ModBlocks.FUELIUM));
    }

    @Override
    public Item getBucket()
    {
        return ModItems.FUELIUM_BUCKET.get();
    }

    public static class Source extends Fuelium
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

    public static class Flowing extends Fuelium
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
                    .viscosity(900)
                    .density(900)
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
            protected static final ResourceLocation STILL_TEXTURE = new ResourceLocation(Reference.MOD_ID, "block/fuelium_still");
            protected static final ResourceLocation FLOWING_TEXTURE = new ResourceLocation(Reference.MOD_ID, "block/fuelium_flowing");
            protected static final Vector3f COLOR = new Vector3f(0.58039215686274509803921568627451F, 0.94901960784313725490196078431373F, 0.17647058823529411764705882352941F);

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
        }
    }
}
