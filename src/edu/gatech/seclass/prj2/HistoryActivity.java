package edu.gatech.seclass.prj2;

import edu.gatech.seclass.prj2.data.Customer;
import edu.gatech.seclass.prj2.data.CustomerFactory;
import edu.gatech.seclass.prj2.data.DataHelper;
import edu.gatech.seclass.prj2.data.TransactionAdapter;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class HistoryActivity extends Activity implements OnItemClickListener {

	private static final String SN = HistoryActivity.class.getSimpleName();

	private ListView listView;
	private TransactionAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		long rowId = getIntent().getLongExtra(CustomerDetailActivity.CUSTOMER_ROW_ID, -1);
		if (rowId != -1) {
			DataHelper data = new DataHelper(this);
			SQLiteDatabase db = data.getReadableDatabase();
			Customer customer = data.findById(db, new CustomerFactory(), Customer.TABLE_NAME, rowId);
			populateList(customer.getId());	
			db.close();
		} else {
			Log.e(SN, "Could not identify customer to display history");
		}
	}

	private void populateList(long id) {
		DataHelper data = new DataHelper(this);
		SQLiteDatabase db = data.getReadableDatabase();
		Cursor cursor = data.getTransactions(db, id);
		listView = (ListView) findViewById(R.id.transaction_list);
		listView.setOnItemClickListener(this);
		adapter = new TransactionAdapter(this, cursor);
		listView.setAdapter(adapter);
		db.close();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// noop
	}
	
}
