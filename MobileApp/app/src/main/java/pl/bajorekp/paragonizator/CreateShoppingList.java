package pl.bajorekp.paragonizator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class CreateShoppingList extends Activity {


    EditText editText;
    ListView listView;
    ArrayList<String> arrayList;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shopping_list);
        editText = (EditText) findViewById(R.id.editText);
        listView = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<String>();

        Context context = getApplicationContext();
        sharedPref = context.getSharedPreferences("Dupa", Context.MODE_PRIVATE);
        String napis = sharedPref.getString("Dupa", "Chujnia");
        arrayList.add(napis);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

    }


    public void addNewItem(View view) {
        String name = editText.getText().toString();
        if(!name.isEmpty()) {
            arrayList.add(name);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(adapter);
        }
        editText.setText(null);

    }


    public void sendToServer(View view) {
        Intent intent = new Intent(this, ResultOfMatchin.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_shopping_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
