package arma.orinocosqf.gui;

import arma.orinocosqf.sqf.SQFCommand;
import arma.orinocosqf.sqf.SQFCommandSyntax;
import arma.orinocosqf.syntax.*;
import arma.orinocosqf.type.ValueType;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 03/12/2017
 */
public class SQFCommandPreviewPane extends StackPane {
	private static final Font defaultFont = Font.font(Font.getDefault().getFamily(), 14);

	private static Label getLabel(String s, boolean bold) {
		return getLabel(s, bold, defaultFont.getSize());
	}

	private static Label getLabel(String s, boolean bold, double fontSize) {
		Label lbl = new Label(s);
		lbl.setFont(Font.font(defaultFont.getFamily(), bold ? FontWeight.BOLD : FontWeight.NORMAL, fontSize));
		lbl.setWrapText(true);
		return lbl;
	}

	public SQFCommandPreviewPane(@NotNull SQFCommand command) {
		VBox vbox = new VBox(20);

		final double bigFontSize = defaultFont.getSize() + 10;

		vbox.getChildren().add(getLabel(command.getCommandName(), true, bigFontSize));

		if (command.isDeprecated()) {
			vbox.getChildren().add(getLabel("Deprecated", true, defaultFont.getSize()));
		}

		if (command.isUncertain()) {
			vbox.getChildren().add(getLabel("Uncertain", true, defaultFont.getSize()));
		}

		VBox vboxIntroIn = new VBox(10);
		vboxIntroIn.getChildren().add(getLabel("Introduced In:", false, bigFontSize));
		BIGame game = command.getGameIntroducedIn();
		VBox vboxGameInfo = new VBox(10,
				new HBox(3, getLabel("Game:", true), getLabel(game.getFullName(), false)),
				new HBox(3, getLabel("Version:", true), getLabel(command.getGameVersion(), false))
		);
		vboxIntroIn.getChildren().add(vboxGameInfo);
		vbox.getChildren().addAll(vboxIntroIn, new Separator(Orientation.HORIZONTAL));

		for (SQFCommandSyntax syntax : command.getSyntaxList()) {
			VBox vboxSyntax = new VBox(10);
			Label lblSyntax = getLabel("Syntax", false, bigFontSize);
			vboxSyntax.getChildren().add(lblSyntax);
			vboxSyntax.getChildren().add(new SyntaxPreviewPane(command.getCommandName(), syntax));
			vbox.getChildren().add(vboxSyntax);
			vbox.getChildren().add(new Separator(Orientation.HORIZONTAL));
		}

		getChildren().add(vbox);
	}

	private static class SyntaxPreviewPane extends GridPane {

		public SyntaxPreviewPane(@NotNull String commandName, @NotNull SQFCommandSyntax syntax) {
			getColumnConstraints().add(new ColumnConstraints(110, 110, -1, Priority.NEVER, HPos.LEFT, false));
			getColumnConstraints().add(new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.LEFT, true));
			RowConstraints rowConstraints = new RowConstraints(-1, -1, Double.MAX_VALUE, Priority.ALWAYS, VPos.TOP, true);

			int row = 0;

			setVgap(10);

			//syntax string
			HBox hboxSyntaxString = new HBox(5);
			if (syntax.getLeftParam() != null) {
				StringBuilder sb = new StringBuilder();
				getParameterString(sb, syntax.getLeftParam());
				hboxSyntaxString.getChildren().add(getLabel(sb.toString(), false));
			}

			hboxSyntaxString.getChildren().add(getLabel(commandName, true));

			if (syntax.getRightParam() != null) {
				StringBuilder sb = new StringBuilder();
				getParameterString(sb, syntax.getRightParam());
				hboxSyntaxString.getChildren().add(getLabel(sb.toString(), false));
			}

			addRow(row++, getLabel("Syntax:", true), hboxSyntaxString);


