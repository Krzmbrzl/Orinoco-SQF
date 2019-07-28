package arma.orinocosqf.util;

import arma.orinocosqf.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static arma.orinocosqf.util.ASCIITextHelper.CHARSEQUENCE_CASE_INSENSITIVE_COMPARATOR;

/**
 * @author K
 * @since 5/2/19
 */
public class CommandSet<C extends Command> {

	private static final Comparator<Object> DYNAMIC_COMPARATOR = (left, right) -> {
		CharSequence c1, c2;
		if (left instanceof CharSequence) {
			c1 = (CharSequence) left;
		} else {
			c1 = ((Command) left).getName();
		}
		if (right instanceof CharSequence) {
			c2 = (CharSequence) right;
		} else {
			c2 = ((Command) right).getName();
		}
		return CHARSEQUENCE_CASE_INSENSITIVE_COMPARATOR.compare(c1, c2);
	};

	public static final Comparator<Command> COMPARATOR = (command, other) -> {
		String c1 = command.getName();
		String c2 = other.getName();
		return CHARSEQUENCE_CASE_INSENSITIVE_COMPARATOR.compare(c1, c2);
	};


	protected final List<C> commands;


	public CommandSet(@NotNull List<C> commands) {
		this.commands = commands;
		commands.sort(COMPARATOR);
	}

	public int getId(@NotNull CharSequence charSequence) {
		return Collections.binarySearch(commands, charSequence, DYNAMIC_COMPARATOR);
	}

	public int getId(@NotNull String commandName) {
		return Collections.binarySearch(commands, commandName, DYNAMIC_COMPARATOR);
	}

	@Nullable
	public C getCommandInstance(@NotNull String commandName) {
		int i = Collections.binarySearch(commands, commandName, DYNAMIC_COMPARATOR);
		if (i < 0) {
			return null;
		}
		return commands.get(i);
	}

	public int getId(@NotNull Command command) {
		return Collections.binarySearch(commands, command, COMPARATOR);
	}

	@Nullable
	public String getCommandNameById(int id) {
		C c = commands.get(id);
		if (c == null) {
			return null;
		}
		return c.getName();
	}

	public int count() {
		return commands.size();
	}

	@NotNull
	public Iterable<C> iterate() {
		return commands;
	}
}
