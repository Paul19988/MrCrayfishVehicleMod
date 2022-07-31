package com.mrcrayfish.vehicle.client.handler;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.math.Vector3f;
import com.mrcrayfish.vehicle.init.ModFluidTypes;
import com.mrcrayfish.vehicle.init.ModFluids;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FogHandler
{
    @SubscribeEvent
    public void onComputeFogColor(ViewportEvent.ComputeFogColor event)
    {
        Camera camera = event.getCamera();
        FluidState state = camera.getEntity().getLevel().getFluidState(camera.getBlockPosition());

        if(state.getFluidType() == ModFluidTypes.FUELIUM.get())
        {
            Vector3f color = IClientFluidTypeExtensions.of(state.getFluidType()).modifyFogColor(null, 0, null, 0, 0, null);
            event.setRed(color.x());
            event.setGreen(color.y());
            event.setBlue(color.z());
        }
    }

    @SubscribeEvent
    public void onRenderFog(ViewportEvent.RenderFog event)
    {
        Camera camera = event.getCamera();
        FluidState state = camera.getEntity().getLevel().getFluidState(camera.getBlockPosition());

        if(state.getFluidType() == ModFluidTypes.FUELIUM.get())
        {
            event.setFogShape(FogShape.CYLINDER);
        }
    }
}
