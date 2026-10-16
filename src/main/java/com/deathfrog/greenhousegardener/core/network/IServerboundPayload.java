package com.deathfrog.greenhousegardener.core.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * Small helper for client-to-server addon payloads.
 */
public interface IServerboundPayload extends CustomPacketPayload
{
    default void sendToServer()
    {
        // 26.1.2: PacketDistributor.sendToServer(...) was removed -- PacketDistributor is now clientbound-only
        // (sendToPlayer/sendToAllPlayers/etc). The client-to-server direction moved to its own class,
        // net.neoforged.neoforge.client.network.ClientPacketDistributor.
        ClientPacketDistributor.sendToServer(this);
    }
}
