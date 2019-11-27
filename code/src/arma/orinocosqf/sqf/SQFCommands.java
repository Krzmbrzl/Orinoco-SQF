package arma.orinocosqf.sqf;

import arma.orinocosqf.IdTransformer;
import arma.orinocosqf.exceptions.UnknownIdException;
import arma.orinocosqf.util.CommandSet;
import arma.orinocosqf.util.ResourceHelper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holds all {@link SQFCommand} instances
 *
 * @author K
 * @since 5/8/19
 */
public class SQFCommands extends CommandSet<SQFCommand> implements IdTransformer<String> {

	public static final SQFCommands instance = new SQFCommands();

	public static final String sqfCommandsBaseDirectory = "arma-commands-syntax/";
	public static final String sqfCommandsXMLDirectory = sqfCommandsBaseDirectory + "command_xml/";
	public static final String sqfOperatorsDirectory = sqfCommandsBaseDirectory + "operators/";
	public static final String sqfOperatorsListPath = sqfOperatorsDirectory + "operators.list";

	private Operators operators;

	private SQFCommands() {
		super(new ArrayList<>(3000));

		String[] resourceNames;
		try {
			resourceNames = ResourceHelper.getResourceListing(getClass(), sqfCommandsXMLDirectory);
		} catch (URISyntaxException | IOException e1) {
			throw new RuntimeException("Failed to load SQF command syntax resources");
		}

		for (String currentResourceName : resourceNames) {
			if (!currentResourceName.endsWith(".xml")) {
				continue;
			}

			try {
				SQFCommand command = SQFCommandSyntaxXMLLoader.importFromStream(
						getClass().getClassLoader().getResourceAsStream(sqfCommandsXMLDirectory + currentResourceName), false);
				commands.add(command);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		InputStream in = getClass().getClassLoader().getResourceAsStream(sqfOperatorsListPath);

		if (in == null) {
			throw new RuntimeException("Unable to load operator list");
		}

		Scanner commandsListScan = new Scanner(in);


		Pattern p = Pattern.compile("`(.+?)`([^\n]+)");

		while (commandsListScan.hasNextLine()) {
			String line = commandsListScan.nextLine().trim();
			if (line.length() == 0) {
				continue;
			}
			if (line.charAt(0) == '#') {
				continue;
			}

			Matcher m = p.matcher(line);
			while (m.find()) {
				String commandFileName = m.group(1) + ".xml";
				try {
					SQFCommand operator = SQFCommandSyntaxXMLLoader.importFromStream(
							getClass().getClassLoader().getResourceAsStream(sqfOperatorsDirectory + commandFileName), false);
					commands.add(operator);
				} catch (Exception e) {
					commandsListScan.close();
					throw new IllegalStateException(e);
				}
			}
		}

		commandsListScan.close();

		((ArrayList<SQFCommand>) this.commands).trimToSize();
		this.commands.sort(COMPARATOR);

	}

	@NotNull
	@Override
	public String fromId(int id) throws UnknownIdException {
		SQFCommand c = this.commands.get(id);
		if (c == null) {
			throw new UnknownIdException(id + "");
		}
		return c.getName();
	}

	@Override
	public int toId(@NotNull String value) throws UnknownIdException {
		int id = getId(value);
		if (id < 0) {
			throw new UnknownIdException(value);
		}
		return id;
	}

	@NotNull
	public static Operators ops() {
		if (instance.operators == null) {
			instance.operators = new Operators();
		}
		return instance.operators;
	}

	public static class Operators {
		private SQFCommand getCmd(String s) {
			return instance.commands.get(instance.toId(s));
		}

		public final SQFCommand EQEQ = getCmd("==");
		public final SQFCommand NE = getCmd("!=");
		public final SQFCommand GTGT = getCmd(">>");
		public final SQFCommand LE = getCmd("<=");
		public final SQFCommand GE = getCmd(">=");
		public final SQFCommand AMPAMP = getCmd("&&");
		public final SQFCommand BARBAR = getCmd("||");
		public final SQFCommand ASTERISK = getCmd("*");
		public final SQFCommand EQ = getCmd("=");
		public final SQFCommand PERC = getCmd("%");
		public final SQFCommand PLUS = getCmd("+");
		public final SQFCommand MINUS = getCmd("-");
		public final SQFCommand FSLASH = getCmd("/");
		public final SQFCommand CARET = getCmd("^");
		public final SQFCommand HASH = getCmd("#");
		public final SQFCommand LT = getCmd("<");
		public final SQFCommand GT = getCmd(">");
		public final SQFCommand EXCL = getCmd("!");
		public final SQFCommand LPAREN = getCmd("(");
		public final SQFCommand RPAREN = getCmd(")");
		public final SQFCommand L_CURLY_BRACE = getCmd("{");
		public final SQFCommand R_CURLY_BRACE = getCmd("}");
		public final SQFCommand L_SQ_BRACKET = getCmd("[");
		public final SQFCommand R_SQ_BRACKET = getCmd("]");
		public final SQFCommand COMMA = getCmd(",");
		public final SQFCommand SEMICOLON = getCmd(";");
		public final SQFCommand QUEST = getCmd("?");
		public final SQFCommand COLON = getCmd(":");
	}


}
