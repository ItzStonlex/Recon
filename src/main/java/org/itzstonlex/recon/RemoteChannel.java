package org.itzstonlex.recon;

import org.itzstonlex.recon.log.ReconLog;

import java.io.Closeable;
import java.net.InetSocketAddress;

public interface RemoteChannel extends Closeable {

    RemoteConnection connection();

    ReconLog logger();

    ChannelPipeline pipeline();

    InetSocketAddress address();

    ByteStream.Output buffer();

    void write(ByteStream.Output buffer);

    void resetBuf();

    void forceOpen();

    boolean isClosed();

}
