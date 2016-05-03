package edu.gatech.seclass.prj2.data;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helps persisting data to a SQLite database.
 */
public class DataHelper extends SQLiteOpenHelper {

	private static final String SN = DataHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "team26_stallmanager";

    /**
     * Instantiate a new {@code DataHelper} using the given application context.
     * @param context Android application context
     */
    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		 db.execSQL(Customer.CUSTOMER_TABLE_CREATE);
		 db.execSQL(Transaction.TRANSACTION_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// no upgrade paths, this is the initial version
	}

	/**
	 * Persist the given entity to the DB.
	 * @param tableName the corresponding table name
     * @param db writable database
	 * @param entity a new entity to to persist
	 * @return the same entity with the ID field filled in
	 * @throws PersistanceException if storing to DB did not succeed
	 */
	public <T extends Entity> T add(SQLiteDatabase db, String tableName, T entity) throws PersistanceException {
		long id = db.insert(tableName, null, entity.contentValues());
		if(id == -1) {
			throw new PersistanceException("Could not store entity to DB: " + entity);
		}
		entity.setId(id);
		return entity;
	}
	
	/**
	 * Find the entity using the given row ID.
     * @param db readable database
	 * @param factory the entity factory
	 * @param tableName the corresponding table name
	 * @param rowId DB row ID
	 * @return the DB entity or {@code null} if it could not be found
	 */
	public <T extends Entity> T findById(SQLiteDatabase db, EntityFactory<T> factory,
										 String tableName, long rowId) {
		Cursor cursor = db.query(tableName, null, Entity.COLUMN_ID + "=" + rowId, null, null, null, null);
		T entity = null;
		if (cursor.getCount() == 1 && cursor.moveToFirst()) {
			entity = factory.fromCursor(cursor);
		} else {
			Log.e(SN, "Could not from " + tableName + "with DB row ID: " + rowId);
		}
		return entity;
	}

	/**
	 * Update the entity in the database row specified by the
	 * ID stored in the given instance.
     * @param db writable database
	 * @param entity the instance of the DB entity to update
	 * @param tableName the corresponding table name 
	 * @return {@code true} if persisting was successful
	 */
	public boolean update(SQLiteDatabase db, String tableName, Entity entity) {
		int rows = db.update(tableName, entity.contentValues(), "_id=" + entity.getId(), null);
		return rows == 1;
	}

	/**
	 * Get all transactions of the particular customer.
	 * @param db readable database
	 * @param customerId database ID of the customer
	 * @return cursor to all transactions
	 */
	public Cursor getTransactions(SQLiteDatabase db, long customerId) {
		String selection = Transaction.COLUMN_CUSTOMER_ID + "=" + String.valueOf(customerId);
		Cursor cursor = db.query(Transaction.TABLE_NAME, null, selection, null, null, null, null);
		return cursor;
	}
	
	/**
	 * Get all transactions of the particular customer in a specified time range.
	 * @param db readable database
	 * @param customerId database ID of the customer
	 * @param start start date (UNIX time-stamp)
	 * @param end end date (UNIX time-stamp)
	 * @return cursor to all transactions
	 */
	public Cursor getTransactions(SQLiteDatabase db, long customerId, long start, long end) {
		String selection = Transaction.COLUMN_CUSTOMER_ID + "=" + String.valueOf(customerId) + " AND " + 
							Transaction.COLUMN_DATE + ">" + start + " AND " + Transaction.COLUMN_DATE + 
							"<" + end;
		Cursor cursor = db.query(Transaction.TABLE_NAME, null, selection, null, null, null, null);
		return cursor;
	}
	
	public double totalTransactionsThisYear(SQLiteDatabase db, Customer cust) {
		Calendar now = GregorianCalendar.getInstance();
		int year = now.get(Calendar.YEAR);
		Calendar startCal = GregorianCalendar.getInstance();
		startCal.set(year, 1, 1, 0, 0, 0);
		Calendar endCal = GregorianCalendar.getInstance();
		endCal.set(year, 31, 12, 23, 59, 59);
		Cursor cursor = getTransactions(db, cust.getId(), startCal.getTimeInMillis(), endCal.getTimeInMillis());
		TransactionFactory tf = new TransactionFactory();

		double total = 0;
		for(int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			Transaction transaction = tf.fromCursor(cursor);
			total += transaction.getAmount();
		}
		return total;
	}
	
}
