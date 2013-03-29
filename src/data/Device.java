package data;

public class Device {
	private String bluetoothAddress, friendlyName;
	private boolean obexPushAvailable = false;
	private boolean distributed = false;
	private int discoveryCount = 1;
	private int discovered = -1;
	private int transactionID = -1;
	private String connectionURL = null;
	private int failureCount = 0;
	private boolean beingDistributedTo = false;
	private boolean declined = false;
	private int distributor = -1;
	
	public Device(String bluetoothAddress, String friendlyName, int discovered) {
		this.bluetoothAddress = bluetoothAddress;
		this.friendlyName = friendlyName;
		this.discovered = discovered;
	}

	public void setBluetoothAddress(String bluetoothAddress) {
		this.bluetoothAddress = bluetoothAddress;
	}

	public String getBluetoothAddress() {
		return bluetoothAddress;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setObexPushAvailable(boolean obexPushAvailable) {
		this.obexPushAvailable = obexPushAvailable;
	}

	public boolean getObexPushAvailable() {
		return obexPushAvailable;
	}
	
	public void incDiscoveryCount() {
		discoveryCount++;
	}
	
	public void resetDiscoveryCount() {
		discoveryCount = 1;
	}
	
	public int getDiscoveryCount() {
		return discoveryCount;
	}

	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}

	public int getTransactionID() {
		return transactionID;
	}
	
	public void setDiscovered(int discovered) {
		this.discovered = discovered;
	}

	public int getDiscovered() {
		return discovered;
	}
	
	public void setConnectionURL(String connectionURL) {
		this.connectionURL = connectionURL;
	}

	public String getConnectionURL() {
		return connectionURL ;
	}

	public synchronized void setHasBeenDistributedTo(boolean distributedTo) {
		this.distributed = distributedTo;
	}

	public synchronized boolean hasBeenDistributedTo() {
		return distributed;
	}

	public void incFailureCount() {
		failureCount++;
	}

	public int getFailureCount() {
		return failureCount;
	}

	public synchronized void setBeingDistributedTo(boolean beingDistributedTo) {
		this.beingDistributedTo = beingDistributedTo;
	}

	public synchronized boolean isBeingDistributedTo() {
		return beingDistributedTo;
	}

	public synchronized void setDeclined(boolean declined) {
		this.declined = declined;
	}

	public synchronized boolean isDeclined() {
		return declined;
	}

	public synchronized void setDistributor(int distributor) {
		this.distributor = distributor;
	}

	public synchronized int getDistributor() {
		return distributor;
	}
}
