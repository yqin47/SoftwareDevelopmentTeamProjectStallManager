package edu.gatech.seclass.prj2.data;

import java.util.Date;

import android.content.ContentValues;

/**
 * Represents a single customer.
 */
public class Customer implements Entity {
	
	public static final String TABLE_NAME = "customer";
	
	public static final String COLUMN_FIRST_NAME = "firstname";
	public static final String COLUMN_LAST_NAME = "lastname";
	public static final String COLUMN_ZIPCODE = "zipcode";
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_GOLD_STATUS = "goldstatus";
	public static final String COLUMN_DISCOUNT = "discount";
	public static final String COLUMN_CREATED = "created";
	
    public static final String CUSTOMER_TABLE_CREATE =
            "CREATE TABLE " + Customer.TABLE_NAME + " (" +
            Customer.COLUMN_ID + " INTEGER PRIMARY KEY, " +
            Customer.COLUMN_FIRST_NAME + " TEXT, " +
            Customer.COLUMN_LAST_NAME + " TEXT, " +
            Customer.COLUMN_ZIPCODE + " TEXT, " +
            Customer.COLUMN_EMAIL + " TEXT, " +
            Customer.COLUMN_GOLD_STATUS + " BOOLEAN, " +                
            Customer.COLUMN_CREATED + " LONG, " +
            Customer.COLUMN_DISCOUNT + " DOUBLE);";
    
	private long id;

	private String firstName;
	private String lastName;
	private String zipCode;
	private String emailAddress;
	private boolean goldStatus;
	private double currentAbsoluteDiscount;
	private Date createdDate;
	
	/**
	 * Creates a new instance of {@code Customer} using the current
	 * date and time to populate the {@code created} field.
	 * The initial discount is 0 and the gold status {@code false}.
	 */
	public Customer() {
		super();
		this.createdDate = new Date();
		setCurrentAbsoluteDiscount(0);
		setGoldStatus(false);
	}

	@Override
	public ContentValues contentValues() {
	    ContentValues values = new ContentValues();
	    values.put(COLUMN_FIRST_NAME, getFirstName());
	    values.put(COLUMN_LAST_NAME, getLastName());
	    values.put(COLUMN_EMAIL, getEmailAddress());
	    values.put(COLUMN_ZIPCODE, getZipCode());
	    values.put(COLUMN_GOLD_STATUS, hasGoldStatus());
	    values.put(COLUMN_CREATED, getCreatedDate().getTime());
	    values.put(COLUMN_DISCOUNT, getCurrentAbsoluteDiscount());
	    return values;
	}
	
	@Override
	public String tableName() {
		return Customer.TABLE_NAME;
	}

	@Override
	public long getId() {
		return id;
	}
	
	@Override
	public void setId(long id) {
		this.id = id;
	}

	public String fullName() {
		return getFirstName() + " " + getLastName();
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public boolean hasGoldStatus() {
		return goldStatus;
	}
	public void setGoldStatus(boolean goldStatus) {
		this.goldStatus = goldStatus;
	}
	public double getCurrentAbsoluteDiscount() {
		return currentAbsoluteDiscount;
	}
	public void setCurrentAbsoluteDiscount(double discount) {
		this.currentAbsoluteDiscount = discount;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date created) {
		this.createdDate = created;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(currentAbsoluteDiscount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((emailAddress == null) ? 0 : emailAddress.hashCode());
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + (goldStatus ? 1231 : 1237);
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
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
		Customer other = (Customer) obj;
		if (Double.doubleToLongBits(currentAbsoluteDiscount) != Double
				.doubleToLongBits(other.currentAbsoluteDiscount))
			return false;
		if (emailAddress == null) {
			if (other.emailAddress != null)
				return false;
		} else if (!emailAddress.equals(other.emailAddress))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (goldStatus != other.goldStatus)
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (zipCode == null) {
			if (other.zipCode != null)
				return false;
		} else if (!zipCode.equals(other.zipCode))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", zipCode=" + zipCode
				+ ", email=" + emailAddress + ", goldStatus=" + goldStatus
				+ ", discount=" + currentAbsoluteDiscount + ", created=" + createdDate + "]";
	}

}
