package com.gls.carwashapp.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gls.carwashapp.R;
import com.gls.carwashapp.model.RealItem;

import java.util.ArrayList;
import java.util.List;


// 리사이클러뷰 어댑터 클래스
public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<RealItem> items;
    OnItemClickListener listener;
    RecyclerView recyclerView;

    public CustomRecyclerViewAdapter(Context context, ArrayList<RealItem> items){
        this.context = context;
        this.items = items;
    }

    public static interface OnItemClickListener{
        public void onItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(RealItem item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void setItem(int index, RealItem item) {
        items.set(index, item);
    }

    public void removeItem(int index){
        items.remove(index+1);
        notifyDataSetChanged();
    }

    public void deleteItem(ArrayList<RealItem> items){
        items.clear();
    }

    public void setItems(ArrayList<RealItem> items) {
        this.items = items;
    }

    public RealItem getItem(int position) {
        return items.get(position);
    }

    // 뷰홀더가 만들어지는 시점 호출
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.real_item, parent, false);
        return new ViewHolder(itemView);
    }

    // xml 파일 결합
    @NonNull
    @Override
    public void onBindViewHolder(@NonNull CustomRecyclerViewAdapter.ViewHolder holder, int position) {
        RealItem item = items.get(position);
        holder.setItem(item);
        holder.setOnItemClickListener(listener);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    // 뷰홀더 클래스 (그릇역할)
    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textAddr;
        TextView textConnect;
        ImageView image;
//        TextView textType;

        OnItemClickListener listener;

        public ViewHolder(View itemView){
            super(itemView);

            textAddr = (TextView) itemView.findViewById(R.id.textAddr);
            textConnect = (TextView) itemView.findViewById(R.id.textConnect);
            image = (ImageView) itemView.findViewById(R.id.image);
//            textType = (TextView) itemView.findViewById(R.id.textType);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null){
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem(RealItem item){
            textAddr.setText(item.getDeviceAddr());
            textConnect.setText(item.getConnect());
            image.setImageResource(item.getResId());
//            textType.setText(Integer.toString(item.getDeviceType()));
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }


    }


}