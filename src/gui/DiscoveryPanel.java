package gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import data.DiscoveryTableModel;

@SuppressWarnings("serial")
public class DiscoveryPanel extends JPanel {
	private JTable discoveryTable;
	private JScrollPane scrollPane;
	
	public DiscoveryPanel() {
		super(new BorderLayout());

		discoveryTable = new JTable();
		scrollPane = new JScrollPane(discoveryTable);
			
		add(scrollPane, BorderLayout.CENTER);
		validate();
	}

	public void setTableModel(DiscoveryTableModel dtm) {
		discoveryTable.setModel(dtm);
	}
}