			//parameters
			VBox vboxParameters = new VBox(10);
			for (Param p : syntax.getAllParams()) {
				vboxParameters.getChildren().add(getParameterDescriptionNode(p));
			}
			for (ArrayParam ar : syntax.getAllArrayParams()) {
				vboxParameters.getChildren().add(getParameterDescriptionNode(ar));
			}
			if (vboxParameters.getChildren().size() > 0) {
				addRow(row++, getLabel("Parameters:", true), vboxParameters);
			}

			//return value
			addRow(row++, getLabel("Return Value:", true), getReturnValueDisplayNode(syntax));


			for (int i = 0; i < row; i++) {
				getRowConstraints().add(rowConstraints);
			}


		}

		private void getParameterString(@NotNull StringBuilder sb, @NotNull Param p) {
			if (p instanceof ArrayParam) {
				ArrayParam arrayParam = (ArrayParam) p;

				sb.append("[");
				int i = 0;
				for (Param innerParam : arrayParam.getParams()) {
					getParameterString(sb, innerParam);
					if (i != arrayParam.getValueHolders().size() - 1) {
						sb.append(", ");
					}
					i++;
				}
				if (arrayParam.hasUnboundedParams()) {
					sb.append(" ...");
				}
				sb.append("]");
			} else {
				sb.append(p.getName());
			}
		}

		private Node getParameterDescriptionNode(@NotNull Param p) {
			VBox vboxParam = new VBox(5);
			StringBuilder sb = new StringBuilder(100);
			sb.append(p.getName());
			if (p.isOptional()) {
				sb.append(" (optional) ");
			}
			sb.append(": ");

			sb.append(p.getType());
			appendPolymorphicTypes(sb, p);

			if (p.getDescription().length() > 0) {
				sb.append(" - ");
				sb.append(p.getDescription());
			}
			vboxParam.getChildren().add(getLabel(sb.toString(), false));

			if (p.getLiterals().size() > 0) {
				vboxParam.getChildren().add(getLabel("~~Literals:" + p.getLiterals().toString(), false));
			}

//			if (p instanceof ArrayParam) {
//				vboxParam.getChildren().add(getLabel(getArrayDataValueDisplayText((ArrayParam) p, new StringBuilder(100)), false));
//			}
			return vboxParam;
		}

		private Node getReturnValueDisplayNode(@NotNull SQFCommandSyntax s) {
			VBox vbox = new VBox(5);
			vbox.setPadding(new Insets(0, 0, 0, 3));
			StringBuilder sb = new StringBuilder(30);
			ReturnValueHolder returnValue = s.getReturnValue();

			sb.append(returnValue.getType());
			appendPolymorphicTypes(sb, returnValue);

			if (returnValue.getDescription().length() > 0) {
				sb.append(" - ");
				sb.append(returnValue.getDescription());
			}
			vbox.getChildren().add(getLabel(sb.toString(), false));
			if (s.getReturnValue() instanceof ArrayReturnValueHolder) {
				StringBuilder retSb = new StringBuilder(100);
				getArrayDataValueDisplayText((ArrayReturnValueHolder) s.getReturnValue(), retSb);
				vbox.getChildren().add(getLabel(retSb.toString(), false));
			}

			if (returnValue.getLiterals().size() > 0) {
				vbox.getChildren().add(getLabel("~~Literals:" + returnValue.getLiterals().toString(), false));
			}

			return vbox;
		}

		private void appendPolymorphicTypes(@NotNull StringBuilder sb, @NotNull ValueHolder val) {
			int alternatesLeft = val.getType().getPolymorphicTypes().size();
			for (ValueType alternate : val.getType().getPolymorphicTypes()) {
				if (alternatesLeft >= 1) {
					sb.append(", ");
				}
				sb.append(alternate);
				alternatesLeft--;
			}
		}

		public static void getArrayDataValueDisplayText(@NotNull ArrayValueHolder arrayDataValue, @NotNull StringBuilder sb) {
			ArrayValueHolder.getArrayDataValueDisplayText(arrayDataValue, sb);
		}

	}
}
