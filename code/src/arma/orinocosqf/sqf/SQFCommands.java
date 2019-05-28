package arma.orinocosqf.sqf;

import arma.orinocosqf.IdTransformer;
import arma.orinocosqf.exceptions.UnknownIdException;
import arma.orinocosqf.util.CommandSet;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author K
 * @since 5/8/19
 */
public class SQFCommands extends CommandSet<SQFCommand> implements IdTransformer<String> {

	public static final SQFCommands instance = new SQFCommands();

	static {
		ArrayList<SQFCommand> arrayList = (ArrayList<SQFCommand>) instance.commands;
		InputStream stm = SQFCommands.class.getResourceAsStream("/arma/orinocosqf/sqfcommands.list");
		Scanner scanner = new Scanner(new InputStreamReader(stm, StandardCharsets.UTF_8));
		while (scanner.hasNextLine()) {
			arrayList.add(new SQFCommand(scanner.nextLine()));
		}

		scanner.close();

		stm = SQFCommands.class.getResourceAsStream("/arma/orinocosqf/sqfcommands_operators.list");
		scanner = new Scanner(new InputStreamReader(stm, StandardCharsets.UTF_8));
		while (scanner.hasNextLine()) {
			arrayList.add(new SQFCommand(scanner.nextLine()));
		}
		scanner.close();

		arrayList.trimToSize();
		arrayList.sort(COMPARATOR);
	}

	private SQFCommands() {
		super(new ArrayList<>(2048));
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
