package org.projects.shoppinglist;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.util.SparseBooleanArray;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AreYouSureDialog.OnPositiveListener {


    ArrayAdapter<Product> adapter;
    ListView listView;
    ArrayList<Product> products = new ArrayList<>();

    static AreYouSureDialog dialog;
    static Context context;

    public ArrayAdapter getMyAdapter()
    {
        return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getting our listiew - you can check the ID in the xml to see that it
        //is indeed specified as "list"
        listView = (ListView) findViewById(R.id.list);

        if (savedInstanceState!=null)
        {
            if (savedInstanceState.containsKey("list"))
                products = savedInstanceState.getParcelableArrayList("list");
        }

        //here we create a new adapter linking the products and the
        //listview
        adapter =  new ArrayAdapter<Product>(this,
                android.R.layout.simple_list_item_checked, products);

        //setting the adapter on the listview
        listView.setAdapter(adapter);
        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameInput = (EditText)findViewById(R.id.name);
                String name = nameInput.getText().toString();

                EditText quantityInput = (EditText)findViewById(R.id.quantity);
                Integer quantity = Integer.parseInt(quantityInput.getText().toString());

                products.add(new Product(name, quantity));
                //The next line is needed in order to say to the ListView
                //that the data has changed - we have added stuff now!
                getMyAdapter().notifyDataSetChanged();
            }
        });

        Button removeButton = (Button) findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray array = listView.getCheckedItemPositions();
                int size = array.size();
                int removed = 0;

                for(int i = 0; i < size; i++) {
                    int key = array.keyAt(i);
                    boolean selected = array.get(key);
                    if (selected)
                    {
                        products.remove(key-removed);
                        listView.setItemChecked(key,false);
                        removed++;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        Button clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showing our dialog.
                dialog = new MyDialog();
                //Here we show the dialog
                //The tag "MyFragement" is not important for us.
                dialog.show(getFragmentManager(), "MyFragment");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("list", products);
    }

    @Override
    public void onPositiveClicked() {
        adapter.clear();
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        products.clear(); //here you can do stuff with the bag and
        //adapter etc.
    }

    public static class MyDialog extends AreYouSureDialog {

        @Override
        protected void negativeClick() {
            //Here we override the method and can now do something
            Toast toast = Toast.makeText(context, "No changes.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
