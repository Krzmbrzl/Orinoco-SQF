package arma.orinocosqf.gui;

import arma.orinocosqf.sqf.SQFCommand;
import arma.orinocosqf.sqf.SQFCommands;
import arma.orinocosqf.util.ASCIITextHelper;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author K
 * @since 7/28/19
 */
public class SQFSyntaxVisualizer extends Application {
	private final List<CommandListViewHelper> helpers = new ArrayList<>(SQFCommands.instance.count());

	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox root = new VBox();
		ListView<CommandListViewHelper> commandsLv = new ListView<>();
		for (SQFCommand c : SQFCommands.instance.iterate()) {
			CommandListViewHelper helper = new CommandListViewHelper(c);
			helpers.add(helper);
			commandsLv.getItems().add(helper);
		}

		StackPane commandPreviewSp = new StackPane();

		commandsLv.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super CommandListViewHelper>) c -> {
			if (c.getList().isEmpty()) {
				return;
			}
			commandPreviewSp.getChildren().clear();
			commandPreviewSp.getChildren().add(new SQFCommandPreviewPane(c.getList().get(0).command));
		});


		SearchBox searchBox = new SearchBox();

		VBox vboxRight = new VBox(5, searchBox, commandsLv);
		searchBox.searchStringProperty().addListener((observable, oldValue, newValue) -> {
			commandsLv.getItems().clear();
			if (newValue.length() == 0) {
				commandsLv.getItems().addAll(helpers);
			} else {
				for (CommandListViewHelper helper : helpers) {
					if (ASCIITextHelper.containsIgnoreCase(helper.command.getCommandName(), newValue)) {
						commandsLv.getItems().addAll(helper);
					}
				}
			}
		});

		HBox contentHbox = new HBox(5, commandPreviewSp, vboxRight);
		root.getChildren().add(contentHbox);
		contentHbox.setPadding(new Insets(5));

		commandsLv.setMinWidth(120);
		HBox.setHgrow(commandsLv, Priority.SOMETIMES);
		HBox.setHgrow(commandPreviewSp, Priority.ALWAYS);
		VBox.setVgrow(contentHbox, Priority.ALWAYS);

		Scene s = new Scene(root);
		primaryStage.setScene(s);
		primaryStage.setWidth(960);
		primaryStage.setHeight(720);
		primaryStage.show();
	}

	private static class SearchBox extends StackPane {
		private final StringProperty searchProp = new SimpleStringProperty("");

		public SearchBox() {
			TextField tf = new TextField("");
			tf.setPromptText("Search");
			getChildren().add(tf);
			tf.textProperty().addListener((observable, oldValue, newValue) -> {
				searchProp.setValue(newValue.trim());
			});
		}

		@NotNull
		public StringProperty searchStringProperty() {
			return this.searchProp;
		}
	}

	private static class CommandListViewHelper {
		public final SQFCommand command;

		public CommandListViewHelper(@NotNull SQFCommand command) {
			this.command = command;
		}

		@Override
		public String toString() {
			return command.getName();
		}
	}
}
