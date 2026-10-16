package com.deathfrog.greenhousegardener.core.blocks;

import com.deathfrog.greenhousegardener.GreenhouseGardenerMod;
import com.minecolonies.api.items.ModTags;
import com.minecolonies.core.blocks.MinecoloniesCropBlock;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class BlockSpinach extends MinecoloniesCropBlock
{
    public static final String BLOCK_NAME = "spinach";

    public BlockSpinach()
    {
        super(BLOCK_NAME, com.minecolonies.api.blocks.ModBlocks.farmland, List.of(Blocks.SHORT_GRASS, Blocks.TALL_GRASS, Blocks.FERN), ModTags.temperateBiomes);
    }

    @Override
    public Identifier getRegistryName()
    {
        return Identifier.fromNamespaceAndPath(GreenhouseGardenerMod.MODID, BLOCK_NAME);
    }
}
