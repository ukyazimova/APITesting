package apiTests;

public class ActionsPOJO {
    private String action;

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getPostStatus() {
        return postStatus;
    }

    private String coverUrl;

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    private String postStatus;
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    private String caption;
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


}
