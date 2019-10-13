package com.glab.usisahauchange.ListItems;

import com.glab.usisahauchange.models.Change;

import java.util.List;

public class ItemList extends ListType {

    Change changeList;

    public Change getChangeList() {
        return changeList;
    }

    public void setChangeList(Change changeList) {
        this.changeList = changeList;
    }

    @Override
    public int getType() {
        return TYPE_BALANCES;
    }
}
