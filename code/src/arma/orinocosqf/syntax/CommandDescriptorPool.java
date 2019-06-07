package arma.orinocosqf.syntax;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Used for caching and loading SQF command syntax's ({@link CommandSyntax}) from their xml files. This class also features concurrent
 * access and XML loading. It is recommend to not have a lot of instances of this class to save memory and get optimal concurrency.
 *
 * @author Kayler
 * @see com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFSyntaxHelper#getDescriptor(String)
 * @since 11/13/2017
 */
public class CommandDescriptorPool {
	private final DescriptorWrapper[] tallyCache = new DescriptorWrapper[30];
	/**
	 * Cache of commands that are frequently used in SQF. All commands in here should have there syntax XML parsed once to save overall time
	 * when fetching lots of commands at once.
	 */
	private final Map<String, CommandDescriptor> frequentCache = new HashMap<>();
	private final Random random = new Random();
	private final LinkedBlockingQueue<ProcessingCommand> processing = new LinkedBlockingQueue<>();

	private final DescriptorWrapper PLACEHOLDER = new DescriptorWrapper(new CommandDescriptor(""));

	public CommandDescriptorPool() {
		//fill the array to prevent null pointer exception when sorting array
		Arrays.fill(tallyCache, PLACEHOLDER);

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
			frequentCache.put(command.toLowerCase(), CommandDescriptor.getDescriptorFromFile(command));
		}
	}

	/**
	 * A thread-safe way of retrieving a {@link CommandDescriptor} instance from file. This method will check a map full of frequently used
	 * commands. If the command exists in the frequently used commands, the frequently used command will only be parsed once in it's
	 * lifetime to speed-up access times.
	 * <p>
	 * If the command is not frequently used, it will check an independent tallied cache. If it exists there, it will keep tally of how many
	 * times it gets used. In theory, commands with a low tally count will not last as long in the cache as high-tallied commands. However,
	 * the tallied cache can be completely replaced over time.
	 * <p>
	 * This method will return null when a syntax xml file doesn't exist or the XML had an error being parsed.
	 * <p>
	 * This method will not get any parameter descriptions, return descriptions, or any other type of descriptions. If you wish to get these
	 * descriptions, you will need to invoke {@link SQFCommandSyntaxXMLLoader#importFromStream(CommandXMLInputStream, boolean)} directly.
	 *
	 * @param commandName the name of command (case sensitivity doesn't matter).
	 * @return the {@link CommandDescriptor} instance, or null if one couldn't be created
	 */
	@Nullable
	public CommandDescriptor get(@NotNull String commandName) {
		commandName = commandName.toLowerCase();
		if (frequentCache.containsKey(commandName)) {
			return frequentCache.get(commandName);
		}

		synchronized (tallyCache) {
			for (DescriptorWrapper w : tallyCache) {
				if (w == null || w == PLACEHOLDER) {
					continue;
				}
				if (w.descriptor.getCommandName().equalsIgnoreCase(commandName)) {
					w.requestCount++;
					return w.descriptor;
				}
			}
		}

		ProcessingCommand processingCommand;

		synchronized (processing) {
			ProcessingCommand waitFor = null;
			for (ProcessingCommand processing : processing) {
				if (processing.commandName.equalsIgnoreCase(commandName)) {
					waitFor = processing;
					break;
				}
			}
			if (waitFor != null) {
				try {
					return waitFor.result.get();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			} else {
				processingCommand = new ProcessingCommand(commandName);
				processing.add(processingCommand);
			}
		}

		CommandDescriptor d = CommandDescriptor.getDescriptorFromFile(commandName);

		if (d == null) {
			synchronized (processing) {
				processingCommand.result.put(null);
				processing.remove(processingCommand);
			}
			return null;
		}
		DescriptorWrapper ret = new DescriptorWrapper(d);

		synchronized (tallyCache) {
			Arrays.sort(tallyCache);

			/*
			 * Instead of replacing the least recently used command in the cache, we are replacing a randomly
			 * selected command in the lower third of the cache. The reason we are doing this is because
			 * if we only replaced the least recently used, the least recently used command
			 * would constantly be swapped out; also, it would be hard for the rest of the commands to get
			 * replaced in the cache.
			 */
			int replaceInd = random.nextInt(tallyCache.length / 3);
			tallyCache[replaceInd] = ret;

			synchronized (processing) {
				processingCommand.result.put(ret.descriptor);
				processing.remove(processingCommand);
			}
		}


		return ret.descriptor;
	}

	private class DescriptorWrapper implements Comparable<DescriptorWrapper> {

		@NotNull
		private final CommandDescriptor descriptor;
		private int requestCount = 0;

		public DescriptorWrapper(@NotNull CommandDescriptor descriptor) {
			this.descriptor = descriptor;
		}

		@Override
		public int compareTo(@NotNull DescriptorWrapper o) {
			//sort from -infinity to +infinity (A to Z)
			return this.requestCount - o.requestCount;
		}

		@Override
		public String toString() {
			return "DescriptorWrapper{name=" + descriptor.getCommandName() + ", reqCount=" + requestCount + "}";
		}
	}

	private class ProcessingCommand {
		@NotNull
		private final String commandName;
		private FutureImpl<CommandDescriptor> result = new FutureImpl<>();

		public ProcessingCommand(@NotNull String commandName) {
			this.commandName = commandName;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}
			if (o instanceof ProcessingCommand) {
				return this.commandName.equalsIgnoreCase(((ProcessingCommand) o).commandName);
			}
			return false;
		}
	}

	private class FutureImpl<T> implements Future<T> {
		private final CountDownLatch latch = new CountDownLatch(1);
		private volatile T value;

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			return latch.getCount() == 0;
		}

		@Override
		public T get() throws InterruptedException {
			latch.await();
			return value;
		}

		@Override
		public T get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
			if (latch.await(timeout, unit)) {
				return value;
			} else {
				throw new TimeoutException();
			}
		}

		// calling this more than once doesn't make sense, and won't work properly in this implementation. so: don't.
		public void put(T result) {
			value = result;
			latch.countDown();
		}
	}
}
