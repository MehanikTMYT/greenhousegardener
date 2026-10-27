package com.deathfrog.greenhousegardener.core.datalistener;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonParser;

import net.minecraft.resources.Identifier;

class GreenhouseCropProductListenerTest
{
    @Test
    void parsesExplicitSeedProductMapping()
    {
        final Map<Identifier, Identifier> mappings = GreenhouseCropProductListener.parseDefinitions(Map.of(
            Identifier.fromNamespaceAndPath("test", "wheat"),
            JsonParser.parseString("{\"seed\":\"minecraft:wheat_seeds\",\"product\":\"minecraft:wheat\"}")));

        assertEquals(Identifier.parse("minecraft:wheat"), GreenhouseCropProductListener.productIdFor(
            Identifier.parse("minecraft:wheat_seeds"), mappings));
    }

    @Test
    void mapsMustardSeedToMustardGreens()
    {
        final Map<Identifier, Identifier> mappings = GreenhouseCropProductListener.parseDefinitions(Map.of(
            Identifier.fromNamespaceAndPath("greenhousegardener", "mustard"),
            JsonParser.parseString(
                "{\"seed\":\"greenhousegardener:mustard_seed\",\"product\":\"greenhousegardener:mustard_greens\"}")));

        assertEquals(Identifier.parse("greenhousegardener:mustard_greens"),
            GreenhouseCropProductListener.productIdFor(
                Identifier.parse("greenhousegardener:mustard_seed"), mappings));
    }

    @Test
    void fallsBackToPlantingItemWithoutMapping()
    {
        final Identifier carrot = Identifier.parse("minecraft:carrot");
        assertEquals(carrot, GreenhouseCropProductListener.productIdFor(carrot, Map.of()));
    }

    @Test
    void laterResourceIdDeterministicallyReplacesDuplicateSeed()
    {
        final Map<Identifier, Identifier> mappings = GreenhouseCropProductListener.parseDefinitions(Map.of(
            Identifier.fromNamespaceAndPath("test", "a_first"),
            JsonParser.parseString("{\"seed\":\"minecraft:wheat_seeds\",\"product\":\"minecraft:wheat\"}"),
            Identifier.fromNamespaceAndPath("test", "z_last"),
            JsonParser.parseString("{\"seed\":\"minecraft:wheat_seeds\",\"product\":\"minecraft:bread\"}")));

        assertEquals(Identifier.parse("minecraft:bread"), GreenhouseCropProductListener.productIdFor(
            Identifier.parse("minecraft:wheat_seeds"), mappings));
    }
}
