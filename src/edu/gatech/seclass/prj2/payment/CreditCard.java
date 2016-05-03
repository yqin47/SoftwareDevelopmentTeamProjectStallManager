package edu.gatech.seclass.prj2.payment;

public class CreditCard {

	private String holderName;
	private String cardNumber;
	private String expirationDate;
	private String securityCode;
	
	public CreditCard(String fromScanner) throws CreditCardException {
		// format: Everett#Scott#4224875949325382#12312015#000
		String[] split = fromScanner.split("#");
		if(split.length != 5) {
			throw new CreditCardException("Wrong format received from scanner: " + fromScanner);
		}
		setHolderName(split[0] + " " + split[1]);
		setCardNumber(split[2]);
		setExpirationDate(split[3]);
		setSecurityCode(split[4]);
	}
	
	public String getHolderName() {
		return holderName;
	}
	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getSecurityCode() {
		return securityCode;
	}
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	@Override
	public String toString() {
		return "CreditCard [holderName=" + holderName + ", cardNumber="
				+ cardNumber + ", expirationDate=" + expirationDate
				+ ", securityCode=" + securityCode + "]";
	}
	
}
