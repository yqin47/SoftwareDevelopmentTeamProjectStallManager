package edu.gatech.seclass.prj2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import edu.gatech.seclass.prj2.data.Customer;
import edu.gatech.seclass.prj2.data.CustomerFactory;
import edu.gatech.seclass.prj2.data.DataHelper;
import edu.gatech.seclass.prj2.data.PersistanceException;
import edu.gatech.seclass.prj2.data.Transaction;
import edu.gatech.seclass.prj2.payment.CreditCard;
import edu.gatech.seclass.prj2.payment.CreditCardException;
import edu.gatech.seclass.prj2.payment.EmailStatus;
import edu.gatech.seclass.services.CreditCardService;
import edu.gatech.seclass.services.EmailService;
import edu.gatech.seclass.services.PaymentService;

public class TransactionActivity extends Activity implements OnClickListener {

	private static final String SN = TransactionActivity.class.getSimpleName();

	public static final String CUSTOMER_ROW_ID = "edu.gatech.seclass.prj2.search_customer_row_id";
	
	private static final CharSequence HOLDER = "Holder: ";
	private static final CharSequence CARD_NUMBER = "Card Number: ";
	private static final CharSequence EXP_DATE = "Exp. Date: ";
	private static final CharSequence SECURITY_CODE = "Security Code: ";

	private static final String CASH_REWARD_SUBJECT = "10$ reward";
	private static final String CASH_REWARD_BODY = "You've received a 10$ reward!";
	private static final int REWARD_AMOUNT = 10;
	private static final int REWARD_THRESHOLD = 100;

	private static final double GOLD_THRESHOLD = 1000;
	private static final double GOLD_PERCENTAGE = 5;
	private static final String GOLD_SUBJECT = "Gold membership";
	private static final String GOLD_BODY = "You're now a gold member for life.";
	
	private TextView holderView;
	private TextView cardNumberView;
	private TextView expDateView;
	private TextView codeView;
	private EditText txtFirstName;
	private EditText txtLastName;
	private EditText txtDiscount;
	private CheckBox chkGoldStatus;
	private EditText txtAmount;
	private Button btnReadCC;
	private Button btnPayment;

