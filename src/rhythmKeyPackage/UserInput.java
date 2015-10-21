package rhythmKeyPackage;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
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
		// Disables focus traversal and the Tab events
		// become available to the key event listener
		textField.setFocusTraversalKeysEnabled(false);
		textField.setFocusable(true);
		
		instructionsLabel = new JLabel("<HTML>Instructions:<br>"
				+ "First enter your username and click start.<br>"
				+ "Then type your password and click end.<br>"
				+ "You can do this as many times as you want.</HTML>");
		
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
		@Override
		public void actionPerformed(ActionEvent e) {
			if (startEndButton.getText().equals("Start")) {
				startEndButton.setText("End");
				main.storeUsername(textField.getText());
				// Test code, uncomment to see test
				/*System.out.println(textField.getText());*/
				textField.setText("");
				textField.addKeyListener(keyListener);
			}
			else {
				startEndButton.setText("Start");
				ArrayList<KeyPress> keyPressList = keyListener.getKeyPressList();
				textField.removeKeyListener(keyListener);
				/*for (KeyPress k1 : keyPressList) {
					if (k1.getKeyup() == 0) {
						for (KeyPress k2 : keyPressList) {
							if (k1.getKeyIdentifier().getKeyCode() == k2.getKeyIdentifier().getKeyCode() && k2.getKeydown() == 0) {
								keyPressList.remove(k2);
								k1.setKeyup(k2.getKeyup());
							}
						}
					}
				}*/
				//now, get rid of duplicate keypresses
				List <KeyPress> keyDownList = new ArrayList<KeyPress>();
				List <KeyPress> finalKeyPressList  = new ArrayList<KeyPress>();
				for(int i = 0; i < keyPressList.size(); i++){
					//System.out.println("keydown detected!");
					KeyPress kp1 = keyPressList.get(i);
					if(kp1.getKeyup() == 0){ //keydown data only
						keyDownList.add(kp1);
					}
					if(kp1.getKeydown() == 0){ //keyup data only
						for(int j = 0; j < keyDownList.size(); j++){ //look to pair with keydown
							//System.out.println("Looking for pair ...");
							KeyPress kp2 = keyDownList.get(j);
							if(kp1.getKeyIdentifier().getKeyCode() == (kp2.getKeyIdentifier().getKeyCode())){ //Pair found!
								//System.out.println("Pair Found!");
								kp2.setKeyup(kp1.getKeyup());
								keyDownList.remove(j);
								j = keyDownList.size();
								finalKeyPressList.add(kp2);
							}
						}
					}
				}
//				List <KeyPress> reverseFinalList = new ArrayList<KeyPress>();
//				for(int i = finalKeyPressList.size()-1; i >= 0; i--){
//					reverseFinalList.add(finalKeyPressList.get(i));
//				}
				session.setKeyStrokes(finalKeyPressList);
				main.storeSession(session);
				// Test code, uncomment to see test
				/*System.out.println("Length of list: " + keyPressList.size());
				for (KeyPress k : keyPressList) {
					System.out.print(k.getKeyIdentifier().getKeyChar());
				}
				System.out.println();*/
				textField.setText("");
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
