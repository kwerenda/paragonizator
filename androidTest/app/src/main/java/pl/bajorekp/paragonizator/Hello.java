package pl.bajorekp.paragonizator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class Hello extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hello, menu);
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

    public void onButtonClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("DUPA", "fajna dupa");
        startActivity(intent);

    }

    public void goToCamera(View view) {
        Intent intent = new Intent(this, Camera.class);
        intent.putExtra("DUPA", "fajna dupa");
        startActivity(intent);

    }

    public void goToSendToServer(View view) {
        Intent intent = new Intent(this, JsonSendPost.class);
        intent.putExtra("DUPA", "fajna dupa");
        startActivity(intent);

    }
}
