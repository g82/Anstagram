package madwhale.g82.com.anstagram_gangnam.api;

/**
 * Created by g82 on 5/6/16.
 */
public class Api {

    public static final String BASE_URL = "http://52.79.195.156:3000";
    public static final String GET_POST = BASE_URL + "/api/post?user_id=";
    public static final String UP_POST = BASE_URL + "/api/post";
    public static final String POST_LIKE = BASE_URL + "/api/post/like";
    public static final String DEL_LIKE = BASE_URL + "/api/post/like";

    public static class Image {
        String url;

        public String getUrl() {
            return url;
        }
    }

    public static class Likes {
        int count;
        boolean userliked;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public boolean isUserliked() {
            return userliked;
        }

        public void setUserliked(boolean userliked) {
            this.userliked = userliked;
        }
    }

    /**
     *
     * {
     "id": 48,
     "uploader": "g82",
     "text": "하이하이",
     "likes": {
     "count": 0,
     "userliked": false
     },
     "image": {
     "url": "https://bucket-anstagram.s3.ap-northeast-2.amazonaws.com/uploads/post/image/48/20160514_071342.png"
     },
     "created_at": "2016-05-14T10:13:46.997Z",
     "updated_at": "2016-05-14T10:13:46.997Z"
     }
     */

    public static class Post {

        int id;
        String uploader;
        String text;
        Likes likes;
        String created_at;
        String updated_at;
        Image image;

        public int getId() {
            return id;
        }

        public String getUploader() {
            return uploader;
        }

        public String getText() {
            return text;
        }

        public Likes getLikes() {
            return likes;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public Image getImage() {
            return image;
        }


    }

}
