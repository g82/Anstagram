package madwhale.g82.com.anstagram_gangnam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ImageView ivPost = (ImageView) findViewById(R.id.iv_post);

        Intent intent = getIntent();
        Uri photoUri = intent.getData();

        Glide.with(this)
                .load(photoUri)
                .into(ivPost);

    }
}
