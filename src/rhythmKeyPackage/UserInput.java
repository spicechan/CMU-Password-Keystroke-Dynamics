package rhythmKeyPackage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class UserInput {
	
	private Main main;
	private Session session;
	private JFrame frame;
	private KeyTimeListener keyListener;
	private JButton startEndButton;
	private JTextField textField;
	private JLabel instructionsLabel;

	public UserInput(Main main) {
		this.main = main;
		session = new Session();
		keyListener = new KeyTimeListener();
		StartEndAction action = new StartEndAction();
		startEndButton = new JButton(action);
		startEndButton.setText("Start");
		startEndButton.setEnabled(true);
		textField = new JTextField(20);
		instructionsLabel = new JLabel("Instructions:\n"
				+ "First enter your username and click start.\n"
				+ "Then type your password and click end.\n"
				+ "You can do this as many times as you want.");
		
		// frame setup
		frame = new JFrame("Keystroke Dynamics Typing Terminal");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(instructionsLabel, BorderLayout.NORTH);
		frame.getContentPane().add(textField, BorderLayout.WEST);
		frame.getContentPane().add(startEndButton, BorderLayout.CENTER);
		frame.pack();
	}
	
	public void start() {
		frame.setVisible(true);
	}
	
	private class StartEndAction implements Action {
		public void actionPerformed(ActionEvent e) {
			if (startEndButton.getText().equals("Start")) {
				startEndButton.setText("End");
				main.storeUsername(textField.getText());
				textField.setText("");
			}
			else {
				startEndButton.setText("Start");
				session.setKeyStrokes(keyListener.getKeyPressList());
				main.storeSession(session);
			}
		}

		@Override
		public Object getValue(String key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void putValue(String key, Object value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setEnabled(boolean b) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void addPropertyChangeListener(
				PropertyChangeListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removePropertyChangeListener(
				PropertyChangeListener listener) {
			// TODO Auto-generated method stub
			
		}
	}
}
