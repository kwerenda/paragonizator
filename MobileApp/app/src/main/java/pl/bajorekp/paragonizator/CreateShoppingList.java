package pl.bajorekp.paragonizator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


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
//        String httpAddress = "[debug] http address: " + sharedPref.getString(getString(R.string.http_address), ""); // would be added to resulting json, needs to be commented
//        shoppingItems.add(httpAddress);
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


    public static String POST(String url, String data){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);


            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(data);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null)
                    result += line;

                inputStream.close();
            }
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private class HttpPostProductList extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            List<String> product_list = adapter.getListData();
            String email = params[1];
            int distance = Integer.getInteger(params[2], 2)*1000;
            int maxShops = Integer.getInteger(params[3], 5);

            ShoppingListPOJO shopping_list = new ShoppingListPOJO();
            shopping_list.email = email;
            shopping_list.radius = distance;
            shopping_list.shopsLimit = maxShops;
            shopping_list.itemNames = (ArrayList<String>) product_list;
            ObjectMapper mapper = new ObjectMapper();
            String result = "";
            try {
                // 4. convert JSONObject to JSON to String
                String json = mapper.writeValueAsString(shopping_list);
                result = POST(params[0],json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return result;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Optimization received!", Toast.LENGTH_LONG).show();
        }
    }

    public void sendToServer(View view) {

        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("Dupa", Context.MODE_PRIVATE);
        String address = sharedPreferences.getString(getString(R.string.http_address), "") + "/api/shopping_list";
        String email = sharedPreferences.getString("Email", "");
        String distance = sharedPreferences.getString(getString(R.string.distance_from_localization), "2");
        String max_shops = sharedPreferences.getString(getString(R.string.maximum_of_shops), "5");
        new HttpPostProductList().execute(address, email, distance, max_shops);
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
