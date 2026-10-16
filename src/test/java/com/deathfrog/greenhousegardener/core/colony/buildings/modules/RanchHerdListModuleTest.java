package com.deathfrog.greenhousegardener.core.colony.buildings.modules;

import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

import java.util.List;

import javax.annotation.Nonnull;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RanchHerdListModuleTest
{
    private static final Identifier DEER = id("deer");
    private static final Identifier BOAR = id("boar");
    private static final Identifier BEAR = id("bear");

    @Test
    void retainsFirstSeenOrderAcrossRescans()
    {
        assertEquals(
            List.of(DEER, BOAR, BEAR),
            RanchHerdOrder.reconcile(
                List.of(DEER, BOAR, BEAR),
                List.of(BEAR, DEER, BOAR)));
    }

    @Test
    void absentTypeRelinquishesItsPositionAndWaitingTypePromotes()
    {
        assertEquals(
            List.of(DEER, BEAR),
            RanchHerdOrder.reconcile(
                List.of(DEER, BOAR, BEAR),
                List.of(DEER, BEAR)));
    }

    @Test
    void returningTypeJoinsTheEndOfTheOrder()
    {
        assertEquals(
            List.of(DEER, BEAR, BOAR),
            RanchHerdOrder.reconcile(
                List.of(DEER, BEAR),
                List.of(BOAR, BEAR, DEER)));
    }

    @Test
    void initialDiscoveryUsesStableRegistryOrder()
    {
        assertEquals(
            List.of(BEAR, BOAR, DEER),
            RanchHerdOrder.reconcile(List.of(), List.of(DEER, BOAR, BEAR)));
    }

    private static Identifier id(final @Nonnull String path)
    {
        return Identifier.fromNamespaceAndPath("test", path);
    }
}
