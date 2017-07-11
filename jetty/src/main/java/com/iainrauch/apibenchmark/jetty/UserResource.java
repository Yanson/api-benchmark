package com.iainrauch.apibenchmark.jetty;

import com.iainrauch.apibenchmark.common.UserDto;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

import static com.iainrauch.apibenchmark.common.Utils.getBlob;

@Path("/users")
public class UserResource {

    private static final AtomicLong counter = new AtomicLong();

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDto getUser(@PathParam("id") String id) {
        return new UserDto(id, getBlob(1234), counter.incrementAndGet());
    }


}
