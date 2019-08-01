package com.example.ramoreserrands.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.ramoreserrands.R;
import com.example.ramoreserrands.model.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RecyclerSearchViewAdapter extends RecyclerView.Adapter<RecyclerSearchViewAdapter.SearchViewHolder> implements Filterable {

    private Context mContext;
    private List<Product> itemList;
    private List<Product> itemListFull;
    private OnItemCLickListener rListener;
    RequestOptions options;

    private String view_item;

    public static class SearchViewHolder extends RecyclerView.ViewHolder{
        public ImageView img_product, img_subcategory;
        public TextView tv_product_name,tv_product_price, tv_subcategory_name, tv_subitem;
        public EditText edit_quantity;

        public SearchViewHolder(@NonNull View itemView, final OnItemCLickListener listener) {
            super(itemView);
            img_product = itemView.findViewById(R.id.product_img_id);
            tv_product_name = itemView.findViewById(R.id.product_name_id);
            tv_product_price = itemView.findViewById(R.id.product_price_id);

            edit_quantity =  itemView.findViewById(R.id.quantity_cart);
            tv_subitem = itemView.findViewById(R.id.subitem_id);

            tv_subcategory_name = itemView.findViewById(R.id.subcategory_name_id);
            img_subcategory = itemView.findViewById(R.id.subcategory_img_id);

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
        }
    }

    public interface OnItemCLickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener (OnItemCLickListener listener){
        rListener = listener;
    }

    public RecyclerSearchViewAdapter(Context mContext,List<Product> itemList, String view_item) {
        this.mContext = mContext;
        this.itemList = itemList;
        itemListFull = new ArrayList<>(itemList);

        this.view_item = view_item;

        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .placeholder(R.drawable.loading)
                .error(R.drawable.noimage);
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
//
//        SearchViewHolder evh = new SearchViewHolder(v, rListener);
//
//        return evh;

        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (view_item.equals("product")) {
            view = inflater.inflate(R.layout.product_item, parent, false);
        }else if (view_item.equals("cart")){
            view = inflater.inflate(R.layout.checkout_item, parent, false);
        }else {
            view = inflater.inflate(R.layout.sub_category_item,parent,false);
        }

        return new SearchViewHolder(view, rListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchViewHolder searchViewHolder, int position) {
//
//        Product currentItem = itemList.get(position);
//        String price = formatDecimal(itemList.get(position).getProduct_price());
//
//        searchViewHolder.tv_product_name.setText(currentItem.getProduct_name());
//        searchViewHolder.tv_product_price.setText("₱ " + price);
//        String uri = currentItem.getProduct_img();
//        Glide.with(searchViewHolder.itemView.getContext()).load(uri).into(searchViewHolder.img_product);

        if (view_item.equals("product") ) {

            String price = formatDecimal(itemList.get(position).getProduct_price());

            searchViewHolder.tv_product_name.setText(itemList.get(position).getProduct_name());
            searchViewHolder.tv_product_price.setText("₱ " + price);
//            myViewHolder.img_product.setImageResource(R.drawable.grocery_icon);
            GlideApp.with(mContext).load(itemList.get(position).getProduct_img()).apply(options).into(searchViewHolder.img_product);
        }
        else if (view_item.equals("cart")){

            String price = formatDecimal(itemList.get(position).getSubitem_total());

            searchViewHolder.tv_product_name.setText(itemList.get(position).getProduct_name());
            searchViewHolder.tv_subitem.setText("₱ " + price);
            searchViewHolder.edit_quantity.setText(itemList.get(position).getQuantity());
//            myViewHolder.img_product.setImageResource(R.drawable.grocery_icon);
            GlideApp.with(mContext).load(itemList.get(position).getProduct_img()).apply(options).into(searchViewHolder.img_product);

        }
        else {
            searchViewHolder.tv_subcategory_name.setText(itemList.get(position).getSubcategory_name());
//            myViewHolder.img_subcategory.setImageResource(R.drawable.grocery_icon);
            GlideApp.with(mContext).load(itemList.get(position).getSubcategory_img()).apply(options).into(searchViewHolder.img_subcategory);
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }


    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(itemListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Product item : itemListFull) {
                    if (item.getProduct_name().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemList.clear();
            itemList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static String formatDecimal(String value) {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        return df.format(Double.valueOf(value));
    }
}
