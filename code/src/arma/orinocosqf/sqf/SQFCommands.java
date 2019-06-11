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

	private SQFCommands() {
		super(new ArrayList<>(3000));

		String prefix = "../arma-commands-syntax/";
		File[] files = new File(prefix + "command_xml").listFiles((file, s) -> s.endsWith(".xml"));
		if (files == null) {
			throw new NullPointerException();
		}

		for (File commandXmlFile : files) {
			try {
				SQFCommand d = SQFCommandSyntaxXMLLoader.importFromStream(new FileInputStream(commandXmlFile), false);
				commands.add(d);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		Scanner commandsListScan;
		try {
			commandsListScan = new Scanner(new File(prefix + "operators/operators.list"));
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
					SQFCommand d = SQFCommandSyntaxXMLLoader.importFromStream(new FileInputStream(prefix + "operators/" + commandFileName), false);
					commands.add(d);
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		}
		((ArrayList) this.commands).trimToSize();
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

}
