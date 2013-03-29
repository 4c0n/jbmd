package data;

import java.util.Vector;

public class DiscoveryRelay {
	private Vector<Device> devices;
	private int inquiryCount = 0;
	private Distributable distributable;

	public DiscoveryRelay() {
		devices = new Vector<Device>();
	}
	
	public synchronized void setDevices(Vector<Device> devices) {
		this.devices = devices;
		notify();
	}
	
	public synchronized Vector<Device> getDevices() {
		try {
			wait();
		} 
		catch (InterruptedException e) {
			return devices;
		}
		return devices;
	}
	
	public synchronized void setInquiryCount(int inquiryCount) {
		this.inquiryCount = inquiryCount;
	}
	
	public synchronized int getInquiryCount() {
		return inquiryCount;
	}

	public synchronized Distributable getDistributable() {
		return distributable;
	}
	
	public synchronized void setDistributable(Distributable distributable) {
		this.distributable = distributable;
	}
}