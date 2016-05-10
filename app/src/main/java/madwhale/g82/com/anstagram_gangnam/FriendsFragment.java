package madwhale.g82.com.anstagram_gangnam;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import madwhale.g82.com.anstagram_gangnam.api.Api;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {


    public FriendsFragment() {
        // Required empty public constructor
    }

    List<Api.Friend> listFriends;
    FriendsAdapter friendsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        listFriends = new ArrayList<>();

        View baseView = inflater.inflate(R.layout.fragment_friends, container, false);
        RecyclerView rvFriends = (RecyclerView) baseView.findViewById(R.id.rv_friends);
        rvFriends.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        friendsAdapter = new FriendsAdapter();
        rvFriends.setAdapter(friendsAdapter);

        getFriends();

        return baseView;
    }

    class FriendsViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_image;
        public TextView tv_name, tv_status;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_img);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_status = (TextView) itemView.findViewById(R.id.tv_status);
        }
    }

    class FriendsAdapter extends RecyclerView.Adapter<FriendsViewHolder> {

        @Override
        public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = getActivity().getLayoutInflater().inflate(R.layout.item_friend, null);
            FriendsViewHolder viewHolder = new FriendsViewHolder(itemView);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(FriendsViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return listFriends.size();
        }
    }

    private void getFriends() {

        Request request = new Request.Builder()
                .url(Api.GET_FRIENDS)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("FriendFragment", "onFailure", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("FriendFragment", "onResponse");

                if (response.code() == 200) {

                    Gson gson = new Gson();
                    try {
                        Api.Friend[] friends = gson.fromJson(response.body().charStream(),Api.Friend[].class);

                        for (Api.Friend friend : friends) {
                            listFriends.add(friend);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                friendsAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                    catch (JsonSyntaxException e) {
                        Log.d("JSONSystax", "error", e);
                    }

                }
            }
        });


    }

}
