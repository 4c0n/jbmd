package main;

/*import javax.bluetooth.LocalDevice;

import com.intel.bluetooth.BlueCoveLocalDeviceProperties;*/

import gui.MainFrame;

public class JBMD {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MainFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);
		
		/*System.out.println("Local devices: " + LocalDevice.getProperty(BlueCoveLocalDeviceProperties.LOCAL_DEVICE_DEVICES_LIST));
		System.out.println("Local device ID: " + LocalDevice.getProperty(BlueCoveLocalDeviceProperties.LOCAL_DEVICE_PROPERTY_DEVICE_ID));
		System.out.println("Local stack: " + LocalDevice.getProperty(BlueCoveLocalDeviceProperties.LOCAL_DEVICE_PROPERTY_STACK));*/
	}
}
