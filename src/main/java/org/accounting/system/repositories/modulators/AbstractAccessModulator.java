package org.accounting.system.repositories.modulators;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import net.jodah.typetools.TypeResolver;
import org.accounting.system.beans.RequestInformation;
import org.accounting.system.entities.Entity;
import org.accounting.system.enums.Collection;
import org.bson.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;

public abstract class AbstractAccessModulator<E extends Entity, I> implements PanacheMongoRepositoryBase<E, I> {

    @Inject
    RequestInformation requestInformation;

    @Inject
    MongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String database;

    protected Class<E> clazz;

    private Class<I> identity;

    public AbstractAccessModulator() {
        Class<?>[] typeArguments = TypeResolver.resolveRawArguments(AbstractAccessModulator.class, getClass());
        this.clazz = (Class<E>) typeArguments[0];
        this.identity = (Class<I>) typeArguments[1];
    }

    public MongoCollection<Document> getMongoCollection() {
        return mongoClient.getDatabase(database).getCollection(clazz.getSimpleName());
    }

    public MongoCollection<Document> getMongoCollection(String collection) {
        return mongoClient.getDatabase(database).getCollection(collection);
    }

    public RequestInformation getRequestInformation() {
        return requestInformation;
    }

    public Collection collection() {
        return Collection.valueOf(clazz.getSimpleName());
    }

    public Class<I> getIdentity() {
        return identity;
    }
}