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
import android.widget.TextView;
import android.widget.Toast;


public class Settings extends Activity {

    SharedPreferences sharedPref;
    TextView editHttp;
    TextView editMaxDistance;
    TextView editNumberOfShops;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        editHttp = (TextView) findViewById(R.id.editHttp);
        editMaxDistance = (TextView) findViewById(R.id.editMaxDistance);
        editNumberOfShops = (TextView) findViewById(R.id.editNumberOfShops);

        Context context = getApplicationContext();
        sharedPref = context.getSharedPreferences("Dupa", Context.MODE_PRIVATE);
        String httpAddress = sharedPref.getString(getString(R.string.http_address), "");
        String maxDistance = sharedPref.getString(getString(R.string.distance_from_localization), "");
        String maxNumberOfShops = sharedPref.getString(getString(R.string.maximum_of_shops), "");
        editHttp.setText(httpAddress);
        editMaxDistance.setText(maxDistance);
        editNumberOfShops.setText(maxNumberOfShops);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveSettings(View view) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.http_address), editHttp.getText().toString());
        editor.putString(getString(R.string.distance_from_localization), editMaxDistance.getText().toString());
        editor.putString(getString(R.string.maximum_of_shops), editNumberOfShops.getText().toString());
        editor.commit();
        Toast toast = Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(this, CreateShoppingList.class);
        startActivity(intent);
    }
}
