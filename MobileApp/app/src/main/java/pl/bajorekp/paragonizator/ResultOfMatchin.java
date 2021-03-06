package pl.bajorekp.paragonizator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ResultOfMatchin extends Activity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String NAME = "NAME";
    private static final String IS_EVEN = "IS_EVEN";

    public static final String OPTIMIZED_LIST = "OPTIMIZED_LIST";

    private ExpandableListAdapter mAdapter;
    private ExpandableListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Serializable serializableExtra = getIntent().getSerializableExtra(getPackageName() + OPTIMIZED_LIST);
        ArrayList<OptimizedShoppingListItemPOJO> optimizedList = (ArrayList<OptimizedShoppingListItemPOJO>)serializableExtra;

        setContentView(R.layout.activity_result_of_matchin);
        listView = (ExpandableListView) findViewById(R.id.expandableListView);

        List<Map<String, String>> groupData = jsonToRootEntries(optimizedList);
        List<List<Map<String, String>>> listOfChildGroups = jsonToChildGroups(optimizedList);

        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                this,

                groupData,
                android.R.layout.simple_expandable_list_item_1,
                new String[] { "ROOT_NAME" },
                new int[] { android.R.id.text1 },

                listOfChildGroups,
                android.R.layout.simple_expandable_list_item_2,
                new String[] { "CHILD_NAME", "CHILD_NAME2" },
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        listView.setAdapter(adapter);

        setTitle("Your shopping route");

        for(int groupNr = 0; groupNr < adapter.getGroupCount(); groupNr++) {
            listView.expandGroup(groupNr, true);
        }
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result_of_matchin, menu);
        return true;
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    private static String multipost(String urlString, MultipartEntity reqEntity) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000000);
            conn.setConnectTimeout(1500000);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.addRequestProperty("Content-length", reqEntity.getContentLength()+"");
            conn.addRequestProperty(reqEntity.getContentType().getName(), reqEntity.getContentType().getValue());

            OutputStream os = conn.getOutputStream();
            reqEntity.writeTo(conn.getOutputStream());
            os.close();
            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return readStream(conn.getInputStream());
            }

        } catch (Exception e) {
            Log.e("MULTIPART_READ", "multipart post error " + e + "(" + urlString + ")");
        }
        return "";
    }

    private class HttpPostReceiptImage extends AsyncTask<String, Void, String> {

        private Activity activity;

        public HttpPostReceiptImage(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {
            String data = params[1];
            ContentBody contentPart = new ByteArrayBody(Base64.decode(data, Base64.DEFAULT), "file");

            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("file", contentPart);


            return multipost(params[0], reqEntity);
        }
        // onPostExecute displays the results of the AsyncTask.


        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Receipt sent", Toast.LENGTH_LONG).show();

            if(result.isEmpty()) {
                Toast.makeText(getBaseContext(), "Error, cannot correct to server", Toast.LENGTH_LONG).show();
                return;
            }

            ReceiptPOJO receiptPOJO = new ReceiptPOJO();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                receiptPOJO = objectMapper.readValue(result, ReceiptPOJO.class);
                Toast.makeText(getBaseContext(), "Receipt received!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "Error parsing receipt JSON", Toast.LENGTH_LONG).show();
            }


            Intent intent = new Intent(activity, ReceiptResultActivity.class);
            intent.putExtra(getPackageName() + ReceiptResultActivity.RECEIPT_DATA, receiptPOJO);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Log.v("BITMAP", String.valueOf(imageBitmap.getHeight()) + " x " + String.valueOf(imageBitmap.getWidth()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            Toast.makeText(getBaseContext(), "Analyzing receipt..", Toast.LENGTH_LONG ).show();

            Context context = getApplicationContext();
            SharedPreferences sharedPreferences = context.getSharedPreferences("Dupa", Context.MODE_PRIVATE);
            String address = sharedPreferences.getString(getString(R.string.http_address), "") + "/api/ocr";
            new HttpPostReceiptImage(this).execute(address, encodedImage);

        }
    }

    private List<Map<String, String>>  jsonToRootEntries(ArrayList<OptimizedShoppingListItemPOJO> jsonList) {
        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();;
        for(final OptimizedShoppingListItemPOJO shoppingEntry : jsonList) {
            groupData.add(new HashMap<String, String>() {{
                put("ROOT_NAME", shoppingEntry.shop.name); //TODO: add localisation
            }});
        }
        return groupData;
    }

    private List<List<Map<String, String>>>  jsonToChildGroups(ArrayList<OptimizedShoppingListItemPOJO> jsonList) {
        List<List<Map<String, String>>> listOfChildGroups = new ArrayList<List<Map<String, String>>>();
        for(final OptimizedShoppingListItemPOJO shoppingEntry : jsonList) {

            List<Map<String, String>> childGroup = new ArrayList<Map<String, String>>();
            for(final ProductPOJO product: shoppingEntry.products) {
                childGroup.add(new HashMap<String, String>() {
                    {
                        put("CHILD_NAME", product.name);
                        put("CHILD_NAME2", product.price + " zl");
                    }});
            }
            listOfChildGroups.add(childGroup);
        }
        return listOfChildGroups;
    }

}
