package edu.gatech.seclass.prj2.data;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class TransactionAdapter extends CursorAdapter {

    private static final String DELIMITER = ", ";
	private static final String AMOUNT = "Amount: ";
    private static final String DATE = "Date: ";
    private static final String REWARDS_APPLIED = "\nRewards Applied: ";
    private static final String REWARDS_GOLD = "GOLD";
    private static final String REWARDS_CASH = "CASH";

	public TransactionAdapter(Context context, Cursor c) {
		super(context, c, 0);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(
				android.R.layout.two_line_list_item, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView amountView = (TextView) view.findViewById(android.R.id.text1);
		TextView detailsView = (TextView) view.findViewById(android.R.id.text2);
		Transaction transaction = new TransactionFactory().fromCursor(cursor);

		Date created = transaction.getDate();
		java.text.DateFormat df = DateFormat.getLongDateFormat(context);
		java.text.DateFormat tf = DateFormat.getTimeFormat(context);
		String dateString = df.format(created) + " " + tf.format(created);
		amountView.setText(AMOUNT + formattedAmount(transaction));
		String rewardsString = REWARDS_APPLIED + listOfRewards(transaction);
		detailsView.setText(DATE + dateString + rewardsString);
	}

	private String listOfRewards(Transaction transaction) {
		List<String> rewards = new ArrayList<String>();
		if(transaction.isCashRewardApplied()) {
			rewards.add(REWARDS_CASH);
		}
		if(transaction.isGoldRewardApplied()) {
			rewards.add(REWARDS_GOLD);
		}
		return TextUtils.join(DELIMITER, rewards);
	}

	private String formattedAmount(Transaction transaction) {
		NumberFormat format = NumberFormat.getCurrencyInstance();
		return format.format(transaction.getAmount());
	}

}
