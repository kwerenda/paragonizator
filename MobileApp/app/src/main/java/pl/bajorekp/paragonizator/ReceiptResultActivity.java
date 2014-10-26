package pl.bajorekp.paragonizator;

import android.app.Activity;
import android.os.Bundle;

import java.io.Serializable;

import pl.bajorekp.paragonizator.POJOS.ReceiptPOJO;

/**
 * Created by bogna on 26/10/14.
 */
public class ReceiptResultActivity extends Activity {

    public static final String RECEIPT_DATA = "RECEIPT_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Serializable serializableExtra = getIntent().getSerializableExtra(getPackageName() + RECEIPT_DATA);
        ReceiptPOJO receiptPOJO = (ReceiptPOJO)serializableExtra;
        setContentView(R.layout.activity_receipt);

        setTitle("Receipt");
    }


}
