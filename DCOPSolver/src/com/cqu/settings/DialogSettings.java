package com.cqu.settings;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;

public class DialogSettings extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2649743350270457034L;
	
	private final JPanel contentPanel = new JPanel();
	
	private JSpinner spinnerDPOPCommunicationTime;
	private JSpinner spinnerCommunicationNCCC;
	private JCheckBox cbDisplayGraphFrame;
	
	private Settings settings;
	/**
	 * Create the dialog.
	 */
	public DialogSettings(final Settings settings) {
		setTitle("Settings");
		setBounds(100, 100, 314, 245);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		spinnerDPOPCommunicationTime = new JSpinner();
		spinnerDPOPCommunicationTime.setModel(new SpinnerNumberModel(0, 0, 1000, 10));
		spinnerDPOPCommunicationTime.setBounds(187, 10, 101, 22);
		contentPanel.add(spinnerDPOPCommunicationTime);
		
		JLabel lblDpop = new JLabel("DPOP类算法通信时间：");
		lblDpop.setBounds(10, 13, 167, 15);
		contentPanel.add(lblDpop);
		
		JLabel lblnccc = new JLabel("每次消息通信NCCC：");
		lblnccc.setBounds(10, 48, 167, 15);
		contentPanel.add(lblnccc);
		
		spinnerCommunicationNCCC = new JSpinner();
		spinnerCommunicationNCCC.setModel(new SpinnerNumberModel(0, 0, 1000, 10));
		spinnerCommunicationNCCC.setBounds(187, 42, 101, 22);
		contentPanel.add(spinnerCommunicationNCCC);
		
		cbDisplayGraphFrame = new JCheckBox("每次显示GraphFrame");
		cbDisplayGraphFrame.setSelected(true);
		cbDisplayGraphFrame.setBounds(6, 82, 282, 23);
		contentPanel.add(cbDisplayGraphFrame);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnOK = new JButton("OK");
				btnOK.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						DialogSettings.this.setValues();
						
						DialogSettings.this.dispose();
					}
				});
				btnOK.setActionCommand("OK");
				buttonPane.add(btnOK);
				getRootPane().setDefaultButton(btnOK);
			}
			{
				JButton btnCancel = new JButton("Cancel");
				btnCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						DialogSettings.this.dispose();
					}
				});
				btnCancel.setActionCommand("Cancel");
				buttonPane.add(btnCancel);
			}
		}
		
		this.settings=settings;
		this.initValues();
	}
	
	private void initValues()
	{
		spinnerDPOPCommunicationTime.setValue(settings.getCommunicationTimeInDPOPs());
		spinnerCommunicationNCCC.setValue(settings.getCommunicationNCCCInAdopts());
		cbDisplayGraphFrame.setSelected(settings.isDisplayGraphFrame());
	}
	
	private void setValues()
	{
		settings.setCommunicationTimeInDPOPs((Integer)spinnerDPOPCommunicationTime.getValue());
		settings.setCommunicationNCCCInAdopts((Integer)spinnerCommunicationNCCC.getValue());
		settings.setDisplayGraphFrame(cbDisplayGraphFrame.isSelected());
	}
}