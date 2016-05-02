package madwhale.g82.com.anstagram_gangnam;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import madwhale.g82.com.anstagram_gangnam.data.DataPostItem;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment {

    public TimelineFragment() {
        // Required empty public constructor
    }

    ArrayList<DataPostItem> arrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // MakeData
        arrayList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            DataPostItem item = new DataPostItem(i, "http://ncache.ilbe.com/files/attach/new/20150520/4255758/5793357203/5852486647/d6e3f3370e25b32915952fe849e958c1.jpg", "불꽃놀이했어요~", "ansta_", 1234, false);
            arrayList.add(i, item);
        }

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

        public PostViewHolder(View itemView) {
            super(itemView);

            tv_username = (TextView) itemView.findViewById(R.id.tv_user_nickname);
            tv_postlikecount = (TextView) itemView.findViewById(R.id.tv_like_count);
            tv_posttext = (TextView) itemView.findViewById(R.id.tv_post_text);

        }
    }

}
