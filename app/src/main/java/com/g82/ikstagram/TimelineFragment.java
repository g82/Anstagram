package com.g82.ikstagram;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;

import com.g82.ikstagram.api.Api;
import com.g82.ikstagram.uuid.UserUUID;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 10-E
 */
public class TimelineFragment extends Fragment {


    ArrayList<Api.Post> arrayList;
    PostViewAdapter postViewAdapter;

    private String user_id;

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        user_id = UserUUID.getUserUUID(getActivity());
        arrayList = new ArrayList<>();

        //fetchAsyncPosts();
        fetchPostsFromFB();

        // Inflate the layout for this fragment
        View baseView = inflater.inflate(R.layout.fragment_timeline, container, false);
        RecyclerView recyclerView = (RecyclerView) baseView.findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        postViewAdapter = new PostViewAdapter();
        recyclerView.setAdapter(postViewAdapter);

        baseView.findViewById(R.id.fab_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE);

                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    //startCameraActivity();
                    startGallery();
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            2000);
                }


            }
        });
        return baseView;
    }

    public void startCameraActivity() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 1000);
        }
    }

    public void startGallery() {
        Intent cameraIntent = new Intent(Intent.ACTION_GET_CONTENT);
        cameraIntent.setType("image/*");
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 1000);
        }
    }

    /**
     * Main(TimelineFrag) -> run Camera -> main -> Post
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            Log.d("onActivityResult", "Camera SUCCESS");
            Intent startIntent = new Intent(getActivity(), PostActivity.class);
            startIntent.setData(data.getData());
            startActivity(startIntent);
        }

    }

    private void fetchAsyncPosts() {

        FetchPostsTask fetchPostsTask = new FetchPostsTask();
        fetchPostsTask.execute(Api.GET_POST + user_id);
    }

    private void fetchPostsFromFB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            //Success!
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Api.Post post = document.toObject(Api.Post.class);
                                arrayList.add(post);
                            }
                            postViewAdapter.notifyDataSetChanged();
                        }
                        else {
                            Log.w("FBFirestore", "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    interface OnLikeListener {
        void onLike(LikeTaskResponse response);
    }

    class FetchPostsTask extends AsyncTask<String, Void, Api.Post[]> {

        @Override
        protected Api.Post[] doInBackground(String... strings) {

            String url = strings[0];

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                Gson gson = new Gson();
                Api.Post[] posts = gson.fromJson(response.body().charStream(), Api.Post[].class);

                return posts;

            } catch (IOException e) {
                Log.d("FetchPostsTask", e.getMessage());
                return null;
            } catch (JsonSyntaxException e) {
                Log.d("FetchPostsTask", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Api.Post[] posts) {
            super.onPostExecute(posts);

            if (posts == null) return;

            for (Api.Post post : posts) {
                arrayList.add(post);
            }
            postViewAdapter.notifyDataSetChanged();
        }
    }

    class PostViewAdapter extends RecyclerView.Adapter<PostViewHolder> {

        @Override
        public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View baseView = getActivity().getLayoutInflater().inflate(R.layout.item_post, parent, false);
            PostViewHolder postViewHolder = new PostViewHolder(baseView);
            return postViewHolder;
        }

        @Override
        public void onBindViewHolder(PostViewHolder holder, int position) {

            Api.Post item = arrayList.get(position);

            String url = item.getImageUrl();

            Glide.with(TimelineFragment.this)
                    .load(url)
                    .centerCrop()
                    .crossFade()
                    .into(holder.iv_post);

            holder.tv_username.setText(item.getUploader());
            holder.tv_posttext.setText(item.getText());
            holder.tv_postlikecount.setText(String.valueOf(item.getLikes().getCount()));
            holder.chk_like.setChecked(item.getLikes().isUserliked());
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tv_username, tv_postlikecount, tv_posttext;
        public ImageView iv_post;
        public CheckBox chk_like;

        public PostViewHolder(View itemView) {
            super(itemView);
            iv_post = (ImageView) itemView.findViewById(R.id.iv_post_img);
            tv_username = (TextView) itemView.findViewById(R.id.tv_user_nickname);
            tv_postlikecount = (TextView) itemView.findViewById(R.id.tv_like_count);
            tv_posttext = (TextView) itemView.findViewById(R.id.tv_post_text);
            chk_like = (CheckBox) itemView.findViewById(R.id.chk_like);
            chk_like.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final int position = getLayoutPosition();

            Api.Post post = arrayList.get(position);

            Log.d("Like", post.getLikes().isUserliked() + "/" + post.getId() + "/" + user_id);

            boolean like = !(post.getLikes().isUserliked());

            LikeTaskRequest likeTaskRequest = new LikeTaskRequest(like, post.getId(), user_id);
            LikeTask likeTask = new LikeTask(new OnLikeListener() {
                @Override
                public void onLike(LikeTaskResponse response) {
                    Api.Post post = arrayList.get(position);
                    post.getLikes().setCount(response.getLikes());
                    post.getLikes().setUserliked(response.isResult());
                    postViewAdapter.notifyDataSetChanged();
                }
            });
            likeTask.execute(likeTaskRequest);
        }
    }

    class LikeTaskRequest {

        boolean like;
        int post_id;
        String user_id;

        public LikeTaskRequest(boolean like, int post_id, String user_id) {
            this.like = like;
            this.post_id = post_id;
            this.user_id = user_id;
        }

        public boolean isLike() {
            return like;
        }

        public int getPost_id() {
            return post_id;
        }

        public String getUser_id() {
            return user_id;
        }
    }

    class LikeTaskResponse {
        boolean result;
        int likes;

        public boolean isResult() {
            return result;
        }

        public int getLikes() {
            return likes;
        }
    }

    class LikeTask extends AsyncTask<LikeTaskRequest, Void, LikeTaskResponse> {

        private OnLikeListener onLikeListener;

        public LikeTask(OnLikeListener onLikeListener) {
            this.onLikeListener = onLikeListener;
        }

        @Override
        protected LikeTaskResponse doInBackground(LikeTaskRequest... likeTaskRequests) {

            LikeTaskRequest likeInfo = likeTaskRequests[0];

            RequestBody requestBody = new FormBody.Builder()
                    .add("post_id", String.valueOf(likeInfo.getPost_id()))
                    .add("user_id", likeInfo.getUser_id())
                    .build();

            Request request;

            if (likeInfo.isLike()) {
                request = new Request.Builder()
                        .url(Api.POST_LIKE)
                        .post(requestBody)
                        .build();
            } else {
                request = new Request.Builder()
                        .url(Api.DEL_LIKE)
                        .delete(requestBody)
                        .build();
            }

            OkHttpClient okHttpClient = new OkHttpClient();

            try {
                Response response = okHttpClient.newCall(request).execute();

                if (response.code() == 200) {
                    Gson gson = new Gson();
                    LikeTaskResponse taskResponse = gson.fromJson(response.body().charStream(), LikeTaskResponse.class);
                    return taskResponse;
                } else {
                    return null;
                }

            } catch (IOException e) {
                Log.d("LikeTask", "Error", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(LikeTaskResponse likeTaskResponse) {
            super.onPostExecute(likeTaskResponse);
            onLikeListener.onLike(likeTaskResponse);
            Log.d("Like Response", likeTaskResponse.getLikes() + "/" + likeTaskResponse.isResult());
        }
    }

}
