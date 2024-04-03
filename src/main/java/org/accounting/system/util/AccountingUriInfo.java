package org.accounting.system.util;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

public class AccountingUriInfo implements UriInfo {

    private String absoluteString;
    private URI requestURI;
    private URI absolutePath;
    private int queryIdx;


    public AccountingUriInfo(final String absoluteUri) {

        UtilData utilData = new UtilData(absoluteUri);
        this.absoluteString = absoluteUri;
        this.queryIdx = utilData.getQueryIdx();
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public String getPath(boolean b) {
        return null;
    }

    @Override
    public List<PathSegment> getPathSegments() {
        return null;
    }

    @Override
    public List<PathSegment> getPathSegments(boolean b) {
        return null;
    }

    @Override
    public URI getRequestUri() {
        if (requestURI == null) {
            requestURI = URI.create(absoluteString);
        }
        return requestURI;
    }

    @Override
    public UriBuilder getRequestUriBuilder() {
        return UriBuilder.fromUri(getRequestUri());
    }

    @Override
    public URI getAbsolutePath() {
        if (absolutePath == null) {
            absolutePath = queryIdx < 0 ? getRequestUri() : URI.create(absoluteString.substring(0, queryIdx));
        }
        return absolutePath;
    }

    @Override
    public UriBuilder getAbsolutePathBuilder() {
        return UriBuilder.fromUri(getAbsolutePath());
    }

    @Override
    public URI getBaseUri() {
        return null;
    }

    @Override
    public UriBuilder getBaseUriBuilder() {
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getPathParameters() {
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getPathParameters(boolean b) {
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getQueryParameters() {
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getQueryParameters(boolean b) {
        return null;
    }

    @Override
    public List<String> getMatchedURIs() {
        return null;
    }

    @Override
    public List<String> getMatchedURIs(boolean b) {
        return null;
    }

    @Override
    public List<Object> getMatchedResources() {
        return null;
    }

    @Override
    public URI resolve(URI uri) {
        return null;
    }

    @Override
    public URI relativize(URI uri) {
        return null;
    }

    public static class UtilData {

        private final int queryIdx;

        public UtilData(final String absoluteUri) {

            int pathIdx = absoluteUri.indexOf('/');

            if (pathIdx > 0 && absoluteUri.length() > 3 && absoluteUri.charAt(pathIdx - 1) == ':' && absoluteUri.charAt(pathIdx + 1) == '/') {
                pathIdx = pathIdx + 2;
                int tmp = absoluteUri.indexOf('/', pathIdx);
                if (tmp > -1) pathIdx = tmp;
            }

            queryIdx = pathIdx > -1 ? absoluteUri.indexOf('?', pathIdx) : absoluteUri.indexOf('?');
        }

        public int getQueryIdx() {
            return queryIdx;
        }
    }
}
