package org.accounting.system.repositories.groupmanagement;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import org.accounting.system.entities.actor.Actor;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.repositories.modulators.AccessibleModulator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ActorRepository extends AccessibleModulator<Actor, String> {

    public Optional<Actor> findByOidcIdAndIssuer(String oidcId, String issuer) {

        return find("oidcId = ?1 and issuer = ?2", oidcId, issuer).stream().findAny();
    }

    public PanacheQuery<Actor> fetchAll(int page, int size) {

        var clients = getMongoCollection()
                .aggregate(List.of(Aggregates.skip(size * (page)), Aggregates.limit(size)), Actor.class)
                .into(new ArrayList<>());

        var count = getMongoCollection()
                .aggregate(List.of(Aggregates.count()))
                .first();

        var projectionQuery = new MongoQuery<Actor>();

        projectionQuery.list = clients;
        projectionQuery.index = page;
        projectionQuery.size = size;
        projectionQuery.count = count == null ? 0L : Long.parseLong(count.get("count").toString());
        projectionQuery.page = Page.of(page, size);

        return projectionQuery;
    }

    public long countDocuments(Date start, Date end) {

        return getMongoCollection().countDocuments(Filters.and(Filters.gte("registered_on", start), Filters.lte("registered_on", adjustEndDate(end))));
    }


    /**
     * Adjusts the given date to the end of the day (23:59:59.999).
     * @param date The original end date.
     * @return A new Date set to 23:59:59.999 of the given day.
     */
    private Date adjustEndDate(Date date) {

        var calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
}
