package edu.gatech.seclass.prj2.data;

import java.util.Date;

import android.database.Cursor;

public class TransactionFactory implements EntityFactory<Transaction> {

	@Override
	public Transaction fromCursor(Cursor cursor) {
		Transaction t = new Transaction();
		
		int columnAmount = cursor.getColumnIndexOrThrow(Transaction.COLUMN_AMOUNT);
		t.setAmount(cursor.getDouble(columnAmount));
		
		int columnDate = cursor.getColumnIndexOrThrow(Transaction.COLUMN_DATE);
		t.setDate(new Date(cursor.getLong(columnDate)));
		
		int columnCashReward = cursor.getColumnIndexOrThrow(Transaction.COLUMN_CASH_REWARD);
		t.setCashRewardApplied(cursor.getInt(columnCashReward)>0);
		
		int columnGoldReward = cursor.getColumnIndexOrThrow(Transaction.COLUMN_GOLD_REWARD);
		t.setGoldRewardApplied(cursor.getInt(columnGoldReward)>0);
		
		return t;
	}

}
