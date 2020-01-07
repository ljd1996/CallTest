package com.hearing.calltest.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hearing.calltest.R;

import java.util.List;

/**
 * @author liujiadong
 * @since 2020/1/7
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<String> mData;
    private OnItemClickListener mOnItemClickListener;


    public MyAdapter() {

    }

    public MyAdapter(List<String> data) {
        this.mData = data;
    }

    public void setData(List<String> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public String getData(int index) {
        return mData.get(index);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.mTextView.setText(mData.get(position));
        holder.mTextView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onClick(position, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        private ViewHolder(View view) {
            super(view);
            mTextView = view.findViewById(R.id.text);
        }
    }

    public interface OnItemClickListener {
        void onClick(int index, ViewHolder holder);
    }
}
