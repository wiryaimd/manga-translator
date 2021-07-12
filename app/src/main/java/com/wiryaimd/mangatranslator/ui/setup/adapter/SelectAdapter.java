package com.wiryaimd.mangatranslator.ui.setup.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.model.SelectedModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.MyHolder> {

    private List<SelectedModel> selectedList = new ArrayList<>();

    public void setSelectedList(List<SelectedModel> selectedList){
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

        holder.tvtitle.setText(selectedModel.getName());

        if (selectedModel.getType() == SelectedModel.Type.IMAGE){

        }else{

        }
    }

    @Override
    public int getItemCount() {
        return selectedList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public ImageView imgtype;
        public TextView tvtitle;

        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvtitle = itemView.findViewById(R.id.selectedfile_name);
            imgtype = itemView.findViewById(R.id.selectedfile_type);

        }

    }
}
