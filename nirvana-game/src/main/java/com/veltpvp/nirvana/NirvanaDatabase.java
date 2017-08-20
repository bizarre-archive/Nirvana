package com.veltpvp.nirvana;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;

import java.util.Arrays;

public class NirvanaDatabase {

    @Getter private MongoClient client;
    @Getter private MongoDatabase database;
    @Getter private MongoCollection players;

    public NirvanaDatabase(Nirvana main) {

        if (main.getConfigFile().getBoolean("MONGO.AUTHENTICATION.ENABLED")) {
            client = new MongoClient(new ServerAddress(main.getConfigFile().getString("MONGO.HOST"), main.getConfigFile().getInteger("MONGO.PORT")), Arrays.asList(MongoCredential.createCredential(main.getConfigFile().getString("MONGO.AUTHENTICATION.USER"), main.getConfigFile().getString("MONGO.DATABASE"), main.getConfigFile().getString("MONGO.AUTHENTICATION.PASSWORD").toCharArray())));
        } else {
            client = new MongoClient(new ServerAddress(main.getConfigFile().getString("MONGO.HOST"), main.getConfigFile().getInteger("MONGO.PORT")));
        }

        database = client.getDatabase(main.getConfigFile().getString("MONGO.DATABASE"));
        players = database.getCollection("players");
    }

}
