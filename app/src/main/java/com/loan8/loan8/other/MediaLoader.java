package com.loan8.loan8.other;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.loan8.loan8.R;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumLoader;

public class MediaLoader implements AlbumLoader {

    @Override
    public void load(ImageView imageView, AlbumFile albumFile) {
        load(imageView, albumFile.getPath());
    }

    @Override
    public void load(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .error(R.drawable.main_logo)
                .placeholder(R.drawable.main_logo)
                /*.crossFade()*/
                .into(imageView);
    }
}