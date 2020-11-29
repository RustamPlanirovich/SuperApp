package com.example.superapp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsResponse {
    private String linkName;
    private String addressLink;
    private String commentLink;
    private String docName;
    private String id;
    private String createTime;
    private Map<String, String> tag;


    public FriendsResponse() {
    }

    public FriendsResponse(String addressLink, String commentLink, String linkName, String docName, String id, String createTime, Map<String, String> tag) {
        this.linkName = linkName;
        this.addressLink = addressLink;
        this.commentLink = commentLink;
        this.docName = docName;
        this.id = id;
        this.createTime = createTime;
        this.tag = tag;
    }

    public String getAddressLink() {
        return addressLink;
    }

    public void setAddressLink(String addressLink) {
        this.addressLink = addressLink;
    }

    public String getCommentLink() {
        return commentLink;
    }

    public void setCommentLink(String commentLink) {
        this.commentLink = commentLink;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getId() {return id;}

    public void setId(String id) { this.id = id;}

    public String getCreateTime() {return createTime;}

    public void setCreateTime(String createTime) { this.createTime = createTime;}

    public Map<String, String> getTag() {return tag;}

    public void setTag(Map<String, String> tag) { this.tag = tag;}

}
