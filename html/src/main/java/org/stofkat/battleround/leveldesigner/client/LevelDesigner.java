package org.stofkat.battleround.leveldesigner.client;

import java.util.ArrayList;
import java.util.List;

import org.stofkat.battleround.common.User;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LevelDesigner implements EntryPoint, LevelDesignerClientInterface {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while " + "attempting to contact the server. Please check your network " + "connection and try again.";

	private final LevelDesignerServiceAsync service = GWT.create(LevelDesignerService.class);

	private List<FocusWidget> levelRelatedWidgets = new ArrayList<FocusWidget>();

	private DialogBox fileUploadDialogBox;
	private Label lblLevelname;
	private TextBox txtbxEnterLevelname;
	private Button btnSave;
	private Label lblLoggedInAs;
	
	private User user;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		final RootLayoutPanel rootPanel = RootLayoutPanel.get();
		rootPanel.setTitle("rootPanel");

		fileUploadDialogBox = new DialogBox(true);

		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);
		rootPanel.add(dockLayoutPanel);
		rootPanel.setWidgetLeftWidth(dockLayoutPanel, 0.0, Unit.PX, 800.0, Unit.PX);
		rootPanel.setWidgetTopHeight(dockLayoutPanel, 0.0, Unit.PX, 600.0, Unit.PX);

		StackPanel stackPanel = new StackPanel();
		dockLayoutPanel.addEast(stackPanel, 9.2);

		VerticalPanel levelDetails = new VerticalPanel();
		stackPanel.add(levelDetails, "Level details", false);

		Label lblDescription = new Label("Description");
		levelDetails.add(lblDescription);

		TextBox textBox = new TextBox();
		levelDetails.add(textBox);
		textBox.setWidth("101px");

		VerticalPanel imagesPanel = new VerticalPanel();
		stackPanel.add(imagesPanel, "Images", false);
		imagesPanel.setSize("111px", "453px");

		ListBox imagesListBox = new ListBox();
		imagesPanel.add(imagesListBox);
		imagesListBox.setSize("107px", "100%");
		imagesListBox.setVisibleItemCount(5);

		HorizontalPanel addAndDeletePanel = new HorizontalPanel();
		imagesPanel.add(addAndDeletePanel);
		addAndDeletePanel.setSize("79px", "27px");

		Button btnAdd = new Button("Add");
		ClickHandler handler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				fileUploadDialogBox.show();
				fileUploadDialogBox.center();
			}
		};
		btnAdd.addClickHandler(handler);
		addAndDeletePanel.add(btnAdd);

		Button btnDel = new Button("Del");
		addAndDeletePanel.add(btnDel);

		VerticalPanel spritesPanel = new VerticalPanel();
		stackPanel.add(spritesPanel, "Sprites", false);
		spritesPanel.setSize("111px", "100%");

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		dockLayoutPanel.addNorth(horizontalPanel, 2.2);

		lblLevelname = new Label("LevelName");
		lblLevelname.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		lblLevelname.setVisible(false);
		lblLevelname.getElement().setClassName("topLabel");

		Label lblLevel = new Label("Level:");
		lblLevel.getElement().setClassName("topLabel");
		horizontalPanel.add(lblLevel);
		horizontalPanel.add(lblLevelname);

		txtbxEnterLevelname = new TextBox();
		txtbxEnterLevelname.setText("");
		txtbxEnterLevelname.getElement().setClassName("txtbxEnterLevelname");
		txtbxEnterLevelname.setFocus(true);
		horizontalPanel.add(txtbxEnterLevelname);

		btnSave = new Button("Save");
		btnSave.setVisible(true);
		horizontalPanel.add(btnSave);

		Button btnNew = new Button("New");
		horizontalPanel.add(btnNew);

		Button btnPublish = new Button("Publish");
		horizontalPanel.add(btnPublish);
		
		lblLoggedInAs = new Label("Logged in as: ");
		lblLoggedInAs.getElement().setClassName("topLabel");
		lblLoggedInAs.setVisible(false);
		horizontalPanel.add(lblLoggedInAs);

		levelRelatedWidgets.add(btnAdd);
		levelRelatedWidgets.add(btnDel);

		TabLayoutPanel detailTabsPanel = new TabLayoutPanel(1.5, Unit.EM);

		SimplePanel exampleTabPanel = new SimplePanel();
		detailTabsPanel.add(exampleTabPanel, "Example", false);
		dockLayoutPanel.addSouth(detailTabsPanel, 7.7);

		SimplePanel westPanel = new SimplePanel();
		dockLayoutPanel.addWest(westPanel, 7.7);

		TabLayoutPanel mainContentTabPanel = new TabLayoutPanel(1.5, Unit.EM);

		SimplePanel levelPreviewPanel = new SimplePanel();
		mainContentTabPanel.add(levelPreviewPanel, "Level Preview", false);
		dockLayoutPanel.add(mainContentTabPanel);
		
		disableWidgets();
		
		service.isAuthenticated(new AuthenticationCallbackHandler(this));
	}

	private void disableWidgets() {
		for (FocusWidget widget : levelRelatedWidgets) {
			widget.setEnabled(false);
		}
	}

	@Override
	public void userAuthenticated(User user) {
		this.user = user;
		
		// TODO: Validate for XSS.
		lblLoggedInAs.setText(lblLoggedInAs.getText() + user.getName());
		lblLoggedInAs.setVisible(true);
		String levelId = Window.Location.getParameter("levelId");

		if (levelId != null) {
			lblLevelname.setVisible(true);
			txtbxEnterLevelname.setVisible(false);
			btnSave.setVisible(false);
			// Go fill all panels with data of the level.
		}
	}

	@Override
	public void userFailedToAuthenticate() {
		navigateToPage("/assets/pages/login.html");
	}
	
	public native void navigateToPage(String page) /*-{
	    $wnd.location.href = page;
	}-*/;

	@Override
	public void loadLevel() {
		// TODO Auto-generated method stub
		
	}
}
