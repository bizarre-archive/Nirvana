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

        if (main.getMainConfig().getBoolean("MONGO.AUTHENTICATION.ENABLED")) {
            client = new MongoClient(new ServerAddress(main.getMainConfig().getString("MONGO.HOST"), main.getMainConfig().getInteger("MONGO.PORT")), Arrays.asList(MongoCredential.createCredential(main.getMainConfig().getString("MONGO.AUTHENTICATION.USER"), main.getMainConfig().getString("MONGO.DATABASE"), main.getMainConfig().getString("MONGO.AUTHENTICATION.PASSWORD").toCharArray())));
        } else {
            client = new MongoClient(new ServerAddress(main.getMainConfig().getString("MONGO.HOST"), main.getMainConfig().getInteger("MONGO.PORT")));
        }

        database = client.getDatabase(main.getMainConfig().getString("MONGO.DATABASE"));
        players = database.getCollection("players");
    }

}
