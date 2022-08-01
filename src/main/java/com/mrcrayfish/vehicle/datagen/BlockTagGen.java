package com.mrcrayfish.vehicle.datagen;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class BlockTagGen extends BlockTagsProvider
{

    public BlockTagGen(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper)
    {
        super(generator, Reference.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags()
    {}
}
