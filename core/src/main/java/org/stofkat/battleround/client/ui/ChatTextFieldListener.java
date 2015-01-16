package org.stofkat.battleround.client.ui;

import org.stofkat.battleround.client.ui.ChatTextField.TextFieldListener;
import org.stofkat.battleround.core.ServerInterface;
import org.stofkat.battleround.shared.dispatch.actions.ChatAction;

public class ChatTextFieldListener implements TextFieldListener {
	private ServerInterface server;
	
	public ChatTextFieldListener(ServerInterface server) {
		this.server = server;
	}

	@Override
	public void keyTyped(ChatTextField textField, char key) {
		if (key == '\r' || key == '\n') {
			textField.getOnscreenKeyboard().show(false);
			
			server.pauseTimerBecauseWereGonnaUpdate();
			server.executeServerAction(new ChatAction(server.getClientId(), server.getLastUpdateNumber(), textField.getText()));
		} else {
			int cursorPosition = textField.getCursorPosition();
			textField.setText(textField.getText() + new String(new char[] {key}));
			textField.setCursorPosition(cursorPosition + 1);
		}
	}
}
