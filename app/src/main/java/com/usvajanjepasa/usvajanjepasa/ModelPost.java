package com.usvajanjepasa.usvajanjepasa;

public class ModelPost {
    String postComments;
    String postID;
    String postImage;
    String postText;
    String postTime;
    String uid;
    String usersEmail;
    String usersName;

    public ModelPost() {
    }

    public ModelPost(String postID2, String postText2, String postImage2, String postComments2, String postTime2, String uid2, String usersName2, String usersEmail2) {
        this.postID = postID2;
        this.postText = postText2;
        this.postImage = postImage2;
        this.postComments = postComments2;
        this.postTime = postTime2;
        this.uid = uid2;
        this.usersName = usersName2;
        this.usersEmail = usersEmail2;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid2) {
        this.uid = uid2;
    }

    public String getPostComments() {
        return this.postComments;
    }

    public void setPostComments(String postComments2) {
        this.postComments = postComments2;
    }

    public String getPostID() {
        return this.postID;
    }

    public void setPostID(String postID2) {
        this.postID = postID2;
    }

    public String getPostText() {
        return this.postText;
    }

    public void setPostText(String postText2) {
        this.postText = postText2;
    }

    public String getPostImage() {
        return this.postImage;
    }

    public void setPostImage(String postImage2) {
        this.postImage = postImage2;
    }

    public String getPostTime() {
        return this.postTime;
    }

    public void setPostTime(String postTime2) {
        this.postTime = postTime2;
    }

    public String getUsersName() {
        return this.usersName;
    }

    public void setUsersName(String usersName2) {
        this.usersName = usersName2;
    }

    public String getUsersEmail() {
        return this.usersEmail;
    }

    public void setUsersEmail(String usersEmail2) {
        this.usersEmail = usersEmail2;
    }
}

