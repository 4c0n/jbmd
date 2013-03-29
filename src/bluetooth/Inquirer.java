package bluetooth;

import java.io.IOException;
import java.util.Vector;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import data.Device;
import data.DiscoveryRelay;
import data.Distributable;
import data.VectorQueue;

public class Inquirer implements DiscoveryListener, Runnable {
	private boolean inquiryCompleted = false;
	private DiscoveryAgent discoveryAgent;
	private LocalDevice device;
	private DiscoveryRelay discoveryRelay;
	private Vector<Device> devices;
	private final static UUID OBEX_PUSH_PROFILE = new UUID(0x1105);
	private int inquiryCount = 0;
	private Distributable distributable;
	private Thread[] distributors;
	private Vector<Device> distributedTo;
	private int maxConnections, maxServiceSearches;
	private VectorQueue<Device> distributionQueue; 
	private VectorQueue<RemoteDevice> serviceSearchQueue;	
	private int activeDistributorCount = 0, activeServiceSearchCount = 0;
	
	public Inquirer(DiscoveryRelay discoveryRelay) throws NoLocalDeviceException {
		this.discoveryRelay = discoveryRelay;
		devices = new Vector<Device>();
		distributedTo = new Vector<Device>();
		distributionQueue = new VectorQueue<Device>();
		serviceSearchQueue = new VectorQueue<RemoteDevice>();
		
		try {
			device = LocalDevice.getLocalDevice();
		} 
		catch (BluetoothStateException e) {
			throw new NoLocalDeviceException();
		}
		maxConnections = Integer.parseInt(LocalDevice.getProperty("bluetooth.connected.devices.max"));
		maxServiceSearches = Integer.parseInt(LocalDevice.getProperty("bluetooth.sd.trans.max"));
		boolean inqscan = Boolean.parseBoolean(LocalDevice.getProperty("bluetooth.connected.inquiry.scan"));
		if (maxConnections < 2 || !inqscan) throw new NoLocalDeviceException();
		
		distributors = new Thread[maxConnections];
		
		System.out.println("There is a local device to work with...");
		System.out.println("API version: " + LocalDevice.getProperty("bluetooth.api.version"));
		System.out.println("Maximum number of connections: " + maxConnections);
		System.out.println("Maximum number of service searches: " + maxServiceSearches);
		System.out.println("Inquiry scanning during connection: " + inqscan);
		System.out.println("Page scanning during connection: " + LocalDevice.getProperty("bluetooth.connected.page.scan"));
		System.out.println("Inquiry allowed during connection: " + LocalDevice.getProperty("bluetooth.connected.inquiry"));
		System.out.println("Paging allowed during connection (multiple connections possible): " + LocalDevice.getProperty("bluetooth.connected.page"));
		
		discoveryAgent = device.getDiscoveryAgent();
	}
	
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		//System.out.println("Device discovered!");
		try {
			String address = btDevice.getBluetoothAddress();
			for (Device rdev: devices) { // Check of Device al in de lijst staat, maar er nog niet naar gedistributeerd is
				if (address.equals(rdev.getBluetoothAddress())) {
					if (!rdev.hasBeenDistributedTo() && !rdev.isBeingDistributedTo()) {
						if (rdev.getObexPushAvailable()) {
							rdev.setBeingDistributedTo(true);
							startNewDistributor(rdev);
						}
						else startServiceSearch(btDevice, rdev);
					}
					rdev.incDiscoveryCount();
					//System.out.println("DiscoveryCount: " + rdev.getDiscoveryCount());
					return;
				}
			}
			for (Device ddev: distributedTo) { // Device staat niet in de lijst, maar heeft wel een distributie
				if (address.equals(ddev.getBluetoothAddress())) {
					ddev.setDiscovered(inquiryCount);
					ddev.resetDiscoveryCount();
					devices.add(ddev);
					discoveryRelay.setDevices(devices);
					return;
				}
			}
			Device dev = new Device(btDevice.getBluetoothAddress(), btDevice.getFriendlyName(true), inquiryCount);
			devices.add(dev);
			discoveryRelay.setDevices(devices);
			
			startServiceSearch(btDevice, dev);
			
			System.out.println(dev.getBluetoothAddress() + " " + dev.getFriendlyName());
		} 
		catch (BluetoothStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		}
	}
	
	public void inquiryCompleted(int discType) {
		String type = "...";
		switch (discType) {
		case INQUIRY_COMPLETED:
			type = "Inquiry Completed...";
			inquiryCount++;
			break;
		case INQUIRY_ERROR:
			type = "Inquiry Error...";
			break;
		case INQUIRY_TERMINATED:
			type = "Inquiry Terminated...";
		}
		inquiryCompleted = true;
		System.out.println(type);
	}
	
	public void serviceSearchCompleted(int transID, int respCode) {
		String code = "...";
		switch (respCode) {
		case SERVICE_SEARCH_COMPLETED:
			code = "Service Search Completed";
			break;
		case SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
			code = "Service Search Device Not Reachable";
			break;
		case SERVICE_SEARCH_ERROR: 
			code = "Service Search Error";
			break;
		case SERVICE_SEARCH_NO_RECORDS:
			code = "Service Search No Records";
			break;
		case SERVICE_SEARCH_TERMINATED:
			code = "Sevice Search Terminated";
		}
		activeServiceSearchCount--;
		purgeServiceSearchQueue(); // Je komt alleen in de queue als er al searches actief zijn
		System.out.println(code + ": " + transID);
	}

	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		for (Device dev: devices) {
			if (dev.getTransactionID() == transID) {
				dev.setConnectionURL(servRecord[0].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
				dev.setObexPushAvailable(true);
				dev.setBeingDistributedTo(true);
				/*if (dev.getFriendlyName().equals("4C0N")) */startNewDistributor(dev);
			}
		}
		discoveryRelay.setDevices(devices);
		System.out.println("ServicesDiscovered: " + transID);
		System.out.println("Number of services: " + servRecord.length);
		System.out.println("Connection String: " + servRecord[0].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
	}
	
	private void cleanupDevices() { 
		//System.out.println("Cleanup!");
		for (int i = 0; i < devices.size(); i++) {
			Device dev = devices.get(i);
			System.out.println("" + (inquiryCount - dev.getDiscovered() - dev.getDiscoveryCount()));
			if (inquiryCount - dev.getDiscovered() - dev.getDiscoveryCount() >= 1) {
				devices.remove(dev);
				System.out.println("Device removed: " + dev.getFriendlyName() + " " + (inquiryCount - dev.getDiscovered() - dev.getDiscoveryCount()));
			}
		}
		//System.out.println("Cleanup done...");
	}
	
	private void startNewDistributor(Device dev) {
		if (distributable != null) {
			if (activeDistributorCount < maxConnections) { // Check of er een connectie geopend kan worden
				System.out.println("Starting new distributor...");
				Thread t = new Thread(new Distributor(distributable, dev));
				distributors[activeDistributorCount] = t;
				dev.setDistributor(activeDistributorCount);
				t.start();
				activeDistributorCount++;
			}
			else distributionQueue.add(dev); // Plaats in queue
		}
		else dev.setBeingDistributedTo(false);
	}
	
	private void cleanupDistributors() {
		if (activeDistributorCount > 0) {
			for (int i = 0; i < distributors.length; i++) {
				if (distributors[i] != null) {
					if (distributors[i].getState().toString().equals("TERMINATED")) {
						for (Device dev: devices) {
							if (i == dev.getDistributor()) {
								if (!dev.hasBeenDistributedTo()) {
									if (!dev.isDeclined()) {
										//System.out.println("Into the queue...");
										dev.incFailureCount();
										if (dev.getFailureCount() < 2) {
											dev.setBeingDistributedTo(true);
											distributionQueue.add(dev);
										}
										else dev.setDeclined(true);
									}
								}
								else {
									dev.setBeingDistributedTo(false);
									distributedTo.add(dev);
								}
							}
						}
						distributors[i] = null;
						activeDistributorCount--;
					}
				}
			}
		}
		System.out.println("Currently " + activeDistributorCount + " distributors active...");
	}
	
	private void purgeDistributorQueue() {
		//System.out.println("Start purging distributor queue...");
		if (activeDistributorCount < maxConnections) {
			for (int i = 0; i < distributors.length; i++) {
				if (distributors[i] == null) {
					//System.out.println("Distributor: " + i + " is null...");
					Device dev = distributionQueue.poll();
					if (dev != null) {
						dev.setBeingDistributedTo(true);
						startNewDistributor(dev);
					}
				}
			}
		}
	}
	
	private void purgeServiceSearchQueue() {
		if (activeServiceSearchCount < maxServiceSearches) {
			for (int i = 0; i < maxServiceSearches - activeServiceSearchCount; i++) {
				RemoteDevice rdev = serviceSearchQueue.poll();
				if (rdev != null) {
					try {
						int transID = discoveryAgent.searchServices(null, 
								new UUID[] {OBEX_PUSH_PROFILE},	rdev, this);
						activeServiceSearchCount++;
						for (Device dev: devices) if (dev.getBluetoothAddress().equals(rdev.getBluetoothAddress())) dev.setTransactionID(transID);
					} 
					catch (BluetoothStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else return;
			}
		}
	}
	
	private void startServiceSearch(RemoteDevice btDevice, Device dev) throws BluetoothStateException {
		if (activeServiceSearchCount < maxServiceSearches) {
			activeServiceSearchCount++;
			int transID = discoveryAgent.searchServices(null, 
					new UUID[] {OBEX_PUSH_PROFILE},	btDevice, this);
			dev.setTransactionID(transID);
		}
		else serviceSearchQueue.add(btDevice);
	}

	public void run() {
		//inquiryCount++;
		try {
			while(true) {
				inquiryCompleted = false;
				distributable = discoveryRelay.getDistributable();
				
				//System.out.println("Starting Inquiry... Count: " + inquiryCount);
				discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this); // Start Inquiry
				
				while (!inquiryCompleted) Thread.sleep(100); // Wait untill Inquiry is over
				
				cleanupDevices();
				
				discoveryRelay.setInquiryCount(inquiryCount);
				discoveryRelay.setDevices(devices);
				
				cleanupDistributors();
				purgeDistributorQueue(); 
			}
		} 
		catch (BluetoothStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InterruptedException e) {
			if (!inquiryCompleted) {
				System.out.println("Inquirer interrupted!");
				discoveryAgent.cancelInquiry(this);
			}
			return;
		} 		
	}
}
