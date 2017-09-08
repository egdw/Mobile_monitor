package im.hdy.entity;

/**
 * Created by hdy on 2017/9/8.
 */
public class Reply {
    private Integer code;
    private String message;

    public Reply(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}