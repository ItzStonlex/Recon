package org.itzstonlex.recon.wrapped;

import org.itzstonlex.recon.ChannelConfig;
import org.itzstonlex.recon.RemoteChannel;
import org.itzstonlex.recon.handler.PacketHandler;
import org.itzstonlex.recon.log.ReconLog;
import org.itzstonlex.recon.side.wrapped.AbstractClient;

public class WrappedClient extends AbstractClient {

    public WrappedClient() {
        super(5_000);
    }

    @Override
    public void initChannel(ReconLog logger, ChannelConfig channelConfig) {
        channelConfig.pipeline().addLast("packet-handler", new PacketHandler());
    }

    public static void main(String[] args) {
        RemoteChannel channel = new WrappedClient().connect();

        System.out.println("Client connect: " + channel.address());
    }

}
