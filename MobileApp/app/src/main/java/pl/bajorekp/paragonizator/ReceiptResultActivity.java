package pl.bajorekp.paragonizator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by bogna on 26/10/14.
 */
public class ReceiptResultActivity extends Activity {

    public static final String RECEIPT_DATA = "RECEIPT_DATA";

    private ListView listView;
    private TextView shopName;
    private TextView receiptDate;
    ReceiptListAdapter<ProductOnReceiptPOJO> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Serializable serializableExtra = getIntent().getSerializableExtra(getPackageName() + RECEIPT_DATA);
        ReceiptPOJO receiptPOJO = (ReceiptPOJO)serializableExtra;
        setContentView(R.layout.activity_receipt);
        listView = (ListView)findViewById(R.id.receiptListView);

        shopName = (TextView)findViewById(R.id.shopTextName);
        shopName.setText(receiptPOJO.shop.name);

        receiptDate = (TextView)findViewById(R.id.receiptDate);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
//        System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
        receiptDate.setText(dateFormat.format(date));


        ArrayList<ProductOnReceiptPOJO> products = receiptPOJO.products;

        adapter = new ReceiptListAdapter<ProductOnReceiptPOJO>(this, R.layout.receipt_list_item, products);

        listView.setAdapter(adapter);

        setTitle("Receipt");
    }


}
