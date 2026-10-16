package com.deathfrog.greenhousegardener.core.blocks;

import com.deathfrog.greenhousegardener.GreenhouseGardenerMod;
import com.minecolonies.api.items.ModTags;
import com.minecolonies.core.blocks.MinecoloniesCropBlock;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class BlockCucumber extends MinecoloniesCropBlock
{
    public static final String BLOCK_NAME = "cucumber";

    public BlockCucumber()
    {
        super(BLOCK_NAME, com.minecolonies.api.blocks.ModBlocks.farmland, List.of(Blocks.SHORT_GRASS, Blocks.TALL_GRASS, Blocks.FERN), ModTags.temperateBiomes);
    }

    @Override
    public Identifier getRegistryName()
    {
        return Identifier.fromNamespaceAndPath(GreenhouseGardenerMod.MODID, BLOCK_NAME);
    }
}
