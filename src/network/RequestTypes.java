package network;

public enum RequestTypes {
	BALANCE,
	MAKE_PAYMENT,
	REQUEST_PAYMENT,
	VIEW_REQUESTS,
	PAY_REQUEST,
	OBTAIN_QR_CODE,
	CONFIRM_QR_CODE,
	NEW_GROUP,
	ADD_USER,
	GROUPS,
	DIVIDE_PAYMENT,
	STATUS_PAYMENT,
	HISTORY,
	LOGIN;
	
	public static RequestTypes getRequestType(String s) {
		switch (s) {
		case "balance":
		case "b":
			return BALANCE;
		case "makepayment":
		case "m":
			return MAKE_PAYMENT;	
		case "requestpayment":
		case "r":
			return REQUEST_PAYMENT;
		case "viewrequests":
		case "v":
			return VIEW_REQUESTS;
		case "payrequest":
		case "p":
			return PAY_REQUEST;
		case "obtainQRcode":
		case "o":
			return OBTAIN_QR_CODE;
		case "confirmQRcode":
		case "c":
			return CONFIRM_QR_CODE;
		case "newgroup":
		case "n":
			return NEW_GROUP;
		case "addu":
		case "a":
			return ADD_USER;
		case "groups":
		case "g":
			return GROUPS;
		case "dividepayment":
		case "d":
			return DIVIDE_PAYMENT;
		case "statuspayments":
		case "s":
			return STATUS_PAYMENT;
		case "history":
		case "h":
			return HISTORY;
		default:
			return null;
		}
	}
}









