package engineering.reverse.ludumcomments;

/**
 * Created by cogito on 7/31/17.
 */

public class CommentData {
    private String comment;
    private String love;
    private String time;
    private String name;
    int id;

    public CommentData(int id, String comment,String love,String time) {
        this.id = id;
        this.comment=comment;
        this.love=love;
        this.time=time;
    }

    public CommentData(int id, String name, String comment,String love,String time) {
        this.id = id;
        this.name = name;
        this.comment=comment;
        this.love=love;
        this.time=time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLove() {
        return love;
    }

    public void setLove(String love) {
        this.love = love;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
