package com.deathfrog.greenhousegardener.core.world.biomeservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.minecraft.resources.Identifier;

import com.deathfrog.greenhousegardener.core.colony.buildings.modules.GreenhouseBiomeModule.HumiditySetting;
import com.deathfrog.greenhousegardener.core.colony.buildings.modules.GreenhouseBiomeModule.TemperatureSetting;

class GreenhouseBiomeOverlayServiceTest
{
    @Test
    void overlayWriterSkipsCellsAlreadyMatchingTargetBiome()
    {
        final Identifier targetBiome = Identifier.fromNamespaceAndPath("minecraft", "plains");

        assertFalse(GreenhouseBiomeOverlayService.shouldWriteOverlayCell(targetBiome, targetBiome));
    }

    @Test
    void overlayWriterWritesCellsWithDifferentBiome()
    {
        final Identifier targetBiome = Identifier.fromNamespaceAndPath("minecraft", "plains");
        final Identifier currentBiome = Identifier.fromNamespaceAndPath("minecraft", "desert");

        assertTrue(GreenhouseBiomeOverlayService.shouldWriteOverlayCell(currentBiome, targetBiome));
    }

    @Test
    void overlayWriterWritesCellsWhenCurrentBiomeCannotBeResolved()
    {
        final Identifier targetBiome = Identifier.fromNamespaceAndPath("minecraft", "plains");

        assertTrue(GreenhouseBiomeOverlayService.shouldWriteOverlayCell(null, targetBiome));
    }

    @Test
    void overlayWriterSkipsCellsWhenTargetBiomeCannotBeResolved()
    {
        final Identifier currentBiome = Identifier.fromNamespaceAndPath("minecraft", "plains");

        assertFalse(GreenhouseBiomeOverlayService.shouldWriteOverlayCell(currentBiome, null));
    }

    @Test
    void everyClimateUsesAnOwnedReferenceBiomeAndRoundTrips()
    {
        for (final TemperatureSetting temperature : TemperatureSetting.values())
        {
            for (final HumiditySetting humidity : HumiditySetting.values())
            {
                final GreenhouseClimate climate = new GreenhouseClimate(temperature, humidity);
                final Identifier biome = GreenhouseBiomeOverlayService.biomeFor(climate);
                assertEquals("greenhousegardener", biome.getNamespace());
                assertEquals(climate, GreenhouseBiomeOverlayService.climateFor(biome).orElseThrow());
            }
        }
    }

    @Test
    void legacyReferencesRemainMigrationAliasesButNotCurrentReferences()
    {
        final Identifier sparseJungle = Identifier.withDefaultNamespace("sparse_jungle");
        final GreenhouseClimate historical = new GreenhouseClimate(TemperatureSetting.HOT, HumiditySetting.NORMAL);

        assertTrue(GreenhouseBiomeOverlayService.climateFor(sparseJungle).isEmpty());
        assertEquals(historical, GreenhouseBiomeOverlayService.legacyClimateFor(sparseJungle).orElseThrow());
        assertTrue(GreenhouseBiomeOverlayService.representsClimate(sparseJungle, historical));
    }

    @Test
    void untrackedLegacyBiomeIsNotTreatedAsAnAppliedOverlay()
    {
        final Identifier sparseJungle = Identifier.withDefaultNamespace("sparse_jungle");
        final GreenhouseClimate historical = new GreenhouseClimate(TemperatureSetting.HOT, HumiditySetting.NORMAL);

        assertFalse(GreenhouseBiomeOverlayService.isTrackedLegacy(sparseJungle, null, historical));
    }

    @Test
    void matchingPersistedLegacyBiomeIsTreatedAsAnAppliedOverlay()
    {
        final Identifier sparseJungle = Identifier.withDefaultNamespace("sparse_jungle");
        final GreenhouseClimate historical = new GreenhouseClimate(TemperatureSetting.HOT, HumiditySetting.NORMAL);

        assertTrue(GreenhouseBiomeOverlayService.isTrackedLegacy(sparseJungle, sparseJungle, historical));
    }
}
