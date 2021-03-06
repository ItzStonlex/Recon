package org.itzstonlex.recon.minecraft.core.server.impl;

import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.minecraft.core.CoreService;
import org.itzstonlex.recon.minecraft.core.MinecraftCore;
import org.itzstonlex.recon.minecraft.core.player.CorePlayer;
import org.itzstonlex.recon.minecraft.core.server.CoreServer;
import org.itzstonlex.recon.minecraft.packet.PacketHandler;
import org.itzstonlex.recon.minecraft.packet.PlayerRedirect;
import org.itzstonlex.recon.minecraft.server.MinecraftServer;

import java.net.InetSocketAddress;

public class Proxy extends CoreServer {

    public Proxy(MinecraftCore minecraftCore, RemoteChannel channel,
                 String name, InetSocketAddress address) {

        super(minecraftCore, channel, true, name, address);
    }

    @PacketHandler
    public void handle(PlayerRedirect packet) {
        CoreService coreService = super.getService();

        CorePlayer player = coreService.getPlayerManager().getPlayer(packet.getPlayerName());
        MinecraftServer minecraftServer = coreService.getServerManager().getServer(packet.getServer());

        if (player != null) {
            player.redirect(minecraftServer);
        }
    }

}
