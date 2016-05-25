package madwhale.g82.com.anstagram_gangnam.api;

/**
 * Created by g82 on 5/6/16.
 */
public class Api {

    public static final String BASE_URL = "http://52.79.195.156:3000";
    public static final String GET_POST = BASE_URL + "/api/post";
    public static final String GET_FRIENDS = BASE_URL + "/api/friend";

    public static class Image {
        String url;

        public String getUrl() {
            return url;
        }
    }

    /**
     {
     "id": 53,
     "uploader": "ㅈㅅ",
     "text": "ㅈ",
     "likes": {
     "count": 50,
     "userliked": false
     },
     "image": {
     "url": "https://bucket-anstagram.s3.ap-northeast-2.amazonaws.com/uploads/post/image/53/20160515_062644.png"
     },
     "created_at": "2016-05-15T09:26:46.081Z",
     "updated_at": "2016-05-22T08:19:36.248Z"
     }
     */

    public static class Post {

        int id;
        String uploader;
        String text;

        String created_at;
        String updated_at;
        Image image;
        Likes likes;

        public int getId() {
            return id;
        }

        public String getUploader() {
            return uploader;
        }

        public String getText() {
            return text;
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

        public Likes getLikes() {
            return likes;
        }

        public class Likes {
            int count;
            boolean userliked;

            public int getCount() {
                return count;
            }

            public boolean isUserliked() {
                return userliked;
            }
        }

    }

}
