package com.deathfrog.greenhousegardener.core.datalistener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import com.deathfrog.greenhousegardener.GreenhouseGardenerMod;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

/**
 * Loads item remainders for greenhouse climate materials whose item metadata does not expose one.
 */
public class GreenhouseClimateRemainderListener extends SimpleJsonResourceReloadListener<JsonElement>
{
    // 26.1.2: SimpleJsonResourceReloadListener became Codec<T>-based; see the identical note in
    // GreenhouseClimateItemValueListener for why this stays JsonElement-typed via a passthrough codec.
    private static final Codec<JsonElement> JSON_ELEMENT_CODEC = Codec.PASSTHROUGH.xmap(
        dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue(),
        json -> new Dynamic<>(JsonOps.INSTANCE, json));
    private static final String DIRECTORY = "greenhouse_climate_remainders";
    private static final String TAG_CONSUMED = "consumed";
    private static final String TAG_REMAINDER = "remainder";

    public static final GreenhouseClimateRemainderListener INSTANCE = new GreenhouseClimateRemainderListener();

    private final Map<Item, Item> remainders = new HashMap<>();

    private GreenhouseClimateRemainderListener()
    {
        super(JSON_ELEMENT_CODEC, FileToIdConverter.json(DIRECTORY));
    }

    @Override
    protected void apply(
        @NotNull final @Nonnull Map<Identifier, JsonElement> object,
        @NotNull final @Nonnull ResourceManager resourceManager,
        @NotNull final @Nonnull ProfilerFiller profiler)
    {
        remainders.clear();

        for (final Map.Entry<Identifier, JsonElement> entry : object.entrySet())
        {
            try
            {
                JsonElement elem = entry.getValue();

                if (elem == null) continue;

                final JsonObject json = GsonHelper.convertToJsonObject(elem, DIRECTORY + "/" + entry.getKey());

                if (json == null) continue;

                final Item consumed = parseItem(json, TAG_CONSUMED)
                    .orElseThrow(() -> new JsonParseException("Unknown consumed item: " + GsonHelper.getAsString(json, TAG_CONSUMED)));
                final Item remainder = parseItem(json, TAG_REMAINDER)
                    .orElseThrow(() -> new JsonParseException("Unknown remainder item: " + GsonHelper.getAsString(json, TAG_REMAINDER)));

                remainders.put(consumed, remainder);
            }
            catch (final JsonParseException | IllegalArgumentException e)
            {
                GreenhouseGardenerMod.LOGGER.error("Error parsing greenhouse climate remainder {}", entry.getKey(), e);
            }
        }

        GreenhouseGardenerMod.LOGGER.info("Loaded {} greenhouse climate remainder mappings", remainders.size());
    }

    /**
     * Resolve the empty container produced when a climate material stack is consumed.
     *
     * @param consumed stack consumed by the greenhouse climate ledger
     * @return remainder stack, or empty when no remainder is known
     */
    public ItemStack getRemainder(final ItemStack consumed)
    {
        if (consumed.isEmpty())
        {
            return ItemStack.EMPTY;
        }

        // 26.1.2: ItemStack.hasCraftingRemainingItem()/getCraftingRemainingItem() were removed in favor of
        // Item.getCraftingRemainder() returning a nullable ItemStackTemplate that must be materialized.
        final ItemStackTemplate craftingRemainder = consumed.getCraftingRemainder();
        if (craftingRemainder != null)
        {
            return craftingRemainder.create();
        }

        final Item remainder = remainders.get(consumed.getItem());
        return remainder == null ? ItemStack.EMPTY : new ItemStack(remainder);
    }

    private static Optional<Item> parseItem(final @Nonnull JsonObject json, final @Nonnull String key)
    {
        String gString = GsonHelper.getAsString(json, key);

        if (gString == null) return Optional.of(Items.AIR);

        final Identifier location = Identifier.parse(gString);
        return BuiltInRegistries.ITEM.getOptional(location);
    }
}