	private CreditCard creditCard;
	private Customer customer;
	private EmailStatus emailStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction);
		
		btnReadCC = (Button) findViewById(R.id.transaction_btn_read_cc);
		btnPayment = (Button) findViewById(R.id.transaction_btn_payment);
		
		btnReadCC.setOnClickListener(this);
		btnPayment.setOnClickListener(this);

		holderView = ((TextView) findViewById(R.id.transaction_cc_holder));
		cardNumberView = ((TextView) findViewById(R.id.transaction_cc_card_number));
		expDateView = ((TextView) findViewById(R.id.transaction_cc_exp_date));
		codeView = ((TextView) findViewById(R.id.transaction_cc_code));		
		
		txtFirstName = (EditText) findViewById(R.id.transaction_txt_firstname);
		txtLastName = (EditText) findViewById(R.id.transaction_txt_lastname);
		txtDiscount = (EditText) findViewById(R.id.transaction_txt_discount);
		txtAmount = (EditText) findViewById(R.id.transaction_txt_amount);
		chkGoldStatus = (CheckBox) findViewById(R.id.transaction_gold);

		holderView.setText(HOLDER);
		cardNumberView.setText(CARD_NUMBER);
		expDateView.setText(EXP_DATE);
		codeView.setText(SECURITY_CODE);
		
	    // Get the SearchView and set the searchable configuration
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) findViewById(R.id.transaction_search_customer);
	    // Assumes current activity is the searchable activity
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(getApplicationContext(), SearchActivity.class)));
	    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
	    searchView.setSubmitButtonEnabled(true);
	    
		long rowId = getIntent().getLongExtra(CUSTOMER_ROW_ID, -1);
		if (rowId != -1) {
			DataHelper data = new DataHelper(this);
			SQLiteDatabase db = data.getReadableDatabase();
			customer = data.findById(db, new CustomerFactory(), Customer.TABLE_NAME, rowId);
			fillForm(customer);
			db.close();
		}
		
		this.creditCard = null;
		updatePaymentButtonState();
		emailStatus = EmailStatus.NONE;
	}

	@Override
	public void onClick(View v) {
		if(v instanceof Button) {
			switch (v.getId()) {
			case R.id.transaction_btn_read_cc:
				Log.d(SN, "Read CC button clicked");
				if(processCreditCard()) {
					updatePaymentButtonState();
				} else{
					Toast.makeText(this, "Could not read Credit Card", Toast.LENGTH_SHORT).show();
					updatePaymentButtonState();
				}
				break;
			case R.id.transaction_btn_payment:
				Log.d(SN, "Payment button clicked");
			
				double amount = -1;
				try {
					amount = Double.valueOf(txtAmount.getText().toString());
				} catch(NumberFormatException e) {
					Log.e(SN, "Invalid amount entered when paying");
				}
				if(amount < 0) {
					Toast.makeText(this, "Please enter a valid, positive amount", Toast.LENGTH_SHORT).show();
					break;
				}

				new PaymentProcessOperation().execute(amount);
				


				break;		
			default:
				Log.e(SN, "Unknown button ID: " + v.getId());
				break;
			}
		} else {
			Log.e(SN, "Encountered click event not originating from a button, ID: " + v.getId());
		}
	}

	private void updatePaymentButtonState() {
		if(creditCard == null || customer == null) {
			btnPayment.setEnabled(false);
		} else {
			btnPayment.setEnabled(true);
		}
	}

	private boolean processCreditCard() {
		String cardString = CreditCardService.getCardInfo();
		try {
			creditCard = new CreditCard(cardString);
			Log.i(SN, "Credit Card information read: " + creditCard);
			Log.d(SN, "Credit Card raw format: " + cardString);
			holderView.setText(HOLDER + creditCard.getHolderName());
			cardNumberView.setText(CARD_NUMBER + creditCard.getCardNumber());
			expDateView.setText(EXP_DATE + creditCard.getExpirationDate());
			codeView.setText(SECURITY_CODE + creditCard.getSecurityCode());
			return true;
		} catch (CreditCardException e) {
			Log.e(SN, "Could not process credit card: " + e.getMessage());
			return false;
		}
	}

	private boolean processPayment(double amount) {

		// *** INITIALIZE DB ***
		Transaction transaction = initTransaction(amount);
		DataHelper data = new DataHelper(this);
		SQLiteDatabase db = data.getWritableDatabase();
		
		// *** APPLY REWARDS ***
		double discount = customer.getCurrentAbsoluteDiscount();
		double newDiscount = 0;
		double newAmount = 0;
		
		boolean gold = customer.hasGoldStatus();
		if(gold) {
			newAmount = amount * ( (100 - GOLD_PERCENTAGE) / 100.0);
			Log.d(SN, "Applying gold discount of " + GOLD_PERCENTAGE + "% to payment, new value: " + newAmount);
			transaction.setAmount(newAmount);
			transaction.setGoldRewardApplied(true);
		} else {
			newAmount = amount;
		}
		if(discount > 0) {
			double delta = newAmount - discount;
			if(delta <= 0) {
				newAmount = 0.0;
				newDiscount = Math.abs(delta);
			} else {
				newAmount = delta;
				newDiscount = 0.0;
			}
			Log.d(SN, "Applied cash discount of " + discount + "$ to payment, new value: " + newAmount);
			transaction.setAmount(newAmount);
			transaction.setCashRewardApplied(true);
		}

		// *** DO THE PAYMENT ***
		boolean paymentSuccess = PaymentService.processPayment(customer.getFirstName(),
				customer.getLastName(), creditCard.getCardNumber(), 
				creditCard.getExpirationDate(), creditCard.getSecurityCode(), newAmount);
		
		if(paymentSuccess) {
			Log.i(SN, "Payment of " + amount + " from " + customer.fullName() + " successful.");

			customer.setCurrentAbsoluteDiscount(newDiscount);
			
			// *** STORE TRANSACTION ***
			try {
				Log.d(SN, "Persisting transaction to DB");
				
				transaction = data.add(db, Transaction.TABLE_NAME, transaction);
				Log.i(SN, "Saved new transaction to DB: " + transaction.toString());
			} catch (PersistanceException e) {
				Log.e(SN, "Could not store new transaction: " + e.getMessage());
			}

			// *** UPDATE REWARDS ***
			checkCashReward(newAmount, customer);

			// *** GOLD REWARD? ***
			checkGoldReward(data, db, customer);
			
			// *** PERSIST REWARDS ***
			Log.d(SN, "Persisting customer to DB");
			boolean success = data.update(db, Customer.TABLE_NAME, customer);
			if(success) {
				Log.d(SN, "Updating customer record in DB successful for: " + customer.fullName());
			} else {
				Log.e(SN, "Could not store rewards for customer to DB: " + customer.fullName());
			}
			db.close();
			return true;
		} else {
			Log.w(SN, "Unsuccessful payment of " + newAmount + " from " + customer.fullName());
			return false;
		}
	}

	private void checkGoldReward(DataHelper data, SQLiteDatabase db, Customer customer) {
		if(data.totalTransactionsThisYear(db, customer) >= GOLD_THRESHOLD) {
			Log.d(SN, "Customer reached gold status: " + customer.fullName());
			customer.setGoldStatus(true);
			Log.d(SN, "Sending gold status e-mail: " + customer.fullName());
			boolean success = EmailService.sendEmail(customer.getEmailAddress(),
													 GOLD_SUBJECT, GOLD_BODY);
			if(success) {
				emailStatus = EmailStatus.GOLD_OK;
			} else {
				emailStatus = EmailStatus.GOLD_ERROR;
				Log.e(SN, "Sending gold e-mail to customer unsucessful: " + customer.fullName());
			}
		} else {
			emailStatus = EmailStatus.NONE;
		}
	}

	private void checkCashReward(double amount, Customer customer) {
		if(amount >= REWARD_THRESHOLD) {
			double newDiscount = customer.getCurrentAbsoluteDiscount() + REWARD_AMOUNT;
			Log.d(SN, "Customer received cash award for large purchase: " + customer.fullName() +
					", new absolute discount: " + newDiscount);
			customer.setCurrentAbsoluteDiscount(newDiscount);
			Log.d(SN, "Sending cash reward status e-mail: " + customer.fullName());
			boolean success = EmailService.sendEmail(customer.getEmailAddress(),
													 CASH_REWARD_SUBJECT, CASH_REWARD_BODY);
			
			if(success) {
				emailStatus = EmailStatus.CASH_OK;
			} else {
				emailStatus = EmailStatus.CASH_ERROR;
				Log.e(SN, "Sending cash e-mail to customer unsucessful: " + customer.fullName());
			}
		} else {
			emailStatus = EmailStatus.NONE;
		}
	}

	private Transaction initTransaction(Double amount) {
		Transaction transaction = new Transaction();
		transaction.setAmount(amount);
		transaction.setCustomerId(customer.getId());
		return transaction;
	}
	
	private void fillForm(Customer c) {
		txtFirstName.setText(c.getFirstName());
		txtLastName.setText(c.getLastName());
		txtDiscount.setText(String.valueOf(c.getCurrentAbsoluteDiscount()));
		chkGoldStatus.setChecked(c.hasGoldStatus());
	}

	/**
	 * Overly elaborate set-up to simulate an actual operation going on,
	 * so the user feels "something happened".
	 */
    private class PaymentProcessOperation extends AsyncTask<Double, Void, Boolean> {

    	private static final int PAYMENT_LAG = 1200;

        private ProgressDialog dialog;

		@Override
        protected Boolean doInBackground(Double... amount) {

			TransactionActivity.this.runOnUiThread(new Runnable() {
				  public void run() {
						dialog = new ProgressDialog(TransactionActivity.this);
						dialog.setMessage("Processing Payment");
						dialog.setCancelable(false);  
						dialog.show();
				  }
				});
		
            try {
                Thread.sleep(PAYMENT_LAG);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
            return processPayment(amount[0]);
        }

        @Override
        protected void onPostExecute(final Boolean successful) {
			TransactionActivity.this.runOnUiThread(new Runnable() {
				  public void run() {
					  
						dialog.dismiss();	

						if(successful) {
							Toast.makeText(TransactionActivity.this, "Payment successful", Toast.LENGTH_SHORT).show();

							switch(emailStatus) {
							case CASH_ERROR:
								Toast.makeText(TransactionActivity.this, "Error sending cash reward e-mail", Toast.LENGTH_SHORT).show();
								break;
							case CASH_OK:
								Toast.makeText(TransactionActivity.this, "Cash reward e-mail sent", Toast.LENGTH_SHORT).show();
								break;
							case GOLD_ERROR:
								Toast.makeText(TransactionActivity.this, "Error sending gold member e-mail", Toast.LENGTH_SHORT).show();
								break;
							case GOLD_OK:
								Toast.makeText(TransactionActivity.this, "Gold member e-mail sent", Toast.LENGTH_SHORT).show();
								break;
							case NONE:
							default:
								break;
							}
							
							// if we call finish() we return to the search results. we would need to
							// redesign our general approach on searching for a customer when processing
							// a payment. because of time constraints, we just go back to the
							// main actvity in this hard-core way
							startActivity(new Intent(TransactionActivity.this, MainActivity.class));

						} else {
							Toast.makeText(TransactionActivity.this, "Payment unsuccessful", Toast.LENGTH_SHORT).show();
						}
				  }
				});
        }

    }
    
}
