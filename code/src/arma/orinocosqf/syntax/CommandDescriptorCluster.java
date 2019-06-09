package arma.orinocosqf.syntax;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Different from {@link CommandDescriptorPool} in that this class contains existing {@link SQFCommandDescriptor} instances whereas {@link
 * CommandDescriptorPool} can create them on demand from file.
 *
 * @author Kayler
 * @since 11/14/2017
 */
public class CommandDescriptorCluster {
	private final Map<String, SQFCommandDescriptor> map = new HashMap<>();

	public CommandDescriptorCluster(@NotNull SQFCommandDescriptor... descriptors) {
		for (SQFCommandDescriptor cd : descriptors) {
			map.put(cd.getCommandName().toLowerCase(), cd);
		}
	}


	/**
	 * @return the {@link SQFCommandDescriptor} for the given command name (case-insensitive), or null if couldn't be found or doesn't exist in
	 * cluster
	 */
	@Nullable
	public SQFCommandDescriptor get(@NotNull String commandName) {
		return map.get(commandName.toLowerCase());
	}
}
