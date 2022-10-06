package org.accounting.system.entities.projections;

import com.mongodb.ReadPreference;
import com.mongodb.client.model.Collation;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MongoQuery<E> implements PanacheQuery<E> {


    public List<E> list;

    public int index;

    public long count;

    public int size;

    public Page page;


    @Override
    public int pageCount() {
        long count = this.count;
        return count == 0L ? 1 : (int)Math.ceil((double)count / (double)this.size);
    }

    @Override
    public Page page() {
        return page;
    }

    @Override
    public <T extends E> PanacheQuery<T> range(int startIndex, int lastIndex) {
        return null;
    }

    @Override
    public <T extends E> PanacheQuery<T> withCollation(Collation collation) {
        return null;
    }

    @Override
    public <T extends E> PanacheQuery<T> withReadPreference(ReadPreference readPreference) {
        return null;
    }

    @Override
    public long count() {
        return count;
    }

    @Override
    public <T extends E> List<T> list() {

        List<T> documents = new ArrayList<>();

        list.stream().forEach(entity -> documents.add((T) entity));

        return documents;
    }

    @Override
    public <T extends E> Stream<T> stream() {
        return null;
    }

    @Override
    public <T extends E> T firstResult() {
        return null;
    }

    @Override
    public <T extends E> Optional<T> firstResultOptional() {
        return Optional.empty();
    }

    @Override
    public <T extends E> T singleResult() {
        return null;
    }

    @Override
    public <T extends E> Optional<T> singleResultOptional() {
        return Optional.empty();
    }

    @Override
    public boolean hasPreviousPage() {
        return this.index > 0;
    }

    @Override
    public <T> PanacheQuery<T> project(Class<T> type) {
        return null;
    }

    @Override
    public <T extends E> PanacheQuery<T> page(Page page) {
        return null;
    }

    @Override
    public <T extends E> PanacheQuery<T> page(int pageIndex, int pageSize) {
        return null;
    }

    @Override
    public <T extends E> PanacheQuery<T> nextPage() {
        return null;
    }

    @Override
    public <T extends E> PanacheQuery<T> previousPage() {
        return null;
    }

    @Override
    public <T extends E> PanacheQuery<T> firstPage() {
        return null;
    }

    @Override
    public <T extends E> PanacheQuery<T> lastPage() {
        return null;
    }

    @Override
    public boolean hasNextPage() {
        return this.index < this.pageCount() - 1;
    }
}
