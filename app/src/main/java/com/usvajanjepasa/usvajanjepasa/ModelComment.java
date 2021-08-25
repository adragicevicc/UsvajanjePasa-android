package com.usvajanjepasa.usvajanjepasa;

public class ModelComment {
    String comID;
    String comment;
    String currEmail;
    String currName;
    String currUid;

    public ModelComment() {
    }

    public ModelComment(String comID2, String comment2, String currUid2, String currName2, String currEmail2) {
        this.comID = comID2;
        this.comment = comment2;
        this.currUid = currUid2;
        this.currName = currName2;
        this.currEmail = currEmail2;
    }

    public String getComID() {
        return this.comID;
    }

    public void setComID(String comID2) {
        this.comID = comID2;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment2) {
        this.comment = comment2;
    }

    public String getCurrUid() {
        return this.currUid;
    }

    public void setCurrUid(String currUid2) {
        this.currUid = currUid2;
    }

    public String getCurrName() {
        return this.currName;
    }

    public void setCurrName(String currName2) {
        this.currName = currName2;
    }

    public String getCurrEmail() {
        return this.currEmail;
    }

    public void setCurrEmail(String currEmail2) {
        this.currEmail = currEmail2;
    }
}
