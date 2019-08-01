package com.example.ramoreserrands.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.ramoreserrands.R;
import com.example.ramoreserrands.model.Product;

import java.text.DecimalFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{

    private Context mContext;
    private List<Product> mData;
    private String view_item;
    private OnItemClickListener mListener;
    RequestOptions options;


    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_product_name,tv_subcategory_name;
        TextView tv_subitem,tv_quantity,tv_cart_price,tv_product_price;
        ImageView img_product,img_subcategory;
        ImageView mDeleteImage;



        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            tv_product_name = itemView.findViewById(R.id.product_name_id);
            img_product = itemView.findViewById(R.id.product_img_id);
            tv_product_price = itemView.findViewById(R.id.product_price_id);

            tv_subcategory_name = itemView.findViewById(R.id.subcategory_name_id);
            img_subcategory = itemView.findViewById(R.id.subcategory_img_id);

            tv_quantity = itemView.findViewById(R.id.quantity_cart);
            tv_subitem = itemView.findViewById(R.id.subitem_id);
            tv_cart_price = itemView.findViewById(R.id.price_cart);

            mDeleteImage = itemView.findViewById(R.id.del_item);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }


    public RecyclerViewAdapter(Context mContext, List<Product> mData, String view_item) {
        this.mContext = mContext;
        this.mData = mData;
        this.view_item = view_item;

        // Request option for Glide
        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .placeholder(R.drawable.loading_shape)
                .error(R.drawable.loading_shape);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (view_item.equals("fav")) {
            view = inflater.inflate(R.layout.favorite_item, viewGroup, false);
        }else{
            view = inflater.inflate(R.layout.checkout_item_2, viewGroup, false);
        }

        return new MyViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {
        String price = formatDecimal(mData.get(position).getProduct_price());
        myViewHolder.tv_product_name.setText(mData.get(position).getProduct_name());
        Glide.with(mContext).load(mData.get(position).getProduct_img()).apply(options).into(myViewHolder.img_product);

        if (view_item.equals("fav")) {
            myViewHolder.tv_product_price.setText("₱ " + price);
        }else {
            String price_total = formatDecimal(mData.get(position).getSubitem_total());
            myViewHolder.tv_subitem.setText("₱ " + price_total);
            myViewHolder.tv_cart_price.setText("₱ " + price);
            myViewHolder.tv_quantity.setText("x "+mData.get(position).getQuantity());
        }




    }

    @Override
    public int getItemCount() {
        return mData.size();
    }



    public static String formatDecimal(String value) {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        return df.format(Double.valueOf(value));
    }

}