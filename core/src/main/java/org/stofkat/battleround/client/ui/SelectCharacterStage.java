package org.stofkat.battleround.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.stofkat.battleround.core.Player;
import org.stofkat.battleround.core.ServerInterface;
import org.stofkat.battleround.shared.dispatch.actions.SelectCharacterAction;
import org.stofkat.battleround.shared.dispatch.results.SelectCharacterResult.ResultValue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SelectCharacterStage extends Stage {
	private final ServerInterface serverInterface;
	private Table table;
	private Table radioButtonTable;
	private Label titleLabel;
	private ButtonGroup selectCharacterRadioButtons;
	
	private List<SelectableCharacter> selectableCharacters;
	
	public SelectCharacterStage(ServerInterface serverInterface) {
		super(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		this.serverInterface = serverInterface;
	}
	
	public void displayError(String errorMessage, Skin skin) {
		if (table != null) {
			titleLabel.setText(errorMessage);
		} else {
			Label errorLabel = new Label(errorMessage, skin);
			
			Table tableForErrorLabel = new Table(skin);
			tableForErrorLabel.setFillParent(true);
			tableForErrorLabel.add(errorLabel).center();
			
			addActor(tableForErrorLabel);
		}
	}
	
	public void init(HashMap<Long, Player> players, Skin skin) {
		selectableCharacters = new ArrayList<SelectableCharacter>();
		
		table = new Table(skin);
		table.setFillParent(true);
		table.top();
		
		radioButtonTable = new Table(skin);
		radioButtonTable.top();
		
		ScrollPane scrollPane = new ScrollPane(radioButtonTable, skin);
//		scrollPane.setFillParent(true);
		scrollPane.setWidth((float) Gdx.graphics.getWidth());
		scrollPane.setScrollingDisabled(true, false);
		
		selectCharacterRadioButtons = new ButtonGroup();
		
		// Make a radiobutton for every player.
		int numbersOfCharactersAlreadyChosen = 0;
		for (Player player : players.values()) {
			
			CheckBox radioButtonForPlayer = new CheckBox(player.getCharacterName(), skin);
			
			Label label;
			String playerName = player.getPlayerName();
			if (playerName != null) {
				radioButtonForPlayer.setDisabled(true);
				radioButtonForPlayer.setChecked(false);
				
				numbersOfCharactersAlreadyChosen++;
			} else {
				selectCharacterRadioButtons.add(radioButtonForPlayer);
				playerName = "-";
			}
			
			label = new Label(playerName, skin);
			
			SelectableCharacter selectableCharacter = new SelectableCharacter(player.getId(), label, radioButtonForPlayer);
			selectableCharacters.add(selectableCharacter);
			
			// Put the label and button in the table.
			radioButtonTable.add(radioButtonForPlayer).width(200).height(50);
			radioButtonTable.add(label).pad(10f);
			radioButtonTable.row();
		}
		
		titleLabel = new Label("Pick a character.", skin);
		titleLabel.setColor(Color.BLACK);
		
		if (numbersOfCharactersAlreadyChosen == players.size()) {
			titleLabel.setText("The game is already full, Sorry!");
		}
		
		table.add(titleLabel).pad(10f);
		table.row();
		table.add(scrollPane);
		table.row();
		
		final TextButton button = new TextButton("Submit", skin);
		button.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (actor.equals(button)) {
					Button selectedButton = selectCharacterRadioButtons.getChecked();
					
					if (selectedButton != null) {
						for (SelectableCharacter selectableCharacter : selectableCharacters) {
							if (selectedButton.equals(selectableCharacter.getButton())) {
								SelectCharacterAction action = new SelectCharacterAction(serverInterface.getClientId(), selectableCharacter.getCharacterId(), serverInterface.getLastUpdateNumber());
								serverInterface.executeServerAction(action);
							}
						}
					}
				}
			}
		});
		
		table.add(button).height(50).width(100).padTop(10f);
		
		addActor(table);
	}
	
	public void setCharacterSelectedResult(ResultValue result) {
		titleLabel.setText(result.toString());
	}
	
	public void updateAlreadySelectedCharacters(HashMap<Long, String> characterToPlayerMapping) {
		for (SelectableCharacter selectableCharacter : selectableCharacters) {
			if (characterToPlayerMapping.containsKey(selectableCharacter.getCharacterId())) {
				selectableCharacter.getLabel().setText(characterToPlayerMapping.get(selectableCharacter.getCharacterId()));
				selectableCharacter.getButton().setDisabled(true);
			}
		}
	}
	
	private class SelectableCharacter {
		private long characterId;
		private Label label;
		private Button button;
		
		public SelectableCharacter(long characterId, Label label, Button button) {
			this.characterId = characterId;
			this.label = label;
			this.button = button;
		}

		public long getCharacterId() {
			return characterId;
		}

		public Label getLabel() {
			return label;
		}

		public Button getButton() {
			return button;
		}
	}
}
