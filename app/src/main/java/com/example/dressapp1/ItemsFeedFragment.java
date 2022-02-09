package com.example.dressapp1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ItemsFeedFragment extends Fragment {
    View view;
    SwipeRefreshLayout swipeRefresh;
//    MyAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_items_feed, container, false);

//        DBModel.dbInstance.getAllProducts();
        return view;
    }
//
//    interface OnItemClickListener{
//        void onItemClick(int position, View v);
//    }
//    class MyViewHolder extends RecyclerView.ViewHolder{
//        TextView price, title;
//        ImageView productImg;
//        ImageButton favoriteBtn;
//
//        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
//            super(itemView);
//            price = itemView.findViewById(R.id.product_price);
//            title = itemView.findViewById(R.id.product_title);
//            favoriteBtn = itemView.findViewById(R.id.product_favorite_btn);
//            productImg = itemView.findViewById(R.id.product_img);
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int pos = getAdapterPosition();
//                    if (listener != null) {
//                        listener.onItemClick(pos,v);
//                    }
//                }
//            });
//        }
//
//        public void bind(Product product){
//            price.setText(product.getPrice());
//            title.setText(product.getCategory());
//            String url = product.getImg().toString();
//            if (url != null){
//                Picasso.get()
//                        .load(url)
//                        .placeholder(R.drawable.logo)
//                        .into(productImg);
//            }
//        }
//    }
//
//    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
//
//        OnItemClickListener listener;
//        public void setOnItemClickListener(OnItemClickListener listener){
//            this.listener = listener;
//        }
//
//        @NonNull
//        @Override
//        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = getLayoutInflater().inflate(R.layout.product_view,parent,false);
//            MyViewHolder holder = new MyViewHolder(view,listener);
//            return holder;
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
////            Product product = viewModel.getData().getValue().get(position);
////            holder.bind(student);
//        }
//
////        @Override
////        public int getItemCount() {
////            if (viewModel.getData().getValue() == null) return 0;
////            return viewModel.getData().getValue().size();
////        }
//    }
}