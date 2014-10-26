package pl.bajorekp.paragonizator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
* Created by bogna on 26/10/14.
*/
public class ReceiptListAdapter<T> extends ArrayAdapter<ProductOnReceiptPOJO> {

    private List<ProductOnReceiptPOJO> items;
    private int layoutResourceId;
    private Context context;

    public ReceiptListAdapter(Context context, int resource, ArrayList<ProductOnReceiptPOJO> arrayList) {
        super(context, resource, arrayList);
        this.layoutResourceId = resource;
        this.context = context;
        this.items = arrayList;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(layoutResourceId, parent, false);

        ProductItemHolder holder = new ProductItemHolder();
        holder.productItem = items.get(position);
//        holder.removePaymentButton = (ImageButton)row.findViewById(R.id.shoppingItem_removeItem);
//        holder.removePaymentButton.setTag(holder.shoppingItem);

        holder.name = (TextView)row.findViewById(R.id.receiptItem_name);

        row.setTag(holder);
        setupItem(holder);
        return row;
    }

    public void setupItem(ProductItemHolder holder) {
//        holder.name.setText(holder.productItem.name);
//        holder.name.setText(holder.productItem);
        holder.name.setText(holder.productItem.name);
    }

    public class ProductItemHolder {
//        String productItem;
        TextView name;
        ProductOnReceiptPOJO productItem;


    }

}
