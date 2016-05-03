package edu.gatech.seclass.prj2;

import edu.gatech.seclass.prj2.data.Customer;
import edu.gatech.seclass.prj2.data.CustomerAdapter;
import edu.gatech.seclass.prj2.data.DataHelper;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

public class SearchActivity extends Activity implements OnItemClickListener {
	
	private static final String SN = SearchActivity.class.getSimpleName();

	private CustomerAdapter adapter;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

	    // Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      Log.i(SN, "Searched for: " + query);
	      populateList(query);
	    }
	}

	private void populateList(String query) {
		DataHelper data = new DataHelper(this);
		SQLiteDatabase db = data.getReadableDatabase();
		String where = Customer.COLUMN_LAST_NAME + " like '%" + query + "%'" + 
						"OR " + Customer.COLUMN_FIRST_NAME + " like '%" + query + "%'";
		Cursor cursor = db.query(Customer.TABLE_NAME, null, where, null, null, null, null);

		listView = (ListView) findViewById(R.id.search_customer_list);
		listView.setOnItemClickListener(this);
		adapter = new CustomerAdapter(this, cursor);
		listView.setAdapter(adapter);
		db.close();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent detailIntent = new Intent(this, TransactionActivity.class);

		detailIntent.putExtra(TransactionActivity.CUSTOMER_ROW_ID, id);
		startActivity(detailIntent);
		finish();
	}
	
}
