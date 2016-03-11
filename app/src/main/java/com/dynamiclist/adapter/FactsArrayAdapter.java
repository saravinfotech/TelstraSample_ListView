package com.dynamiclist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dynamiclist.R;
import com.dynamiclist.utilities.Constants;
import com.dynamiclist.model.Row;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter class to provide the dataSource to the ListView
 * Created by Saravanan on 3/7/2016.
 */

public class FactsArrayAdapter extends RecyclerView.Adapter<FactsArrayAdapter.FactsViewHolder>{

    @SuppressWarnings("CanBeFinal")
    private List<Row> factsList;
    @SuppressWarnings("CanBeFinal")
    private Context context;
    private LayoutInflater inflater;

    //@SuppressWarnings("unused")
    //private static final String TAG = FactsArrayAdapter.class.getSimpleName();

    public FactsArrayAdapter(Context context, List<Row> factsList){
        inflater = LayoutInflater.from(context);
        this.factsList = factsList;
        this.context = context;
    }


    @Override
    public FactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.single_row, parent, false);
        return new FactsViewHolder(row);
    }

    @Override
    public void onBindViewHolder(FactsViewHolder holder, int position) {

        Row rowObject = factsList.get(position);

        String stringTitle = rowObject.getTitle();
        String stringDesc = rowObject.getDescription();
        String stringImageURL = rowObject.getImageHref();

        if(stringTitle != null) {
            holder.factsTitle.setText(stringTitle);
        }else {
            holder.factsTitle.setText(Constants.NO_TITLE);
        }
        if(stringDesc != null) {
            holder.factsDesc.setText(stringDesc);
        }else{
            holder.factsDesc.setText(Constants.NO_DESCRIPTION);
        }
        Picasso.with(context)
                .load(stringImageURL)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.img_not_available)         // optional
                .resize(150, 150)
                .into(holder.factsImage);

    }

    @Override
    public int getItemCount() {
        return factsList.size();
    }

    /**
     * View holder class for initializing the row elements
     */
    @SuppressWarnings("CanBeFinal")
    class FactsViewHolder extends RecyclerView.ViewHolder{
        ImageView factsImage;
        TextView factsTitle;
        TextView factsDesc;

       FactsViewHolder(View view){
           super(view);
           factsImage = (ImageView)view.findViewById(R.id.imageView);
           factsTitle = (TextView)view.findViewById(R.id.titleText);
           factsDesc = (TextView)view.findViewById(R.id.descText);
        }

    }

}
