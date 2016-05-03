package edu.gatech.seclass.prj2.data;

import java.util.Date;

import android.database.Cursor;

public class CustomerFactory implements EntityFactory<Customer> {

	@Override
	public Customer fromCursor(Cursor cursor) {
		Customer c = new Customer();
		
		int columnId = cursor.getColumnIndexOrThrow(Customer.COLUMN_ID);
		c.setId(cursor.getLong(columnId));
		
		int columnfirstName = cursor.getColumnIndexOrThrow(Customer.COLUMN_FIRST_NAME);
		c.setFirstName(cursor.getString(columnfirstName));
		
		int columnLastName = cursor.getColumnIndexOrThrow(Customer.COLUMN_LAST_NAME);
		c.setLastName(cursor.getString(columnLastName));
		
		int columnZipCode = cursor.getColumnIndexOrThrow(Customer.COLUMN_ZIPCODE);
		c.setZipCode(cursor.getString(columnZipCode));
		
		int columnEmail = cursor.getColumnIndexOrThrow(Customer.COLUMN_EMAIL);
		c.setEmailAddress(cursor.getString(columnEmail));
		
		int columnGoldStatus = cursor.getColumnIndexOrThrow(Customer.COLUMN_GOLD_STATUS);
		c.setGoldStatus(cursor.getInt(columnGoldStatus)>0);
		
		int columnCreated = cursor.getColumnIndexOrThrow(Customer.COLUMN_CREATED);
		c.setCreatedDate(new Date(cursor.getLong(columnCreated)));
		
		int columnDiscount = cursor.getColumnIndexOrThrow(Customer.COLUMN_DISCOUNT);
		c.setCurrentAbsoluteDiscount(cursor.getInt(columnDiscount));
		
		return c;
	}

}
