package com.g82.ikstagram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.g82.ikstagram.api.Api;
import com.g82.ikstagram.uuid.UserUUID;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostActivity extends AppCompatActivity {

    EditText etText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        final Uri photoUri = intent.getData();

        ImageView ivPost = (ImageView) findViewById(R.id.iv_post);
        etText = (EditText) findViewById(R.id.et_text);
        findViewById(R.id.btn_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //post(photoUri.toString(), etText.getText().toString());
                postToFB(photoUri.toString(), etText.getText().toString());
            }
        });

        Glide.with(this)
                .load(photoUri)
                .centerCrop()
                .crossFade()
                .into(ivPost);

        /** Uri content://media/external/images/media/255 */
        // Uri -> Bitmap -> File
    }

    private void post(String uriString, String textString) {
        PostTask postTask = new PostTask();
        postTask.execute(uriString, textString);
    }

    private void postToFB(final String uriString, final String textString) {

        /*
        TODO
        0.포스트 문서를 만들어서 ID를 발급받는다.
        1.이미지를 올린다. storage//81164xxx/PDLrzsxvxcv(.jpg)
        2.이미지 url 주소를 제공받아서
        3.포스트 도큐먼트(DB)에 이미지 주소를 업데이트 한다.
         */

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> post = new HashMap<>();
        post.put("uploader", UserUUID.getUserUUID(this));
        post.put("text", textString);

        post.put("created_at", new Date());
        post.put("updated_at", new Date());
        post.put("id", -1);

        Map<String, Object> likeMap = new HashMap<>();
        likeMap.put("count" , 20);
        likeMap.put("userliked", false);

        post.put("likes", likeMap);


        db.collection("posts")
                .add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                final String docuId = documentReference.getId();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                //storage//81164xxx/PDLrzsxvxcv(.jpg)
                final StorageReference imageRef =
                        storageRef.child(UserUUID.getUserUUID(PostActivity.this) + "/" + docuId);

                try {
                    Bitmap bitmap = getBitmapFromUri(Uri.parse(uriString));
                    File imageFile = createFileFromBitmap(bitmap);

                    Uri file = Uri.fromFile(imageFile);

                    UploadTask uploadTask = imageRef.putFile(file);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return imageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri downloadUri = task.getResult();
                            UpdatePostImage(docuId, downloadUri, textString);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void UpdatePostImage(String documentId, Uri imageUri, String textString) {

        Map<String, Object> post = new HashMap<>();
        post.put("imageUrl", imageUri.toString());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("posts").document(documentId)
                .set(post, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //성공!
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //실패!
                        Log.w("Firestore", "Error writing document", e);
                    }
                });
    }


    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, opts);

        int width = opts.outWidth;
        int height = opts.outHeight;

        float sampleRatio = getSampleRatio(width, height);

        opts.inJustDecodeBounds = false;
        opts.inSampleSize = (int) sampleRatio;

        Bitmap resizedBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, opts);

        Log.d("Resizing", "Resized Width / Height : " + resizedBitmap.getWidth() + "/" + resizedBitmap.getHeight());

        parcelFileDescriptor.close();

        return resizedBitmap;
    }

    // 5312 x 2988
    private float getSampleRatio(int width, int height) {

        final int targetWidth = 1280;
        final int targetheight = 1280;

        float ratio;

        if (width > height) {
            // Landscape
            if (width > targetWidth) {
                ratio = (float) width / (float) targetWidth;
            } else ratio = 1f;
        } else {
            // Portrait
            if (height > targetheight) {
                ratio = (float) height / (float) targetheight;
            } else ratio = 1f;
        }

        return Math.round(ratio);
    }


    private File createFileFromBitmap(Bitmap bitmap) throws IOException {

        File newFile = new File(getFilesDir(), makeImageFileName());

        FileOutputStream fileOutputStream = new FileOutputStream(newFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        fileOutputStream.close();

        return newFile;
    }

    private String makeImageFileName() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
        Date date = new Date();
        String strDate = simpleDateFormat.format(date);
        return strDate + ".png";
    }

    class PostTask extends AsyncTask<String, Void, Boolean> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(PostActivity.this, "포스트 업로드 중", "잠시만 기다려주세요..", true, false);
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            /** Ready for post */
            Uri imageUri = Uri.parse(strings[0]);
            String text = strings[1];

            try {
                Bitmap bitmap = getBitmapFromUri(imageUri);
                File imageFile = createFileFromBitmap(bitmap);


                /** HTTP POST */
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("uploader", UserUUID.getUserUUID(PostActivity.this))
                        .addFormDataPart("text", text)
                        .addFormDataPart("image", makeImageFileName(),
                                RequestBody.create(MediaType.parse("image/png"), imageFile))
                        .build();

                Request request = new Request.Builder()
                        .url(Api.UP_POST)
                        .post(requestBody)
                        .build();

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .build();

                Response response = okHttpClient.newCall(request).execute();

                return response.code() == 200;

            } catch (IOException e) {
                Log.d("PostTask", "post failed", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            if (aBoolean) {
                Toast.makeText(PostActivity.this, "success", Toast.LENGTH_SHORT).show();
                Log.d("PostTask", "success");
            } else Log.d("PostTask", "failed");
        }
    }


}
