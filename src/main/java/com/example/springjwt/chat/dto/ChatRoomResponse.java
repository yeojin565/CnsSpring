package com.example.springjwt.chat.dto;

public class ChatRoomResponse {

    private String roomKey;
    private String lastMessage;
    private String updatedAt;
    private Long otherUserId;
    private String otherUsername;
    private int unreadCount;
    private String profileImageUrl;
    private String postTitle;

    public ChatRoomResponse(String roomKey, String lastMessage, String updatedAt,
                            Long otherUserId, String otherUsername, int unreadCount,
                            String profileImageUrl, String postTitle) {
        this.roomKey = roomKey;
        this.lastMessage = lastMessage;
        this.updatedAt = updatedAt;
        this.otherUserId = otherUserId;
        this.otherUsername = otherUsername;
        this.unreadCount = unreadCount;
        this.profileImageUrl = profileImageUrl;
        this.postTitle = postTitle;
    }

    // Getter
    public String getRoomKey() { return roomKey; }
    public String getLastMessage() { return lastMessage; }
    public String getUpdatedAt() { return updatedAt; }
    public Long getOtherUserId() { return otherUserId; }
    public String getOtherUsername() { return otherUsername; }
    public int getUnreadCount() { return unreadCount; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getPostTitle() { return postTitle; }

    // Setter
    public void setRoomKey(String roomKey) { this.roomKey = roomKey; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public void setOtherUserId(Long otherUserId) { this.otherUserId = otherUserId; }
    public void setOtherUsername(String otherUsername) { this.otherUsername = otherUsername; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void setPostTitle(String postTitle) { this.postTitle = postTitle; }
}
