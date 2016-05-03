package edu.gatech.seclass.prj2;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import edu.gatech.seclass.prj2.data.Customer;
import edu.gatech.seclass.prj2.data.CustomerFactory;
import edu.gatech.seclass.prj2.data.DataHelper;

/**
 * An activity representing a single Customer detail screen. 
 */
public class CustomerDetailActivity extends Activity implements OnClickListener, TextWatcher, OnCheckedChangeListener {
	
	private static final String SN = CustomerDetailActivity.class.getSimpleName();

	public static final String CUSTOMER_ROW_ID = "edu.gatech.seclass.prj2.customer_row_id";
	
	private Customer loadedCustomer = null;
	private Customer currentCustomer = null;
	
	private boolean editing = false;
	
	private EditText txtFirstName;
	private EditText txtLastName;
	private EditText txtZipCode;
	private EditText txtDiscount;
	private CheckBox chkGoldStatus;
	private EditText txtEmailAddress;
	private TextView lblCreated;
	private Button btnEdit;
	private Button btnSave;
	private Button btnHistory;

	private long rowId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(SN, "Creating the activity, loading customer");
		setContentView(R.layout.activity_customer_detail);
		
		if(savedInstanceState != null) {
			rowId = savedInstanceState.getLong(CUSTOMER_ROW_ID);
		} else {
			rowId = getIntent().getLongExtra(CUSTOMER_ROW_ID, -1);
		}
		
		if (rowId != -1) {
			DataHelper data = new DataHelper(this);
			SQLiteDatabase db = data.getReadableDatabase();
			loadedCustomer = data.findById(db, new CustomerFactory(), Customer.TABLE_NAME, rowId);
			db.close();
		}

		txtFirstName = (EditText) findViewById(R.id.customer_detail_txt_firstname);
		txtLastName = (EditText) findViewById(R.id.customer_detail_txt_lastname);
		txtZipCode = (EditText) findViewById(R.id.customer_detail_txt_zipcode);
		txtEmailAddress = (EditText) findViewById(R.id.customer_detail_txt_email);
		txtDiscount = (EditText) findViewById(R.id.customer_detail_txt_discount);
		chkGoldStatus = (CheckBox) findViewById(R.id.customer_detail_gold);
		lblCreated = (TextView) findViewById(R.id.customer_detail_lbl_created_display);
		btnEdit = (Button) findViewById(R.id.customer_detail_btn_edit);
		btnSave = (Button) findViewById(R.id.customer_detail_btn_save);
		btnHistory = (Button) findViewById(R.id.customer_detail_btn_history);

		if (loadedCustomer != null) {
			Date created = loadedCustomer.getCreatedDate();
			java.text.DateFormat df = DateFormat.getLongDateFormat(this);
			java.text.DateFormat tf = DateFormat.getTimeFormat(this);
			lblCreated.setText(df.format(created) + " " + tf.format(created));
		}
		
		// add on click listeners
		btnEdit.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		btnHistory.setOnClickListener(this);
		
		// as the last step, add change listener (don't do this before filling in data)
		txtFirstName.addTextChangedListener(this);
		txtLastName.addTextChangedListener(this);
		txtZipCode.addTextChangedListener(this);
		txtEmailAddress.addTextChangedListener(this);
		txtDiscount.addTextChangedListener(this);
		chkGoldStatus.setOnCheckedChangeListener(this);
	}

	private void fillForm(Customer c) {
		if(c != null) {
			txtFirstName.setText(c.getFirstName());
			txtLastName.setText(c.getLastName());
			txtZipCode.setText(c.getZipCode());
			txtEmailAddress.setText(c.getEmailAddress());
			txtDiscount.setText(String.valueOf(c.getCurrentAbsoluteDiscount()));
			chkGoldStatus.setChecked(c.hasGoldStatus());
		}
	}

	@Override
	public void onClick(View v) {
		if(v instanceof Button) {
			DataHelper data = new DataHelper(this);
			switch (v.getId()) {
			case R.id.customer_detail_btn_edit:
				Log.d(SN, "Edit button clicked");
				if(editing == false) {
					txtFirstName.setEnabled(true);
					txtLastName.setEnabled(true);
					txtZipCode.setEnabled(true);
					txtEmailAddress.setEnabled(true);

					btnEdit.setEnabled(false);
					editing = true;
				} else {
					Log.e(SN, "Pressed Edit button while already in Edit mode");
				}
				break;
			case R.id.customer_detail_btn_save:
				if(verify()) {
					currentCustomer.setId(loadedCustomer.getId());
					data.update(data.getWritableDatabase(), Customer.TABLE_NAME, currentCustomer);
					Log.d(SN, "Updated customer: " + currentCustomer.toString());
					finish();					
				}
				break;
			case R.id.customer_detail_btn_history:
				
				long id = loadedCustomer.getId();
				
				Intent detailIntent = new Intent(this, HistoryActivity.class);
				detailIntent.putExtra(CustomerDetailActivity.CUSTOMER_ROW_ID, id);
				startActivity(detailIntent);
				
				break;				
			default:
				Log.e(SN, "Unknown button ID: " + v.getId());
				break;
			}
		} else {
			Log.e(SN, "Encountered click event not originating from a button, ID: " + v.getId());
		}
	}

	private Customer build() {
		Customer c = new Customer(); 
		c.setFirstName(txtFirstName.getText().toString());
		c.setLastName(txtLastName.getText().toString());
		c.setZipCode(txtZipCode.getText().toString());
		c.setEmailAddress(txtEmailAddress.getText().toString());
		Double discount;
		try {
			discount = Double.valueOf(txtDiscount.getText().toString());
		} catch(NumberFormatException e) {
			discount = 0.0;
		}
		c.setCurrentAbsoluteDiscount(discount);
		c.setGoldStatus(chkGoldStatus.isChecked());
		return c;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// no-op
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// no-op
	}

	@Override
	public void afterTextChanged(Editable s) {
		checkDirty();
	}

	private void checkDirty() {
		currentCustomer = build();
		if(!loadedCustomer.equals(currentCustomer)) {
			btnSave.setEnabled(true);	
			btnHistory.setEnabled(false);
		} else {
			btnSave.setEnabled(false);
			btnHistory.setEnabled(true);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		checkDirty();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(SN, "Resuming activity, filling form");
		fillForm(loadedCustomer);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(SN, "Saving row ID to instance state");
		outState.putLong(CUSTOMER_ROW_ID, rowId);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.d(SN, "Restoring row ID from instance state");
		rowId = savedInstanceState.getLong(CUSTOMER_ROW_ID);
	}
	
	private boolean verify() {
		if(isEmpty(txtFirstName)) {
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
		if(isEmpty(txtEmailAddress)) {
			Toast.makeText(this, "E-Mail Address missing", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(!isValid(txtEmailAddress)) {
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
	
}
