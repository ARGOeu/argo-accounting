package org.accounting.system.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.Instant;

/**
 * The Metric class represents the Metric collection stored in the mongo database.
 * Every instance of this class represents a record in that collection.
 */

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString
@Getter
@Setter
public class Metric extends Entity {

    private ObjectId id;
    @EqualsAndHashCode.Include
    @BsonProperty("resource_id")
    private String resourceId;
    @EqualsAndHashCode.Include
    @BsonProperty("metric_definition_id")
    private String metricDefinitionId;
    @EqualsAndHashCode.Include
    @BsonProperty("time_period_start")

    private Instant start;
    @EqualsAndHashCode.Include
    @BsonProperty("time_period_end")
    private Instant end;
    @BsonProperty("value")
    private double value;

    private String project;

    private String provider;

    private String installation;

    private String infrastructure;
    @BsonProperty("project_id")
    private String projectId;
    @BsonProperty("installation_id")
    private String installationId;

    private String resource;

    @BsonProperty("user_id")
    private String userId;

    @BsonProperty("group_id")
    private String groupId;
}