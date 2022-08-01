package com.mrcrayfish.vehicle.datagen;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.init.ModFluids;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class FluidTagGen extends FluidTagsProvider
{

    public FluidTagGen(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper)
    {
        super(generator, Reference.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags()
    {
        this.tag(FluidTags.WATER).add(ModFluids.FUELIUM.get(), ModFluids.FLOWING_FUELIUM.get());
        this.tag(FluidTags.WATER).add(ModFluids.BLAZE_JUICE.get(), ModFluids.FLOWING_BLAZE_JUICE.get());
        this.tag(FluidTags.WATER).add(ModFluids.ENDER_SAP.get(), ModFluids.FLOWING_ENDER_SAP.get());
    }
}
