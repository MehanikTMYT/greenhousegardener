package com.deathfrog.greenhousegardener.core.datalistener;
import net.minecraft.resources.FileToIdConverter;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Codec;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import com.deathfrog.greenhousegardener.GreenhouseGardenerMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Loads datapack mappings from field planting items to their normal harvested products. */
public class GreenhouseCropProductListener extends SimpleJsonResourceReloadListener<JsonElement>
{
    // 26.1.2: SimpleJsonResourceReloadListener became Codec<T>-based, replacing the old (Gson, String
    // directory) constructor. This listener still hand-parses raw JsonObject content via GsonHelper (same
    // as before the port), so it is parameterized on JsonElement using a passthrough codec rather than
    // migrating to a typed record codec -- preserves the exact existing parsing/validation behavior.
    private static final Codec<JsonElement> JSON_ELEMENT_CODEC = Codec.PASSTHROUGH.xmap(
        dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue(),
        json -> new Dynamic<>(JsonOps.INSTANCE, json));
    private static final String DIRECTORY = "greenhouse_crop_products";
    private static final String TAG_SEED = "seed";
    private static final String TAG_PRODUCT = "product";

    public static final GreenhouseCropProductListener INSTANCE = new GreenhouseCropProductListener();

    private Map<Item, Item> products = Map.of();

    private GreenhouseCropProductListener()
    {
        super(JSON_ELEMENT_CODEC, FileToIdConverter.json(DIRECTORY));
    }

    @Override
    protected void apply(
        @NotNull final @Nonnull Map<Identifier, JsonElement> objects,
        @NotNull final @Nonnull ResourceManager resourceManager,
        @NotNull final @Nonnull ProfilerFiller profiler)
    {
        final Map<Item, Item> resolvedProducts = new HashMap<>();
        parseDefinitions(objects).forEach((seedId, productId) ->
        {
            final Item seed = BuiltInRegistries.ITEM.getOptional(seedId).orElse(null);
            final Item product = BuiltInRegistries.ITEM.getOptional(productId).orElse(null);
            if (seed == null || product == null)
            {
                GreenhouseGardenerMod.LOGGER.error(
                    "Unknown item in greenhouse crop product mapping: seed {}, product {}",
                    seedId,
                    productId);
            }
            else
            {
                resolvedProducts.put(seed, product);
            }
        });
        products = Map.copyOf(resolvedProducts);
        GreenhouseGardenerMod.LOGGER.info("Loaded {} greenhouse crop product mappings", products.size());
    }

    /**
     * Resolve the warehouse item representing a field's selected planting item.
     *
     * @param seed selected field planting item
     * @return mapped harvested product, or the planting item itself when no mapping exists
     */
    public Item productFor(final ItemStack seed)
    {
        return products.getOrDefault(seed.getItem(), seed.getItem());
    }

    /**
     * Parse a complete reload snapshot in deterministic resource order.
     *
     * @param objects crop-product JSON resources
     * @return immutable planting-item to product mappings
     */
    @SuppressWarnings("null")
    static Map<Identifier, Identifier> parseDefinitions(final Map<Identifier, JsonElement> objects)
    {
        final Map<Identifier, Identifier> loadedProducts = new HashMap<>();
        objects.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(Comparator.comparing(Identifier::toString)))
            .forEach(entry -> loadMapping(entry.getKey(), entry.getValue(), loadedProducts));
        return Map.copyOf(loadedProducts);
    }

    /**
     * Resolve a product against an explicit mapping snapshot.
     *
     * @param seed selected planting item
     * @param mappings planting-item to product mappings
     * @return mapped product or the planting item itself
     */
    static Identifier productIdFor(
        final Identifier seed,
        final Map<Identifier, Identifier> mappings)
    {
        return mappings.getOrDefault(seed, seed);
    }

    /**
     * Parse and add one crop-product resource, logging invalid or conflicting definitions.
     *
     * @param resource resource identifier used for diagnostics
     * @param element JSON mapping definition
     * @param target mappings accumulated during this reload
     */
    private static void loadMapping(
        final Identifier resource,
        final @Nonnull JsonElement element,
        final Map<Identifier, Identifier> target)
    {
        try
        {
            final JsonObject json = GsonHelper.convertToJsonObject(element, DIRECTORY + "/" + resource);

            if (json == null) 
            {
                GreenhouseGardenerMod.LOGGER.error("No JSON element found while parsing greenhouse crop product mapping {}", resource);
                return;
            }

            final Identifier seed = parseItemId(json, TAG_SEED);
            final Identifier product = parseItemId(json, TAG_PRODUCT);
            final Identifier previous = target.put(seed, product);
            if (previous != null && !previous.equals(product))
            {
                GreenhouseGardenerMod.LOGGER.warn(
                    "Crop product mapping {} replaces {} with {} for seed {}",
                    resource,
                    previous,
                    product,
                    seed);
            }
        }
        catch (final JsonParseException | IllegalArgumentException e)
        {
            GreenhouseGardenerMod.LOGGER.error("Error parsing greenhouse crop product mapping {}", resource, e);
        }
    }

    /**
     * Resolve a required item identifier from a mapping property.
     *
     * @param json mapping object
     * @param key property containing the item identifier
     * @return parsed item identifier
     * @throws JsonParseException when the identifier is malformed
     */
    private static Identifier parseItemId(final @Nonnull JsonObject json, final @Nonnull String key)
    {
        final String value = GsonHelper.getAsString(json, key);

        if (value == null)
        {
            throw new JsonParseException("Null value for '" + key + "'");  
        }

        final Identifier id = Identifier.tryParse(value);
        if (id == null)
        {
            throw new JsonParseException("Invalid item identifier for '" + key + "': " + value);
        }
        return id;
    }
}
