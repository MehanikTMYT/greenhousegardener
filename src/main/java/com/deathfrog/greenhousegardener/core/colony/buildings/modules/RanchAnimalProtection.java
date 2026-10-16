package com.deathfrog.greenhousegardener.core.colony.buildings.modules;

import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;

/**
 * Central safety policy for Ranch butchering.
 */
public final class RanchAnimalProtection
{
    private RanchAnimalProtection()
    {
    }

    public static boolean mayButcher(final Animal animal)
    {
        if (animal.hasCustomName() || animal.isLeashed())
        {
            return false;
        }
        // 26.1.2: the Saddleable marker interface was removed; isSaddled() now lives directly on Mob
        // (Animal's superclass), backed by the SADDLE equipment slot.
        if (animal.isSaddled())
        {
            return false;
        }
        if (animal instanceof TamableAnimal tamable && tamable.isTame())
        {
            return false;
        }
        // 26.1.2: OwnableEntity.getOwnerUUID() was replaced by getOwnerReference() (nullable EntityReference).
        return !(animal instanceof OwnableEntity ownable) || ownable.getOwnerReference() == null;
    }
}
