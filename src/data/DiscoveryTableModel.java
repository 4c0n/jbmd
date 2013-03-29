package data;

import javax.swing.table.TableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.util.Vector;

public class DiscoveryTableModel implements TableModel {
	private Vector<Device> devices;
	private Vector<TableModelListener> listeners;
	private DiscoveryRelay discoveryRelay;
	private int interval;
	private Thread updater;
	private ModelUpdater mu;

	public DiscoveryTableModel(DiscoveryRelay discoveryRelay, int interval) {
		this.discoveryRelay = discoveryRelay;
		this.interval = interval;
		devices = new Vector<Device>();
		listeners = new Vector<TableModelListener>();
	}
	
	public void start() {
		mu = new ModelUpdater(this, interval);
		updater = new Thread(mu);
		updater.start();
	}
	
	public void stop() {
		updater.interrupt();
	}

	public void update() {
		devices = discoveryRelay.getDevices();
		TableModelEvent tme = new TableModelEvent(this);
		for(TableModelListener listener: listeners) listener.tableChanged(tme);
	}

	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	public int getColumnCount() {
		return 5;
	}

	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "BT Address";
		case 1:
			return "Friendly Name";
		case 2:
			return "OBEX";
		case 3:
			return "Distributed to";
		case 4:
			return "Declined";
		}
		return null;
	}

	public int getRowCount() {
		return devices.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Device dev = devices.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return dev.getBluetoothAddress();
		case 1: 
			return dev.getFriendlyName();
		case 2:
			return dev.getObexPushAvailable();
		case 3:
			return dev.hasBeenDistributedTo();
		case 4:
			return dev.isDeclined();
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	public Class<?> getColumnClass(int i) {
		switch (i) {
		case 0:
			return String.class;
		case 1:
			return String.class;
		case 2:
			return Boolean.class;
		case 3:
			return Boolean.class;
		case 4:
			return Boolean.class;
		}
		return String.class;
	}
}

class ModelUpdater implements Runnable {
	private DiscoveryTableModel dtm;
	private int interval;
	
	public ModelUpdater(DiscoveryTableModel dtm, int interval) {
		this.dtm = dtm;
		this.interval = interval;
	}
	
	public void run() {
		while(true) {
			dtm.update();
			try {
				Thread.sleep(interval);
			} 
			catch (InterruptedException e) {
				return;
			}
		}
	}
}
