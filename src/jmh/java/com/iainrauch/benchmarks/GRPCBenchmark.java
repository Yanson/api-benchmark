package com.iainrauch.benchmarks;

import com.iainrauch.apibenchmark.grpc.UserClient;
import com.iainrauch.apibenchmark.grpc.UserReply;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import javax.net.ssl.SSLException;
import java.util.UUID;

@State(Scope.Thread)
public class GRPCBenchmark {

    private UserClient userClient;

    @Setup
    public void init() throws SSLException {
        userClient = new UserClient("localhost", 50051);
    }

    @Benchmark
    public long getUser() {
        UserReply user = userClient.get(UUID.randomUUID().toString());
        if (user != null) {
            return user.getCounter();
        }
        return -1;
    }

}
