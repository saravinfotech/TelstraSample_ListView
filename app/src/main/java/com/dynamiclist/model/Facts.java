package com.dynamiclist.model;

/**
 * POJO Class for the response to be stored and retrieved while
 * processing the request
 * Created by Saravanan on 3/7/2016.
 */
/**
 * Class to retrieve the JSON from the server
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
public class Facts {
    @SerializedName("title")
    @Expose
    private String title;
    @SuppressWarnings("Convert2Diamond")
    @SerializedName("rows")
    @Expose
    private List<Row> rows = new ArrayList<Row>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "Facts{" +
                "title='" + title + '\'' +
                ", rows=" + rows +
                '}';
    }
}
