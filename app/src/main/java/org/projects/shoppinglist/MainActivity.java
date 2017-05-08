package org.projects.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.util.SparseBooleanArray;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements AreYouSureDialog.OnPositiveListener {

    ListView listView;
    FirebaseListAdapter mAdapter;

    static AreYouSureDialog dialog;
    static Context context;
    final DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("items");

    public FirebaseListAdapter getMyAdapter()
    {
        return mAdapter;
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
        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mAdapter = new FirebaseListAdapter<Product>(this, Product.class, android.R.layout.simple_list_item_multiple_choice, firebase) {
            @Override
            protected void populateView(View view, Product product, int i) {
                TextView textView = (TextView) view.findViewById(android.R.id.text1); //standard android id.
                textView.setText(product.toString());
            }
        };
        listView.setAdapter(mAdapter);

        String name = MyPreferenceFragment.getName(this);
        if(!name.equals("@string/enter_name") || !name.equals(""))
        {
            Toast welcome = Toast.makeText(context, "Welcome back "+MyPreferenceFragment.getName(this), Toast.LENGTH_LONG);
            welcome.show();
        }

        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameInput = (EditText)findViewById(R.id.name);
                String name = nameInput.getText().toString();

                EditText quantityInput = (EditText)findViewById(R.id.quantity);
                Integer quantity = Integer.parseInt(quantityInput.getText().toString());

                firebase.push().setValue(new Product(name, quantity));

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
                int size = getMyAdapter().getCount();

                for(int i = 0; i < size; i++) {
                    if (array.get(i))
                    {
                        listView.setItemChecked(i,false);
                        getMyAdapter().getRef(i).setValue(null);
                    }
                }
                getMyAdapter().notifyDataSetChanged();
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
        if (id == R.id.clearButton) {
            dialog = new MyDialog();
            dialog.show(getFragmentManager(), "MyFragment");
        }

        if (item.getItemId()==R.id.settings)
        {
            //Start our settingsactivity and listen to result - i.e.
            //when it is finished.
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivityForResult(intent,1);
            //notice the 1 here - this is the code we then listen for in the
            //onActivityResult

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelableArrayList("list", products);
    }

    @Override
    public void onPositiveClicked() {
        firebase.removeValue();
        getMyAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
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
