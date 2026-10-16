package com.deathfrog.greenhousegardener.apiimp.initializer;

import com.deathfrog.greenhousegardener.api.tileentities.GreenhouseTileEntities;
import com.minecolonies.api.util.IItemHandlerCapProvider;

import net.neoforged.neoforge.capabilities.Capabilities.Item;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class CapabilityInitializer
{
    @SuppressWarnings("null")
    public static void registerCapabilities(final RegisterCapabilitiesEvent event)
    {
        // 26.1.2: NeoForge's IItemHandler-based Capabilities.ItemHandler was replaced by a transactional
        // transfer API (Capabilities.Item, ResourceHandler<ItemResource>). MineColonies' own
        // IItemHandlerCapProvider already bridges this via getItemResourceHandlerCap(Direction), which wraps
        // the existing IItemHandler-based inventory logic through LegacyItemHandlerResourceAdapter -- no
        // change to this mod's own inventory logic is required, only to which capability/accessor is wired up.
        event.registerBlockEntity(Item.BLOCK, GreenhouseTileEntities.BUILDING.get(), IItemHandlerCapProvider::getItemResourceHandlerCap);
    }

    private CapabilityInitializer()
    {
        throw new IllegalStateException("Tried to initialize CapabilityInitializer but this is a utility class.");
    }
}
