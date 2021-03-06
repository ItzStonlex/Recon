package org.itzstonlex.recon.option;

import org.itzstonlex.recon.exception.SocketSetOptionException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ChannelOption {

    public static ChannelOption of(Type type, Object value) {
        return new ChannelOption(type, value);
    }

    public static ChannelOption nullable(Type type) {
        return new ChannelOption(type, null);
    }


    private final Type type;
    private Object value;

    private ChannelOption(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void apply(ServerSocket serverSocket) {
        type.apply(serverSocket, value);
    }

    public void apply(Socket socket) {
        type.apply(socket, value);
    }

    public void apply(ServerSocketChannel channel) {
        type.apply(channel, value);
    }

    public void apply(SocketChannel channel) {
        type.apply(channel, value);
    }

    @SuppressWarnings("all")
    public enum Type {

        SO_BROADCAST(StandardSocketOptions.SO_BROADCAST),
        SO_KEEPALIVE(StandardSocketOptions.SO_KEEPALIVE),
        SO_SNDBUF(StandardSocketOptions.SO_SNDBUF),
        SO_RCVBUF(StandardSocketOptions.SO_RCVBUF),
        SO_REUSEADDR(StandardSocketOptions.SO_REUSEADDR),
        SO_LINGER(StandardSocketOptions.SO_LINGER),
        IP_TOS(StandardSocketOptions.IP_TOS),
        IP_MULTICAST_IF(StandardSocketOptions.IP_MULTICAST_IF),
        IP_MULTICAST_TTL(StandardSocketOptions.IP_MULTICAST_TTL),
        IP_MULTICAST_LOOP(StandardSocketOptions.IP_MULTICAST_LOOP),
        TCP_NODELAY(StandardSocketOptions.TCP_NODELAY),
        ;

        private final SocketOption impl;

        Type(SocketOption<?> impl) {
            this.impl = impl;
        }

        public void apply(ServerSocketChannel channel, Object value) {
            try {
                channel.setOption(impl, value);
            }
            catch (IOException exception) {
                throw new SocketSetOptionException(exception);
            }
        }

        public void apply(SocketChannel channel, Object value) {
            try {
                channel.setOption(impl, value);
            }
            catch (IOException exception) {
                throw new SocketSetOptionException(exception);
            }
        }

        public void apply(ServerSocket serverSocket, Object value) {
            try {
                serverSocket.getChannel().setOption(impl, value);
            }
            catch (IOException exception) {
                throw new SocketSetOptionException(exception);
            }
        }

        public void apply(Socket socket, Object value) {
            try {
                socket.getChannel().setOption(impl, value);
            }
            catch (IOException exception) {
                throw new SocketSetOptionException(exception);
            }
        }
    }

}
