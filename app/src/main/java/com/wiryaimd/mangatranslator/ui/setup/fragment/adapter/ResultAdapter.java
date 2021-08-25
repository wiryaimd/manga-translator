package com.wiryaimd.mangatranslator.ui.setup.fragment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ortiz.touchview.TouchImageView;
import com.wiryaimd.mangatranslator.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyHolder> {

    private ArrayList<Bitmap> bitmapList = new ArrayList<>();

    private Context context;

    public ResultAdapter(Context context) {
        this.context = context;
    }

    public void setBitmapList(ArrayList<Bitmap> bitmapList){
        this.bitmapList = bitmapList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ResultAdapter.MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resultimage, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ResultAdapter.MyHolder holder, int position) {
//        Glide.with(context).load(bitmapList.get(position)).into(holder.imgresult);
        holder.imgresult.setImageBitmap(bitmapList.get(position));
    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public TouchImageView imgresult;

        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imgresult = itemView.findViewById(R.id.resultimg_img);

        }
    }
}
