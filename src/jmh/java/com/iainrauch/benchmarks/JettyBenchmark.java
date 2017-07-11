package com.iainrauch.benchmarks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.iainrauch.apibenchmark.common.UserDto;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.UUID;

@State(Scope.Thread)
public class JettyBenchmark {

    private OkHttpClient client;
    private Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class,
        new ByteArrayToBase64TypeAdapter()).create();

    @Setup
    public void init() {
        client = new OkHttpClient();
    }

    @Benchmark
    public long getUser() {
        Request request = new Request.Builder()
            .url("http://localhost:50061/users/" + UUID.randomUUID().toString())
            .addHeader("Accept", "application/json; q=0.5")
            .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                UserDto user = gson.fromJson(response.body().charStream(), UserDto.class);
                return user.getCounter();
            }
        } catch (IOException e) {
            return -1;
        }
        return -1;
    }

    private static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Base64.getDecoder().decode(json.getAsString());
        }

        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.getEncoder().encodeToString(src));
        }
    }
}
