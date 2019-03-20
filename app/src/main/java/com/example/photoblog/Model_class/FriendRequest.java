package com.example.photoblog.Model_class;

import java.util.Date;

public class FriendRequest {

    public String  user_requst_id,friendship;
    public Date timestamp;

    public FriendRequest() {
    }

    public FriendRequest(String user_requst_id, String friendship, Date timestamp) {
        this.user_requst_id = user_requst_id;
        this.friendship = friendship;
        this.timestamp = timestamp;
    }

    public String getFriendship() {
        return friendship;
    }

    public void setFriendship(String friendship) {
        this.friendship = friendship;
    }

    public String getUser_requst_id() {
        return user_requst_id;
    }

    public void setUser_requst_id(String user_requst_id) {
        this.user_requst_id = user_requst_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
