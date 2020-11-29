package com.example.superapp;


public class FriendsResponse {
    private String linkName;
    private String addressLink;
    private String commentLink;
    private String docName;
    private String id;
    private String createTime;

    public FriendsResponse() {
    }

    public FriendsResponse(String addressLink, String commentLink, String linkName, String docName, String id, String createTime) {
        this.linkName = linkName;
        this.addressLink = addressLink;
        this.commentLink = commentLink;
        this.docName = docName;
        this.id = id;
        this.createTime = createTime;
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
}
