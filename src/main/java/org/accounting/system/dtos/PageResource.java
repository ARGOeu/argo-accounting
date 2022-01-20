package org.accounting.system.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.mongodb.panache.PanacheQuery;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

@Schema(name="PageResource", description="An object represents the paginated entities.")
public class PageResource<T> {

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Integer.class,
            description = "Page size.",
            example = "10"
    )
    @JsonProperty("size_of_page")
    public int sizeOfPage;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Integer.class,
            description = "Page number.",
            example = "1"
    )
    @JsonProperty("number_of_page")
    public int numberOfPage;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Long.class,
            description = "Total elements.",
            example = "15"
    )
    @JsonProperty("total_elements")
    public long totalElements;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Integer.class,
            description = "Total pages.",
            example = "2"
    )
    @JsonProperty("total_pages")
    public int totalPages;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = Object.class,
            description = "Paginated entities.",
            example = "[\n" +
                    "        {\n" +
                    "            \"id\": \"61eeab81b3b68f5c3f8c4c25\",\n" +
                    "            \"resourceId\": \"argo\",\n" +
                    "            \"metricDefinitionId\": \"61eeab7bb3b68f5c3f8c4c24\",\n" +
                    "            \"start\": \"2022-01-05T09:13:07Z\",\n" +
                    "            \"end\": \"2022-01-05T09:13:07Z\",\n" +
                    "            \"value\": 200.0\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"61eeac69b3b68f5c3f8c4c29\",\n" +
                    "            \"resourceId\": \"argo\",\n" +
                    "            \"metricDefinitionId\": \"61eeab7bb3b68f5c3f8c4c24\",\n" +
                    "            \"start\": \"2022-01-05T09:13:07Z\",\n" +
                    "            \"end\": \"2022-01-05T09:13:07Z\",\n" +
                    "            \"value\": 200.0\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"61eeac69b3b68f5c3f8c4c2a\",\n" +
                    "            \"resourceId\": \"argo\",\n" +
                    "            \"metricDefinitionId\": \"61eeab7bb3b68f5c3f8c4c24\",\n" +
                    "            \"start\": \"2022-01-05T09:13:07Z\",\n" +
                    "            \"end\": \"2022-01-05T09:13:07Z\",\n" +
                    "            \"value\": 200.0\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"61eeac6bb3b68f5c3f8c4c2b\",\n" +
                    "            \"resourceId\": \"argo\",\n" +
                    "            \"metricDefinitionId\": \"61eeab7bb3b68f5c3f8c4c24\",\n" +
                    "            \"start\": \"2022-01-05T09:13:07Z\",\n" +
                    "            \"end\": \"2022-01-05T09:13:07Z\",\n" +
                    "            \"value\": 200.0\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"61eeac6db3b68f5c3f8c4c2c\",\n" +
                    "            \"resourceId\": \"argo\",\n" +
                    "            \"metricDefinitionId\": \"61eeab7bb3b68f5c3f8c4c24\",\n" +
                    "            \"start\": \"2022-01-05T09:13:07Z\",\n" +
                    "            \"end\": \"2022-01-05T09:13:07Z\",\n" +
                    "            \"value\": 200.0\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"61eeac6eb3b68f5c3f8c4c2d\",\n" +
                    "            \"resourceId\": \"argo\",\n" +
                    "            \"metricDefinitionId\": \"61eeab7bb3b68f5c3f8c4c24\",\n" +
                    "            \"start\": \"2022-01-05T09:13:07Z\",\n" +
                    "            \"end\": \"2022-01-05T09:13:07Z\",\n" +
                    "            \"value\": 200.0\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"61eeac6eb3b68f5c3f8c4c2e\",\n" +
                    "            \"resourceId\": \"argo\",\n" +
                    "            \"metricDefinitionId\": \"61eeab7bb3b68f5c3f8c4c24\",\n" +
                    "            \"start\": \"2022-01-05T09:13:07Z\",\n" +
                    "            \"end\": \"2022-01-05T09:13:07Z\",\n" +
                    "            \"value\": 200.0\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"61eeac6fb3b68f5c3f8c4c2f\",\n" +
                    "            \"resourceId\": \"argo\",\n" +
                    "            \"metricDefinitionId\": \"61eeab7bb3b68f5c3f8c4c24\",\n" +
                    "            \"start\": \"2022-01-05T09:13:07Z\",\n" +
                    "            \"end\": \"2022-01-05T09:13:07Z\",\n" +
                    "            \"value\": 200.0\n" +
                    "        }" +
                    "]"
    )
    @JsonProperty("content")
    public final List<T> content;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = PageLink.class,
            description = "Link to paginated entities.",
            example = "[\n" +
                    "        {\n" +
                    "            \"href\": \"http://localhost:8080/accounting-system/metric-definition/61eeab7bb3b68f5c3f8c4c24/metrics?page=1&size=10\",\n" +
                    "            \"rel\": \"first\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"href\": \"http://localhost:8080/accounting-system/metric-definition/61eeab7bb3b68f5c3f8c4c24/metrics?page=2&size=10\",\n" +
                    "            \"rel\": \"last\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"href\": \"http://localhost:8080/accounting-system/metric-definition/61eeab7bb3b68f5c3f8c4c24/metrics?page=1&size=10\",\n" +
                    "            \"rel\": \"self\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"href\": \"http://localhost:8080/accounting-system/metric-definition/61eeab7bb3b68f5c3f8c4c24/metrics?page=2&size=10\",\n" +
                    "            \"rel\": \"next\"\n" +
                    "        }\n" +
                    "    ]"
    )
    @JsonProperty("links")
    public  List<PageLink> links;



    public PageResource(PanacheQuery<T> panacheQuery, UriInfo uriInfo){

        links = new ArrayList<>();
        this.content = panacheQuery.list();
        this.sizeOfPage = panacheQuery.list().size();
        this.numberOfPage = panacheQuery.page().index+1;
        this.totalElements = panacheQuery.count();
        this.totalPages = panacheQuery.pageCount();

        if(totalPages !=1){
            links.add(buildPageLink(uriInfo, 1, sizeOfPage, "first"));
            links.add(buildPageLink(uriInfo, totalPages, sizeOfPage, "last"));
            links.add(buildPageLink(uriInfo, numberOfPage, sizeOfPage, "self"));


            if(panacheQuery.hasPreviousPage() && panacheQuery.list().size()!=0) {
                links.add(buildPageLink(uriInfo, numberOfPage -1, sizeOfPage, "prev"));
            }

            if(panacheQuery.hasNextPage()) {
                links.add(buildPageLink(uriInfo, numberOfPage +1, sizeOfPage, "next"));
            }
        }
    }


    private PageLink buildPageLink(UriInfo uriInfo, int page,int size, String rel) {

         return PageLink
                .builder()
                 .href(uriInfo.getRequestUriBuilder().replaceQueryParam("page", page).replaceQueryParam("size", size).build().toString())
                 .rel(rel)
                 .build();
    }
}
