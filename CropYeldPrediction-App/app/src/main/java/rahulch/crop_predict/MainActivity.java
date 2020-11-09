package rahulch.crop_predict;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import android.webkit.WebView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
{
    // for listing all cities
    ArrayList<String> listCity=new ArrayList<String>();
    // access all auto complete text views
    AutoCompleteTextView act;
    String city;
    String season;
    String url;
    String area;
    EditText areain;
    boolean ischange = false;
    EditText cityin;
    TextView seasontext;
    Button submitButton;
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        obj_list();
        addCity();
        //final Spinner statesp = (Spinner) findViewById(R.id.spstate);
        cityin = (EditText) findViewById(R.id.actCity);
        areain = (EditText) findViewById(R.id.area);
        seasontext= (TextView) findViewById(R.id.seatext);

        final Spinner seasonsp = (Spinner) findViewById(R.id.spsea);
        submitButton = (Button) findViewById(R.id.submitButton);
        final Handler handler = new Handler(Looper.getMainLooper());
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //state = statesp.getSelectedItem().toString();
                city = cityin.getText().toString();
                area = areain.getText().toString();
                season = seasonsp.getSelectedItem().toString();

                if(city.length()==0)
                {
                    cityin.requestFocus();
                    cityin.setError("FIELD CANNOT BE EMPTY");
                }
                if(area.length()==0)
                {
                    areain.requestFocus();
                    areain.setError("FIELD CANNOT BE EMPTY");
                }
                if(season.equals("- - - Select Season - - -"))
                {
                    seasontext.requestFocus();
                    seasontext.setError("");
                }
                else {
                    if (listCity.contains(city)) {
                        setContentView(R.layout.show);
                        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        final TextView note= (TextView) findViewById(R.id.note);
                        String  notetext="Note:\n1)The production graph shown is predicted production of the city: "+city+" in season: "+season+"\n2)The predicted graph consist top 5 crop on the basis of production\n3)This prediction is based on historical production and rainfall data\n4)Datasets used are authorized and provided by Ministry of Agriculture and Farmers Welfare, Department of Agriculture, Cooperation and Farmers Welfare and Directorate of Economics and Statistics (DES) available on data.gov.in";
                        note.setText(notetext);
                        ischange = true;

                        url = "http://predictcrop.pythonanywhere.com/plot/?inp=" + city + "@" + season + "@" + area;
                        //Intent intent =new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        //startActivity(intent);
                        WebView webView = new WebView(getApplicationContext());
                        webView.loadUrl(url);
                        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", "Loading...",
                                true);
                        dialog.show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Picasso.get().load("http://predictcrop.pythonanywhere.com/static/plot.png").networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
                                dialog.dismiss();
                            }
                        }, 800);
                    }
                    else {
                        cityin.requestFocus();
                        cityin.setError("ENTER A VALID CITY");
                    }
                }
                //Toast.makeText(getApplicationContext(), season, Toast.LENGTH_LONG).show();
            }
        });

    }


    // Get the content of cities.json from assets directory and store it as string
    public String getJson()
    {
        String json=null;
        try
        {
            // Opening cities.json file
            InputStream is = getAssets().open("city.json");
            // is there any content in the file
            int size = is.available();
            byte[] buffer = new byte[size];
            // read values in the byte array
            is.read(buffer);
            // close the stream --- very important
            is.close();
            // convert byte to string
            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return json;
        }
        return json;
    }

    // This add all JSON object's data to the respective lists
    void obj_list()
    {
        // Exceptions are returned by JSONObject when the object cannot be created
        try
        {
            // Convert the string returned to a JSON object
            JSONObject jsonObject=new JSONObject(getJson());
            // Get Json array
            JSONArray array=jsonObject.getJSONArray("array");
            // Navigate through an array item one by one
            for(int i=0;i<array.length();i++)
            {
                // select the particular JSON data
                JSONObject object=array.getJSONObject(i);
                String city=object.getString("city");
                //String state=object.getString("state");
                // add to the lists in the specified format
                listCity.add(city);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    // Add the data items to the spinner
   /* void addToSpinner()
    {
        Spinner spinner=(Spinner)findViewById(R.id.spCity);
        // Adapter for spinner
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }*/

    void addCity()
    {
        act=(AutoCompleteTextView)findViewById(R.id.actCity);
        adapterSetting(listCity);
    }

    // The third auto complete text view

    // setting adapter for auto complete text views
    void adapterSetting(ArrayList arrayList)
    {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,arrayList);
        act.setAdapter(adapter);
        hideKeyBoard();
    }

    // hide keyboard on selecting a suggestion
    public void hideKeyBoard()
    {
        act.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
         });
    }

    @Override
    public void onBackPressed() {
        if (ischange){
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        }else {
            finishAffinity();
        }

    }

}