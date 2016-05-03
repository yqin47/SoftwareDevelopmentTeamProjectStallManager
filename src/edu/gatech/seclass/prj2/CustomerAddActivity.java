package edu.gatech.seclass.prj2;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import edu.gatech.seclass.prj2.data.Customer;
import edu.gatech.seclass.prj2.data.DataHelper;
import edu.gatech.seclass.prj2.data.PersistanceException;

public class CustomerAddActivity extends Activity implements OnClickListener {
	
	private static final String SN = CustomerAddActivity.class.getSimpleName();
	private EditText txtEmail;
	private EditText txtZipCode;
	private EditText txtLastName;
	private EditText txtFirstname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_add);
		((Button) findViewById(R.id.customer_add_btn_save_customer)).setOnClickListener(this);
		txtFirstname = (EditText) findViewById(R.id.customer_add_txt_firstname);
		txtLastName = (EditText) findViewById(R.id.customer_add_txt_lastname);
		txtZipCode = (EditText) findViewById(R.id.customer_add_txt_zipcode);
		txtEmail = (EditText) findViewById(R.id.customer_add_txt_email);
	}

	@Override
	public void onClick(View v) {
		int id = ((Button) v).getId();
		switch (id) {
		case R.id.customer_add_btn_save_customer:
			Log.d(SN, "Save Customer button clicked");
			DataHelper data = new DataHelper(this);
			SQLiteDatabase db = data.getWritableDatabase();
			
			if(verify()) {
				Customer customer = buildCustomer();
				
				try {
					customer = data.add(db, Customer.TABLE_NAME, customer);
					Toast.makeText(this, "Added customer", Toast.LENGTH_SHORT).show();
					Log.i(SN, "Saved new customer to DB: " + customer.toString());
				} catch (PersistanceException e) {
					Toast.makeText(this, "Failed to add customer", Toast.LENGTH_SHORT).show();
					Log.e(SN, "Could not store new customer: " + e.getMessage());
				}
				
				finish();
			}
			
			break;
		default:
			Log.e(SN, "Unknown button ID: " + id);
			break;
		}
		
	}
	
	private boolean verify() {
		if(isEmpty(txtFirstname)) {
			Toast.makeText(this, "First Name missing", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(isEmpty(txtLastName)) {
			Toast.makeText(this, "Last Name missing", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(isEmpty(txtZipCode)) {
			Toast.makeText(this, "Zip Code missing", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(isEmpty(txtEmail)) {
			Toast.makeText(this, "E-Mail Address missing", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(!isValid(txtEmail)) {
			Toast.makeText(this, "E-Mail Address invalid", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private boolean isValid(EditText txt) {
		return Patterns.EMAIL_ADDRESS.matcher(txt.getText().toString()).matches();
	}

	private boolean isEmpty(EditText txt) {
		return txt.getText().toString().trim().isEmpty();
	}

	private Customer buildCustomer() {
		Customer c = new Customer();
		c.setFirstName(txtFirstname.getText().toString());
		c.setLastName(txtLastName.getText().toString());
		c.setZipCode(txtZipCode.getText().toString());
		c.setEmailAddress(txtEmail.getText().toString());
		return c;
	}
	
}
