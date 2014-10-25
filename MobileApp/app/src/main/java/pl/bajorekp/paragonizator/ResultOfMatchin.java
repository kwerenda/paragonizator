package pl.bajorekp.paragonizator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ResultOfMatchin extends Activity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String NAME = "NAME";
    private static final String IS_EVEN = "IS_EVEN";

    private ExpandableListAdapter mAdapter;
    private ExpandableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_of_matchin);
//
         listView = (ExpandableListView) findViewById(R.id.expandableListView);

        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("ROOT_NAME", "Biedronka");
            }});
            add(new HashMap<String, String>() {{
                put("ROOT_NAME", "TESCO");
            }});
        }};
        List<List<Map<String, String>>> listOfChildGroups = new ArrayList<List<Map<String, String>>>();

        List<Map<String, String>> childGroupForFirstGroupRow = new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>() {{
                put("CHILD_NAME", "Zupa");
                put("CHILD_NAME2", "$12.00");
            }});
            add(new HashMap<String, String>() {{
                put("CHILD_NAME", "Chleb");
                put("CHILD_NAME2", "$4.00");
            }});
        }};
        listOfChildGroups.add(childGroupForFirstGroupRow);

        List<Map<String, String>> childGroupForSecondGroupRow = new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>() {{
                put("CHILD_NAME", "Szynka");
                put("CHILD_NAME2", "$3.25");
            }});
            add(new HashMap<String, String>() {{
                put("CHILD_NAME", "Ser żółty");
                put("CHILD_NAME2", "$10.00");
            }});
        }};
        listOfChildGroups.add(childGroupForSecondGroupRow);
        SimpleExpandableListAdapter adapter =new SimpleExpandableListAdapter(
                this,

                groupData,
                android.R.layout.simple_expandable_list_item_1,
                new String[] { "ROOT_NAME" },
                new int[] { android.R.id.text1 },

                listOfChildGroups,
                android.R.layout.simple_expandable_list_item_2,
                new String[] { "CHILD_NAME", "CHILD_NAME" },
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result_of_matchin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.add_products) {
            Intent intent = new Intent(this, AddProducts.class);
            startActivity(intent);
        } else if (id == R.id.scan_receipt) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
        }
    }
}
