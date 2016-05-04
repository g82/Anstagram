package madwhale.g82.com.anstagram_gangnam;


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

import java.util.ArrayList;

import madwhale.g82.com.anstagram_gangnam.data.DataPostItem;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment {

    ArrayList<DataPostItem> arrayList;

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // MakeData
        arrayList = new ArrayList<>();
        DataPostItem item = new DataPostItem(0,
                "http://res.heraldm.com/content/image/2015/12/15/20151215000161_0.jpg",
                "불꽃놀이했어요~", "ansta_", 1234, false);
        arrayList.add(item);
        arrayList.add(new DataPostItem(1, "http://fimg3.pann.com/new/download.jsp?FileKey=DEE066DFF33E3701F8AD3940B201F711",
                "하이!", "g82", 200000, false));

        arrayList.add(new DataPostItem(2, "http://news20.busan.com/content/image/2015/09/13/20150913000163_0.jpg",
                "하ggggg이!", "g82", 200000, false));

        // Inflate the layout for this fragment
        View baseView = inflater.inflate(R.layout.fragment_timeline, container, false);
        RecyclerView recyclerView = (RecyclerView) baseView.findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new PostViewAdapter());

        return baseView;
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

            DataPostItem item = arrayList.get(position);

            String url = item.getPostImgUrl();

            Glide.with(TimelineFragment.this)
                    .load(url)
                    .centerCrop()
                    .crossFade()
                    .into(holder.iv_post);

            holder.tv_username.setText(item.getUserName());
            holder.tv_posttext.setText(item.getPostText());
            holder.tv_postlikecount.setText( String.valueOf( item.getPost_likes_count() ) );
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
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
