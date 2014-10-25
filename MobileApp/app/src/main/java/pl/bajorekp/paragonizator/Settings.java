package pl.bajorekp.paragonizator;

import android.app.Activity;
import android.content.Context;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        TextView textView = (TextView) findViewById(R.id.textView);

        Context context = getApplicationContext();
        sharedPref = context.getSharedPreferences("Dupa", Context.MODE_PRIVATE);
        String napis = sharedPref.getString("Dupa", "Chujnia");
        textView.setText(napis);
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
        Toast.makeText(getApplicationContext(),"Dupa1",Toast.LENGTH_LONG);
        String napis = ((EditText)findViewById(R.id.editTextSettings)).getText().toString();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Dupa", napis);
        editor.commit();

        Toast.makeText(getApplicationContext(),"Dupa2",Toast.LENGTH_LONG);
    }
}
