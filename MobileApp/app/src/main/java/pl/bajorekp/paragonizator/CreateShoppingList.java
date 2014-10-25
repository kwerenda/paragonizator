package pl.bajorekp.paragonizator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class CreateShoppingList extends Activity {


    EditText editText;
    ListView listView;
    ArrayList<String> shoppingItems;
    SharedPreferences sharedPref;
    ShoppingListArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shopping_list);
        editText = (EditText) findViewById(R.id.editText);
        listView = (ListView) findViewById(R.id.shoppingItemsListView);
        shoppingItems = new ArrayList<String>();

        Context context = getApplicationContext();
        sharedPref = context.getSharedPreferences("Dupa", Context.MODE_PRIVATE); //TODO:
        String httpAddress = "[debug] http address: " + sharedPref.getString(getString(R.string.http_address), "");
        shoppingItems.add(httpAddress);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shoppingItems);
        adapter = new ShoppingListArrayAdapter<String>(this, R.layout.shopping_list_item, shoppingItems);

        listView.setAdapter(adapter);


    }


    public void addNewItem(View view) {
        String name = editText.getText().toString();
        if(!name.isEmpty()) {
            shoppingItems.add(name);
            adapter.setListData(shoppingItems);
        }
        adapter.notifyDataSetChanged();
        editText.setText(null);

    }

    public void removeShoppingItemOnClickHandler(View view) {
        String itemToRemove = (String)view.getTag();
        adapter.remove(itemToRemove);
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
