package com.iainrauch.apibenchmark.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.internal.DnsNameResolverProvider;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UserClient {

    private static final Logger logger = Logger.getLogger(UserClient.class.getName());

    private final ManagedChannel channel;
    private final UsersGrpc.UsersBlockingStub blockingStub;

    public UserClient(String host, int port) throws SSLException {
        this(NettyChannelBuilder.forAddress(host, port)
            .nameResolverFactory(new DnsNameResolverProvider())
            .sslContext(GrpcSslContexts.forClient().trustManager(new File("cert.pem")).build())
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid needing certificates.
//            .usePlaintext(true)
            .build());
    }

    /**
     * Construct client for accessing RouteGuide server using the existing channel.
     */
    UserClient(ManagedChannel channel) {
        this.channel = channel;
        this.blockingStub = UsersGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * Say hello to server.
     */
    public UserReply get(String id) {
        GetUserRequest request = GetUserRequest.newBuilder().setId(id).build();
        try {
            return blockingStub.getUser(request);
        } catch (StatusRuntimeException e) {
            logger.warning("Could not get user: " + e);
            return null;
        }
    }

    public static void main(String[] args) throws SSLException {
        UserClient client = new UserClient("localhost", 50051);
        UserReply reply = client.get("abc");
        System.out.println(reply.toString());
    }
}
