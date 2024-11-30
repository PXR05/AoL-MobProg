package com.pxr.golf.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Post {

    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        @SerializedName("user")
        @Expose
        private User user;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    public static class User {

        @SerializedName("edge_owner_to_timeline_media")
        @Expose
        private EdgeOwnerToTimelineMedia edgeOwnerToTimelineMedia;

        public EdgeOwnerToTimelineMedia getEdgeOwnerToTimelineMedia() {
            return edgeOwnerToTimelineMedia;
        }

        public void setEdgeOwnerToTimelineMedia(EdgeOwnerToTimelineMedia edgeOwnerToTimelineMedia) {
            this.edgeOwnerToTimelineMedia = edgeOwnerToTimelineMedia;
        }
    }

    public static class EdgeOwnerToTimelineMedia {

        @SerializedName("edges")
        @Expose
        private List<Edge> edges;

        public List<Edge> getEdges() {
            return edges;
        }

        public void setEdges(List<Edge> edges) {
            this.edges = edges;
        }
    }

    public static class Edge {

        @SerializedName("node")
        @Expose
        private Node node;

        public Node getNode() {
            return node;
        }

        public void setNode(Node node) {
            this.node = node;
        }
    }

    public static class Node {

        @SerializedName("shortcode")
        @Expose
        private String shortcode;

        @SerializedName("display_url")
        @Expose
        private String displayUrl;

        public String getShortcode() {
            return shortcode;
        }

        public void setShortcode(String shortcode) {
            this.shortcode = shortcode;
        }

        public String getDisplayUrl() {
            return displayUrl;
        }

        public void setDisplayUrl(String displayUrl) {
            this.displayUrl = displayUrl;
        }
    }
}
