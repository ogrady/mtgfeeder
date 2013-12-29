package network;

public class MtGRequestSender extends RequestSender {

	@Override
	protected boolean isGood(String httpResult) {
		// TODO Auto-generated method stub
		return httpResult != null && !httpResult.equals("");
	}

}
