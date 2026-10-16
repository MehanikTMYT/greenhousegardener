package com.deathfrog.greenhousegardener.core;

import javax.annotation.Nonnull;

import com.deathfrog.greenhousegardener.GreenhouseGardenerMod;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

/**
 * Tag keys used by Greenhouse Gardener data-pack driven behavior.
 */
public final class ModTags
{
    /**
     * Utility class.
     */
    private ModTags()
    {
    }

    /**
     * Block tag keys used by greenhouse structure validation.
     */
    public static final class BLOCKS
    {
        public static final Identifier GREENHOUSE_ROOF_KEY = tag("greenhouse_roof");
        @SuppressWarnings("null")
        public @Nonnull static final TagKey<Block> GREENHOUSE_ROOF = BlockTags.create(GREENHOUSE_ROOF_KEY);

        /**
         * Build a Greenhouse Gardener block tag resource location.
         *
         * @param path tag path
         * @return namespaced resource location
         */
        private static Identifier tag(final @Nonnull String path)
        {
            return Identifier.fromNamespaceAndPath(GreenhouseGardenerMod.MODID, path);
        }
    }

    /**
     * Entity type tags controlling which animals the Ranch can manage and which
     * interactions the Rancher may perform.
     */
    public static final class ENTITY_TYPES
    {
        public static final TagKey<EntityType<?>> RANCH_ANIMALS = tag("ranch/animals");
        public static final TagKey<EntityType<?>> RANCH_EXCLUDED = tag("ranch/excluded");
        public static final TagKey<EntityType<?>> RANCH_BREEDABLE = tag("ranch/breedable");
        public static final TagKey<EntityType<?>> RANCH_FEEDABLE = tag("ranch/feedable");
        public static final TagKey<EntityType<?>> RANCH_BUTCHERABLE = tag("ranch/butcherable");
        public static final TagKey<EntityType<?>> RANCH_SHEARABLE = tag("ranch/shearable");
        public static final TagKey<EntityType<?>> RANCH_BUCKET_MILKABLE = tag("ranch/bucket_milkable");
        public static final TagKey<EntityType<?>> RANCH_BOWL_MILKABLE = tag("ranch/bowl_milkable");

        private ENTITY_TYPES()
        {
        }

        @SuppressWarnings("null")
        private static TagKey<EntityType<?>> tag(final String path)
        {
            return TagKey.create(Registries.ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(GreenhouseGardenerMod.MODID, path));
        }
    }
}
