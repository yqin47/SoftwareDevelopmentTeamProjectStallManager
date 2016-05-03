package edu.gatech.seclass.prj2.data;

import android.content.ContentValues;

public interface Entity {

	public static final String COLUMN_ID = "_id";
	
	public ContentValues contentValues();
	
	public String tableName();

	public long getId();

	public void setId(long id);

}
