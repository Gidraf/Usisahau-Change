package com.glab.usisahauchange.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glab.usisahauchange.R;
import com.glab.usisahauchange.fragments.AddBalance;
import com.glab.usisahauchange.models.Change;
import com.glab.usisahauchange.services.AlertUserService;
import com.glab.usisahauchange.utils.ClearDate;
import com.glab.usisahauchange.utils.EditChangeListener;
import com.glab.usisahauchange.viewmodels.BalanceViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainBalanceAdapter extends RecyclerView.Adapter<DateViewHolder> implements  ClearDate {

    Context context;
    HashMap<String,List<Change>> balances;
    BalanceViewModel balanceViewModel;
    EditChangeListener editChangeListener;
    List<String> dates = new ArrayList<>();;
    BalanceAdapter adapter;

    public MainBalanceAdapter(Context context, BalanceViewModel balanceViewModel, EditChangeListener editChangeListener) {
        this.context = context;
        this.balanceViewModel = balanceViewModel;
        this.editChangeListener =  editChangeListener;
    }

    public void setBalances(List<Change> balances) {
        this.balances = mapListItem(balances);
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.date_list, parent, false);
        adapter = new BalanceAdapter(context, balanceViewModel, this, view, editChangeListener);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        RecyclerView.LayoutManager manager;
        manager = new LinearLayoutManager(context);
        String datekey = dates.get(position);
        List<Change> changes = balances.get(datekey);
        holder.balanceItems.setHasFixedSize(true);
        holder.balanceItems.setLayoutManager(manager);
        holder.balanceItems.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDelete(adapter));
        itemTouchHelper.attachToRecyclerView(holder.balanceItems);
        if ( changes != null && changes.size() > 0 ){
            adapter.setChangeList(changes, position);
        }
        Date date = null;
        try {
            holder.dateText.setText(dates.get(position));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return dates != null ? dates.size() : 0;
    }

    private HashMap<String, List<Change>>  mapListItem ( List<Change> balances) {

        HashMap<String, List<Change>> groupedHashMap = new HashMap<>();

        for(Change balance : balances) {
            String hashMapKey = balance.getCreatedDate();
            if (!this.dates.contains(hashMapKey)){
                this.dates.add(hashMapKey);
            }
            if (groupedHashMap.containsKey(hashMapKey)){
                groupedHashMap.get(hashMapKey).add(balance);
            }
            else {
                List<Change> changeList = new ArrayList<>();
                changeList.add(balance);
                groupedHashMap.put(hashMapKey, changeList);
            }
        }
        return groupedHashMap;

    }

    @Override
    public void clearDate(int index) {
        dates.remove(index);
        notifyDataSetChanged();
    }
}

class DateViewHolder extends RecyclerView.ViewHolder {
    TextView dateText;
    RecyclerView balanceItems;
    public DateViewHolder(@NonNull View itemView) {
        super(itemView);
        dateText = itemView.findViewById(R.id.date_Text);
        balanceItems = itemView.findViewById(R.id.balance_items_recycler_view);
    }
}

class BalanceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View.OnClickListener clickListener;

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    CardView balanceCardHolder;
    TextView balanceText, reminderTimeText, timeText;
    public BalanceViewHolder(@NonNull View itemView) {
        super(itemView);
        balanceCardHolder = itemView.findViewById(R.id.balance_card_holder);
        balanceText = itemView.findViewById(R.id.balance_amount_text);
        reminderTimeText = itemView.findViewById(R.id.reminder_time_text);
        timeText = itemView.findViewById(R.id.time_text);
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        clickListener.onClick(v);
    }
}
class BalanceAdapter extends RecyclerView.Adapter<BalanceViewHolder> {

    Context context;
    Change change;
    BalanceViewModel balanceViewModel;
    List<Change> changeList;
    ClearDate clearDate;
    View view;
    int index;
    EditChangeListener editChangeListener;

    public BalanceAdapter(Context context, BalanceViewModel balanceViewModel, ClearDate clearDate, View view, EditChangeListener editChangeListener ) {
        this.context = context;
        this.balanceViewModel = balanceViewModel;
        this.clearDate = clearDate;
        this.view = view;
        this.editChangeListener = editChangeListener;
    }

    public void setChangeList(List<Change> changeList, int index) {
        this.changeList = changeList;
        this.index = index;
        notifyDataSetChanged();
    }

    public void  deleteBalance(int position){
        Change change = changeList.get(position);
        this.change = change;
        if (position == 0) {
            balanceViewModel.deleteBalace(change);
            changeList.clear();
            clearDate.clearDate(index);
            notifyDataSetChanged();
            showSnackBar(this.change.getBalance());
            return;
        }
            if (balanceViewModel !=null) {
                balanceViewModel.deleteBalace(change);
                showSnackBar(this.change.getBalance());
                notifyDataSetChanged();
            }
        }

    private void showSnackBar(int balance) {
        Snackbar snackbar = Snackbar.make(view,"Umefuta change yako ya " + balance + " bob" ,
                Snackbar.LENGTH_LONG);
        snackbar.setAction("Chorea?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BalanceAdapter.this.undoDelete();
            }
        });
        snackbar.show();
    }

    private void undoDelete() {
        if (change != null) {
            balanceViewModel.addBalance(change);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.balances, parent, false);
        return new BalanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder holder, int position) {
        final Change change = changeList.get(position);
        holder.reminderTimeText.setText(change.getReminderTime() + " min");
        holder.balanceText.setText(change.getBalance() +" bob");
        holder.timeText.setText(change.getCreatedArt());
        holder.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editChangeListener.onEditChange(change);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return changeList != null ? changeList.size() : 0;
    }
}

class SwipeToDelete extends  ItemTouchHelper.SimpleCallback {
    BalanceAdapter balanceAdapter;
    private Drawable icon;
    private final ColorDrawable background;
    Change change;

    public SwipeToDelete(BalanceAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        balanceAdapter = adapter;
        this.change = change;
        icon = ContextCompat.getDrawable(balanceAdapter.context,
                R.drawable.ic_delete_black_24dp);
        background = new ColorDrawable(Color.RED);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
//        Toast.makeText(balanceAdapter.context, String.valueOf(position), Toast.LENGTH_SHORT).show();
        balanceAdapter.deleteBalance(position);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();
        if (dX > 0) { // Swiping to the right

            int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            int iconRight = itemView.getLeft() + iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());

        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }
        background.draw(c);
    }
}




