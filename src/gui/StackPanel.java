package gui;

import javax.bluetooth.LocalDevice;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.intel.bluetooth.BlueCoveLocalDeviceProperties;

@SuppressWarnings("serial")
public class StackPanel extends JPanel {
	private JLabel lblName, lblNumDevices, lblOpenConn, lblConnTimeOut, lblInqDuration;
	private JLabel lblOBEX_MTU, lblOBEXtimeOut;
	private JTextField txtConnTimeOut, txtInqDuration, txtOBEX_MTU, txtOBEXtimeOut;
	
	public StackPanel() {
		super();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		lblName = new JLabel("Stack Name: " + LocalDevice.getProperty(
				BlueCoveLocalDeviceProperties.LOCAL_DEVICE_PROPERTY_STACK));
		
		String num = LocalDevice.getProperty(
				BlueCoveLocalDeviceProperties.LOCAL_DEVICE_DEVICES_LIST);
		num = num.substring(num.length() - 1, num.length());		
		
		lblNumDevices = new JLabel("Number of devices: " + (Integer.parseInt(num) + 1));
		lblOpenConn = new JLabel("Open Connections: " + LocalDevice.getProperty(
				BlueCoveLocalDeviceProperties.LOCAL_DEVICE_PROPERTY_OPEN_CONNECTIONS));
		
		add(lblName);
		add(lblNumDevices);
		add(lblOpenConn);
	}
}
