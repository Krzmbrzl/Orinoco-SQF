package arma.orinocosqf.sqf;

import arma.orinocosqf.IdTransformer;
import arma.orinocosqf.exceptions.UnknownIdException;
import arma.orinocosqf.util.CommandSet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Holds all {@link SQFCommand} instances
 * @author K
 * @since 5/8/19
 */
public class SQFCommands extends CommandSet<SQFCommand> implements IdTransformer<String> {

	public static final SQFCommands instance = new SQFCommands();

	private SQFCommands() {
		super(new ArrayList<>(3000));

		Scanner commandsListScan = new Scanner(System.in);//todo
		while (commandsListScan.hasNextLine()) {
			String line = commandsListScan.nextLine().trim();
			if (line.length() == 0) {
				continue;
			}
			if (line.charAt(0) == '#') {
				continue;
			}
			SQFCommand d = SQFCommand.getCommandFromFile(line);
			if (d == null) {
				throw new IllegalStateException(line);
			}
			this.commands.add(d);
		}
		((ArrayList) instance.commands).trimToSize();
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
