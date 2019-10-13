package com.glab.usisahauchange.ListItems;

import com.glab.usisahauchange.models.Change;

import java.util.List;

public class DateList extends ListType {

    String dates;

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    @Override
    public int getType() {
        return TYPE_DATE;
    }
}
