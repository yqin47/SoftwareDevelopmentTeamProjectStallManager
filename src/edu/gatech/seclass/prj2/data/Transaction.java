package edu.gatech.seclass.prj2.data;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Represents a single transaction.
 */
public class Transaction implements Entity {
	
	public static final String TABLE_NAME = "trans";
	
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_AMOUNT = "amount";
	public static final String COLUMN_CASH_REWARD = "cash_reward";
	public static final String COLUMN_GOLD_REWARD = "gold_reward";
	public static final String COLUMN_CUSTOMER_ID = "customer_id";
	
	
    public static final String TRANSACTION_TABLE_CREATE =
            "CREATE TABLE " + Transaction.TABLE_NAME + " (" +
            Transaction.COLUMN_ID + " INTEGER PRIMARY KEY, " +
            Transaction.COLUMN_CUSTOMER_ID + " INTEGER, " +
            Transaction.COLUMN_DATE + " LONG, " +
            Transaction.COLUMN_AMOUNT + " DOUBLE, " +
            Transaction.COLUMN_CASH_REWARD + " BOOLEAN, " +
            Transaction.COLUMN_GOLD_REWARD + " BOOLEAN);";
    
	private long id;
	private long customerId;
	private Date date;
	private double amount;
	private boolean cashRewardApplied;
	private boolean goldRewardApplied;
	
	/**
	 * Creates a new instance of {@code Transaction} using the current
	 * date and time to populate the {@code date} field.
	 * The initial amount is 0 and rewards applied {@code false}.
	 */
	public Transaction() {
		super();
		this.date = new Date();
		setAmount(0);
		setCashRewardApplied(false);
		setGoldRewardApplied(false);
	}

	public Transaction(Cursor cursor) {
		this();
		setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
		setDate(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE))));
		setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)));
		setCashRewardApplied(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CASH_REWARD))>0);
		setGoldRewardApplied(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GOLD_REWARD))>0);
		setCustomerId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_ID)));
	}

	@Override
	public ContentValues contentValues() {
	    ContentValues values = new ContentValues();
	    values.put(COLUMN_DATE, getDate().getTime());
	    values.put(COLUMN_AMOUNT, getAmount());
	    values.put(COLUMN_CASH_REWARD, isCashRewardApplied());
	    values.put(COLUMN_GOLD_REWARD, isGoldRewardApplied());
	    values.put(COLUMN_CUSTOMER_ID, getCustomerId());
	    return values;
	}
	
	@Override
	public String tableName() {
		return Customer.TABLE_NAME;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public boolean isCashRewardApplied() {
		return cashRewardApplied;
	}
	public void setCashRewardApplied(boolean cashRewardApplied) {
		this.cashRewardApplied = cashRewardApplied;
	}
	public boolean isGoldRewardApplied() {
		return goldRewardApplied;
	}
	public void setGoldRewardApplied(boolean goldRewardApplied) {
		this.goldRewardApplied = goldRewardApplied;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (cashRewardApplied ? 1231 : 1237);
		result = prime * result + (int) (customerId ^ (customerId >>> 32));
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + (goldRewardApplied ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		if (Double.doubleToLongBits(amount) != Double
				.doubleToLongBits(other.amount))
			return false;
		if (cashRewardApplied != other.cashRewardApplied)
			return false;
		if (customerId != other.customerId)
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (goldRewardApplied != other.goldRewardApplied)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Transaction [id=" + id + ", customerId=" + customerId
				+ ", date=" + date + ", amount=" + amount
				+ ", cashRewardApplied=" + cashRewardApplied
				+ ", goldRewardApplied=" + goldRewardApplied + "]";
	}

}
