package arma.orinocosqf.syntax;

import arma.orinocosqf.util.CaseInsensitiveHashSet;
import arma.orinocosqf.util.TextHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Used for caching and loading SQF command syntax's ({@link CommandSyntax}) from their xml files.
 *
 * @author Kayler
 * @since 11/13/2017
 */
public class CommandDescriptorPool {
	/**
	 * Cache of commands
	 */
	private final CaseInsensitiveHashSet<CommandDescriptor> cache = new CaseInsensitiveHashSet<>();


	public CommandDescriptorPool() {
		String[] frequent = {
				"addAction",
				"and",
				"append",
				"blufor",
				"breakOut",
				"breakTo",
				"call",
				"case",
				"count",
				"damage",
				"direction",
				"do",
				"driver",
				"east",
				"else",
				"exitWith",
				"false",
				"findDisplay",
				"for",
				"forEach",
				"format",
				"from",
				"getDir",
				"group",
				"hint",
				"if",
				"in",
				"isNil",
				"isNull",
				"localize",
				"nil",
				"not",
				"opfor",
				"or",
				"player",
				"position",
				"private",
				"select",
				"set",
				"setDir",
				"setPos",
				"sleep",
				"spawn",
				"step",
				"str",
				"switch",
				"then",
				"to",
				"true",
				"typeOf",
				"uiSleep",
				"vehicle",
				"waitUntil",
				"west",
				"while",
		};
		for (String command : frequent) {
			CommandDescriptor d = CommandDescriptor.getDescriptorFromFile(command);
			if (d == null) {
				throw new IllegalStateException(command);
			}
			cache.put(d);
		}
	}

	/**
	 * This will get a {@link CommandDescriptor} instance for the given command name. Case sensitivity doesn't matter.
	 * The backend has a lazy filled cache, meaning, only when a when a command is requested will it be loaded into the cache.
	 * The cache will never be garbage collected.
	 *
	 * This method will return null when a syntax xml file doesn't exist or the XML had an error being parsed.computeIfKeyAbsent
	 * <p>
	 * This method will not get any parameter descriptions, return descriptions, or any other type of descriptions. If you wish to get these
	 * descriptions, you will need to invoke {@link SQFCommandSyntaxXMLLoader#importFromStream(CommandXMLInputStream, boolean)} directly.
	 *
	 * @param commandName the name of command (case sensitivity doesn't matter).
	 * @return the {@link CommandDescriptor} instance, or null if one couldn't be created
	 */
	@Nullable
	public CommandDescriptor get(@NotNull String commandName) {
		return cache.computeIfKeyAbsent(commandName, cn -> CommandDescriptor.getDescriptorFromFile(TextHelper.asString(cn)));
	}
}
