package com.dynamiclist.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dynamiclist.R;
import com.dynamiclist.Utilities.Constants;
import com.dynamiclist.model.Row;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Saravanan on 3/7/2016.
 */
public class FactsArrayAdapter extends ArrayAdapter<Row> {

    private List<Row> factsList;
    Context context;

    private static final String TAG = FactsArrayAdapter.class.getSimpleName();

    public FactsArrayAdapter(Context context, List<Row> factsList){
        super(context, R.layout.single_row,factsList);
        this.factsList = factsList;
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        FactsViewHolder factsViewHolder = null;
        if(row == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.single_row, parent, false);
            factsViewHolder = new FactsViewHolder(row);
            row.setTag(factsViewHolder);
            Log.d(TAG, "Row creation first time");
        }else{
            factsViewHolder = (FactsViewHolder) row.getTag();
            Log.d(TAG, "Recycling Rows");
        }

        String stringTitle = factsList.get(position).getTitle();
        String stringDesc = factsList.get(position).getDescription();
        String stringImageURL = factsList.get(position).getImageHref();

        Log.d(TAG, "Title is "+stringTitle);
        Log.d(TAG, "Description is "+stringDesc);
        Log.d(TAG, "Image URL is "+stringImageURL);

        if(stringTitle != null) {
            factsViewHolder.factsTitle.setText(stringTitle);
        }else {
            factsViewHolder.factsTitle.setText(Constants.NO_TITLE);
        }
        if(stringDesc != null) {
            factsViewHolder.factsDesc.setText(stringDesc);
        }else{
            factsViewHolder.factsDesc.setText(Constants.NO_DESCRIPTION);
        }
            Picasso.with(row.getContext())
                    .load(stringImageURL)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.img_not_available)         // optional
                   // .resize(200, 200)
                    .into(factsViewHolder.factsImage);
        return row;
    }

    /**
     * View holder class for initializing the row elements
     */
    class FactsViewHolder{
        ImageView factsImage;
        TextView factsTitle;
        TextView factsDesc;

       FactsViewHolder(View view){
           factsImage = (ImageView)view.findViewById(R.id.imageView);
           factsTitle = (TextView)view.findViewById(R.id.titleText);
           factsDesc = (TextView)view.findViewById(R.id.descText);
        }

    }

}
