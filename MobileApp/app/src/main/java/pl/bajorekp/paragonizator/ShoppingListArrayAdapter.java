package pl.bajorekp.paragonizator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bogna on 25/10/14.
 */
public class ShoppingListArrayAdapter<T> extends ArrayAdapter<String> {

    private List<String> items;
    private int layoutResourceId;
    private Context context;

    public ShoppingListArrayAdapter(Context context, int resource, ArrayList<String> arrayList) {
        super(context, resource, arrayList);
        this.layoutResourceId = resource;
        this.context = context;
        this.items = arrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView; //TODO:

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        ShoppingItemHolder holder = new ShoppingItemHolder();
        holder.shoppingItem = items.get(position);
        holder.removePaymentButton = (ImageButton)row.findViewById(R.id.shoppingItem_removeItem);
        holder.removePaymentButton.setTag(holder.shoppingItem);

        holder.name = (TextView)row.findViewById(R.id.shoppingItem_name);

        row.setTag(holder);
        setupItem(holder);
        return row;
    }

    public void setListData(ArrayList<String> newShoppingItems) {
        items = newShoppingItems;
    }

    private void setupItem(ShoppingItemHolder holder) {
        holder.name.setText(holder.shoppingItem);
    }

    public static class ShoppingItemHolder {
        String shoppingItem;
        TextView name;
        ImageButton removePaymentButton;

    }
}
