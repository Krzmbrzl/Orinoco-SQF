package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author K
 * @since 5/2/19
 */
public class CommandSet<C extends Command> {
	private static final Comparator<CharSequence> CHAR_SEQUENCE_COMPARATOR = (left, right) -> {
		int len1 = left.length();
		int len2 = right.length();
		int lim = Math.min(left.length(), right.length());
		for (int k = 0; k < lim; ++k) {
			final char lc = ASCIITextHelper.toLowerCase(left.charAt(k));
			final char rc = ASCIITextHelper.toLowerCase(right.charAt(k));
			if (lc != rc) {
				return lc - rc;
			}
		}
		return len1 - len2;
	};
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
		return CHAR_SEQUENCE_COMPARATOR.compare(c1, c2);
	};

	private static final Comparator<Command> COMPARATOR = (command, other) -> {
		String c1 = command.getName();
		String c2 = other.getName();
		return CHAR_SEQUENCE_COMPARATOR.compare(c1, c2);
	};


	private final List<C> commands;


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

	public int getId(@NotNull Command command) {
		return Collections.binarySearch(commands, command, COMPARATOR);
	}
}
