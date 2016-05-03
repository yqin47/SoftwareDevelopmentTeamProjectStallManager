package edu.gatech.seclass.prj2;

import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import edu.gatech.seclass.prj2.data.Customer;
import edu.gatech.seclass.prj2.data.DataHelper;
import edu.gatech.seclass.prj2.data.PersistanceException;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity implements OnClickListener {
	
	private static final String SN = AboutActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		ImageView catView = (ImageView) findViewById(R.id.catView);
		
		// add debug customers, remove before release
		catView.setOnClickListener(this);
		
		try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(
					getPackageName(), 0);
			ZipFile zf = new ZipFile(ai.sourceDir);
			ZipEntry ze = zf.getEntry("classes.dex");
			long time = ze.getTime();
			TextView build = (TextView) findViewById(R.id.lbl_about_build);
			build.setText("Built: " + SimpleDateFormat.getInstance().format(
					new java.util.Date(time)));
			zf.close();
		} catch (Exception e) {
		}
	}

	// add debug customers, remove before release
	@Override
	public void onClick(View v) {
		DataHelper data = new DataHelper(this);
		SQLiteDatabase db = data.getWritableDatabase();
		
		Customer c1 = new Customer();
		c1.setFirstName("John");
		c1.setLastName("Doe");
		c1.setEmailAddress("joe.doe@example.com");
		c1.setGoldStatus(false);
		c1.setZipCode("12345");
		c1.setCurrentAbsoluteDiscount(0);
		Customer c2 = new Customer();
		c2.setFirstName("Mike");
		c2.setLastName("Miller");
		c2.setEmailAddress("mike.miller@example.com");
		c2.setGoldStatus(true);
		c2.setZipCode("6200");
		c2.setCurrentAbsoluteDiscount(10);
		try {
			data.add(db, Customer.TABLE_NAME, c1);
			data.add(db, Customer.TABLE_NAME, c2);
			Toast.makeText(this, "Test customers added to database", Toast.LENGTH_SHORT).show();
		} catch (PersistanceException e) {
			Log.e(SN, "Could not save demo customer", e);
		} finally {
			db.close();			
		}
	}

}
