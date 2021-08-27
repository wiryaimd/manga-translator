package com.wiryaimd.mangatranslator.ui.setup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.model.SelectedModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.MyHolder> {

    private List<SelectedModel> selectedList = new ArrayList<>();

    private Context context;

    public void setSelectedList(Context context, List<SelectedModel> selectedList){
        this.context = context;
        this.selectedList = selectedList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public SelectAdapter.MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selectedfile, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SelectAdapter.MyHolder holder, int position) {
        SelectedModel selectedModel = selectedList.get(position);

        if (selectedModel.getType() == SelectedModel.Type.IMAGE){
            Glide.with(context).load(selectedModel.getUri()).into(holder.imgsrc);
        }else{
            Glide.with(context).load(R.drawable.ic_pdf).into(holder.imgsrc);
        }
    }

    @Override
    public int getItemCount() {
        return selectedList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public ImageView imgsrc;

        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imgsrc = itemView.findViewById(R.id.selectedfile_src);

        }

    }
}
