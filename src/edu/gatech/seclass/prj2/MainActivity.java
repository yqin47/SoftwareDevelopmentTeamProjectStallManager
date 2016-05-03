package edu.gatech.seclass.prj2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.gatech.seclass.prj2.R;

public class MainActivity extends Activity implements OnClickListener {

	private static final String SN = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setDisplayHomeAsUpEnabled(false);

		((Button) findViewById(R.id.btn_add_customer)).setOnClickListener(this);
		((Button) findViewById(R.id.btn_manage_customers)).setOnClickListener(this);
		((Button) findViewById(R.id.btn_transaction)).setOnClickListener(this);			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as we specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;	
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		if(v instanceof Button) {
			switch (v.getId()) {
			case R.id.btn_add_customer:
				Log.d(SN, "Add Customer button clicked");
				startActivity(new Intent(this, CustomerAddActivity.class));
				break;
			case R.id.btn_manage_customers:
				Log.d(SN, "Manage Customers button clicked");
				startActivity(new Intent(this, CustomerListActivity.class));
				break;
			case R.id.btn_transaction:
				Log.d(SN, "Transaction button clicked");
				startActivity(new Intent(this, TransactionActivity.class));
				break;				
			default:
				Log.e(SN, "Unknown button ID: " + v.getId());
				break;
			}
		} else {
			Log.e(SN, "Encountered click event not originating from a button, ID: " + v.getId());
		}
	}
	
}
