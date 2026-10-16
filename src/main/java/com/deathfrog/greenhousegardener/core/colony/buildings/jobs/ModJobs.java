package com.deathfrog.greenhousegardener.core.colony.buildings.jobs;

import java.util.ArrayList;
import java.util.List;

import com.deathfrog.greenhousegardener.GreenhouseGardenerMod;
import com.minecolonies.api.colony.jobs.registry.JobEntry;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModJobs
{
    public static final String HORTICULTURIST_TAG = "horticulturist";
    public static final String RANCHER_TAG = "rancher";

    public static final Identifier HORTICULTURIST_ID = Identifier.fromNamespaceAndPath(GreenhouseGardenerMod.MODID, HORTICULTURIST_TAG);
    public static final Identifier RANCHER_ID = Identifier.fromNamespaceAndPath(GreenhouseGardenerMod.MODID, RANCHER_TAG);

    public static DeferredHolder<JobEntry, JobEntry> horticulturist;
    public static DeferredHolder<JobEntry, JobEntry> rancher;

    private ModJobs()
    {
        throw new IllegalStateException("Tried to initialize: ModJobs but this is a Utility class.");
    }

    public static List<Identifier> getJobs()
    {
        List<Identifier> jobs = new ArrayList<>() { };
        jobs.add(HORTICULTURIST_ID);
        jobs.add(RANCHER_ID);

        return jobs;
    }
}
