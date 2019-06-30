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

	public static final String sqfCommandsDirectory = "arma/commands/syntax/";

	private SQFCommands() {
		super(new ArrayList<>(3000));

		String commandDirPath = sqfCommandsDirectory + "command_xml/";

		String[] resourceNames;
		try {
			resourceNames = ResourceHelper.getResourceListing(getClass(), commandDirPath);
		} catch (URISyntaxException | IOException e1) {
			throw new RuntimeException("Failed to load SQF command resources");
		}


		for (String currentResourceName : resourceNames) {
			if (!currentResourceName.endsWith(".xml")) {
				continue;
			}

			try {
				SQFCommand d = SQFCommandSyntaxXMLLoader
						.importFromStream(getClass().getClassLoader().getResourceAsStream(commandDirPath + currentResourceName), false);
				commands.add(d);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}


		InputStream is = getClass().getClassLoader().getResourceAsStream(sqfCommandsDirectory + "operators/operators.list");

		if (is == null) {
			throw new IllegalStateException();
		}

		Scanner commandsListScan = new Scanner(is);
		// new File(sqfCommandsDirectory + File.separator + "operators" + File.separator + "operators.list"));


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
							getClass().getClassLoader().getResourceAsStream(sqfCommandsDirectory + "/operators/" + commandFileName), false);
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
