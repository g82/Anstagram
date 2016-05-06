package madwhale.g82.com.anstagram_gangnam.api;

/**
 * Created by g82 on 5/6/16.
 */
public class Api {

    public static final String BASE_URL = "http://52.79.195.156:3000";
    public static final String GET_POST = BASE_URL + "/api/post";

    /**
     * {
     * "id": 1,
     * "uploader": "g82",
     * "text": "현영아...",
     * "likes": 0,
     * "created_at": "2016-05-05T07:27:35.962Z",
     * "updated_at": "2016-05-05T07:27:35.962Z",
     * "image": {
     * "url": "/uploads/post/image/1/IMG_6940.jpg"
     * }
     * }
     */

    public static class Post {

        int id;
        String uploader;
        String text;
        int likes;
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

        public int getLikes() {
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

        public static class Image {
            String url;

            public String getUrl() {
                return url;
            }
        }

    }


}
