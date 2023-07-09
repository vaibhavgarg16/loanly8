package com.loan8.loan8.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.loan8.loan8.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

public class SliderAdapterExample extends SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

    Context context;
    int[] sliderImages = new int[]{R.drawable.slider1, R.drawable.slider2, R.drawable.slider3};

    public SliderAdapterExample(Context context) {
        this.context = context;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_slider_layout_item, parent, false);
        SliderAdapterVH sliderAdapterVH = new SliderAdapterVH(view);
        return sliderAdapterVH;
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        viewHolder.imgSlidId.setImageResource(sliderImages[position]);
    }

    @Override
    public int getCount() {
        return sliderImages.length;
    }

    public class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        ImageView imgSlidId;
        public SliderAdapterVH(View itemView) {
            super(itemView);
            imgSlidId = itemView.findViewById(R.id.imgSlidId);
        }
    }
}
