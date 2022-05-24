package org.accounting.system.entities.projections;

import java.util.List;

public class ProjectionQuery <E>{


    public List<E> list;

    public int index;

    public long count;

    public int size;


    public int pageCount() {
        long count = this.count;
        return count == 0L ? 1 : (int)Math.ceil((double)count / (double)this.size);
    }

    public boolean hasPreviousPage() {
        return this.index > 0;
    }

    public boolean hasNextPage() {
        return this.index < this.pageCount() - 1;
    }
}
