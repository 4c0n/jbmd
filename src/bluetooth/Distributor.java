package bluetooth;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;

import data.Device;
import data.Distributable;

public class Distributor implements Runnable {
	private HeaderSet headerset;
	private Operation operation;
	private ClientSession session;
	private Device device = null;
	private DataOutputStream dos; 
	private Distributable distributable;
	
	public Distributor(Distributable distributable, Device device) {
		this.distributable = distributable;
		this.device = device;
	}
	
	public void run() {
		try {
			while (device.getConnectionURL() == null) Thread.yield();
			System.out.println("Get client session...");
			System.out.println(device.getConnectionURL());
			session = (ClientSession) Connector.open(device.getConnectionURL());
			HeaderSet connectionReply = session.connect(null);
			System.out.println("Connect session...");
			if (connectionReply.getResponseCode() == ResponseCodes.OBEX_HTTP_OK) {
				System.out.println("Session OK...");
				headerset = session.createHeaderSet();
				headerset.setHeader(HeaderSet.NAME, distributable.getName());
				headerset.setHeader(HeaderSet.TYPE, "binary");
				
				operation = session.put(headerset);
				
				dos = operation.openDataOutputStream();
				
				byte[] data = distributable.getData();
				for (byte b: data) dos.write(b);
				dos.close();
				System.out.println("Data sent closing session...");
				
				System.out.println("ResponseCode: " + operation.getResponseCode());
				
				if (operation.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
					device.setDeclined(true);
					System.out.println("Probably the user declined...");
				}
				else device.setHasBeenDistributedTo(true);
				
				operation.close();
				
				session.disconnect(null);
				session.close();				
			}
			else {
				System.out.println("We've got some issues here...");
			}
		} 
		catch (IOException e) {
			System.out.println("e: " + e.getMessage());
			try {
				if (operation != null) {
					if (operation.getResponseCode() == ResponseCodes.OBEX_HTTP_BAD_REQUEST) {
						if (device != null) device.setDeclined(true);
						System.out.println("Probably the user declined...");
					}
				}
				if (dos != null) dos.close();
				if (operation != null) operation.close();
				if (session != null) {
					session.disconnect(null);
					session.close();
				}
			} 
			catch (IOException e1) {
				System.out.println("e1: " + e1.getMessage());
				return;
			}
			return;
		} 
	}
}
