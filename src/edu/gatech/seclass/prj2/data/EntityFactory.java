package edu.gatech.seclass.prj2.data;

import android.database.Cursor;

public interface EntityFactory<T extends Entity> {

	public T fromCursor(Cursor cursor);
	
}
