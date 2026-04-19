package com.bytebrew.bytebrewlibrary;

public class ByteBrewPurchaseResult {
	private boolean purchaseValid;

	private boolean purchaseProcessed;

	private String itemID;

	private String validationTime;

	private String message;

	public ByteBrewPurchaseResult(boolean valid, boolean processed, String message, String itemID, String timestamp) {
		this.purchaseValid = valid;
		this.purchaseProcessed = processed;
		this.itemID = itemID;
		this.validationTime = timestamp;
		this.message = message;
	}

	public boolean isPurchaseValid() {
		return this.purchaseValid;
	}

	public boolean isPurchaseProcessed() {
		return this.purchaseProcessed;
	}

	public String getItemID() {
		return this.itemID;
	}

	public String getValidationTime() {
		return this.validationTime;
	}

	public String getMessage() {
		return this.message;
	}
}
