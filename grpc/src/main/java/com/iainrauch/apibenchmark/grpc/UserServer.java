package com.iainrauch.apibenchmark.grpc;

import com.google.protobuf.ByteString;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import static com.iainrauch.apibenchmark.common.Utils.getBlob;

public class UserServer {

    private static final Logger logger = Logger.getLogger(UserServer.class.getName());

    private Server server;

    private void start() throws IOException {
    /* The port on which the server should run */
        int port = 50051;
        server = NettyServerBuilder.forPort(port)
            .addService(new UserImpl())
            .useTransportSecurity(new File("cert.pem"), new File("key.pem"))
            .build()
            .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                UserServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        UserServer server = new UserServer();
        server.start();
        server.blockUntilShutdown();
    }

    static class UserImpl extends UsersGrpc.UsersImplBase {

        private static final AtomicLong counter = new AtomicLong();

        @Override
        public void getUser(GetUserRequest request, StreamObserver<UserReply> responseObserver) {
            UserReply reply = UserReply.newBuilder()
                .setId(request.getId())
                .setData(ByteString.copyFrom(getBlob(1234)))
                .setCounter(counter.incrementAndGet())
                .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

    }
}
