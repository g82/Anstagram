package madwhale.g82.com.anstagram_gangnam;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import madwhale.g82.com.anstagram_gangnam.apis.Api;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment {

    List<Api.PostModel> listPosts;
    PostViewAdapter postViewAdapter;

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        listPosts = new ArrayList<>();

        asyncFetchPosts();

        // Inflate the layout for this fragment
        View baseView = inflater.inflate(R.layout.fragment_timeline, container, false);
        RecyclerView recyclerView = (RecyclerView) baseView.findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        postViewAdapter = new PostViewAdapter();
        recyclerView.setAdapter(postViewAdapter);

        return baseView;
    }

    private void asyncFetchPosts() {
        FetchPostTask fetchPostTask = new FetchPostTask();
        fetchPostTask.execute(Api.GET_POST);
    }

    class FetchPostTask extends AsyncTask<String, Void, Api.PostModel[]> {

        @Override
        protected Api.PostModel[] doInBackground(String... urls) {

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(urls[0])
                    .build();

            try {
                Response response = client.newCall(request).execute();

                if (response.code() == 200) {
                    Gson gson = new Gson();
                    Api.PostModel[] posts = gson.fromJson(response.body().charStream(), Api.PostModel[].class);
                    return posts;
                }
                else return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Api.PostModel[] postModels) {
            super.onPostExecute(postModels);

            if (postModels == null) return;

            for (Api.PostModel post : postModels) {
                listPosts.add(post);
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

            Api.PostModel post = listPosts.get(position);

            String url = Api.BASE_URL + post.getImage().getUrl();

            Glide.with(TimelineFragment.this)
                    .load(url)
                    .centerCrop()
                    .crossFade()
                    .into(holder.iv_post);

            holder.tv_username.setText(post.getUploader());
            holder.tv_posttext.setText(post.getText());
            holder.tv_postlikecount.setText( String.valueOf( post.getLikes() ) );
        }

        @Override
        public int getItemCount() {
            return listPosts.size();
        }
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_username, tv_postlikecount, tv_posttext;
        public ImageView iv_post;

        public PostViewHolder(View itemView) {
            super(itemView);
            iv_post = (ImageView) itemView.findViewById(R.id.iv_post_img);
            tv_username = (TextView) itemView.findViewById(R.id.tv_user_nickname);
            tv_postlikecount = (TextView) itemView.findViewById(R.id.tv_like_count);
            tv_posttext = (TextView) itemView.findViewById(R.id.tv_post_text);

        }
    }

}
