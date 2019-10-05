package arma.orinocosqf.sqf;

import arma.orinocosqf.IdTransformer;
import arma.orinocosqf.exceptions.UnknownIdException;
import arma.orinocosqf.util.CommandSet;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

	public static final String sqfCommandsDirectory = "arma-commands-syntax";
	private Operators operators;

	private SQFCommands() {
		super(new ArrayList<>(3000));

		File[] files = new File(sqfCommandsDirectory + File.separator + "command_xml").listFiles((file, s) -> s.endsWith(".xml"));

		for (File commandXmlFile : files) {
			try {
				SQFCommand d = SQFCommandSyntaxXMLLoader.importFromStream(new FileInputStream(commandXmlFile), false);
				commands.add(d);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}


		Scanner commandsListScan = null;
		try {
			commandsListScan = new Scanner(new File(sqfCommandsDirectory + File.separator + "operators" + File.separator + "operators.list"));
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}


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
					SQFCommand d = SQFCommandSyntaxXMLLoader.importFromStream(
							new FileInputStream(sqfCommandsDirectory + File.separator + "operators" + File.separator + commandFileName),
							false
					);
					commands.add(d);
				} catch (Exception e) {
					commandsListScan.close();
					throw new IllegalStateException(e);
				}
			}
		}

		commandsListScan.close();

		((ArrayList<SQFCommand>) this.commands).trimToSize();
		this.commands.sort(COMPARATOR);
		for (SQFCommand c : iterate()) {
			if (c.isNotStrict()) {
				System.out.println("SQFCommands.SQFCommands c=" + c);
			}
		}

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
