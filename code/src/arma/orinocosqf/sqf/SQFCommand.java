package arma.orinocosqf.sqf;

import arma.orinocosqf.CaseInsentiveKey;
import arma.orinocosqf.Command;
import arma.orinocosqf.syntax.BIGame;
import arma.orinocosqf.syntax.CommandXMLInputStream;
import arma.orinocosqf.syntax.Param;
import arma.orinocosqf.util.MemCompact;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kayler
 * @since 06/11/2016.
 */
public class SQFCommand implements CaseInsentiveKey, Command<SQFCommandSyntax>, MemCompact {
	/**
	 * @see CommandXMLInputStream#CommandXMLInputStream(String)
	 */
	@Nullable
	public static SQFCommand getCommandFromFile(@NotNull String commandName) {
		try {
			return SQFCommandSyntaxXMLLoader.importFromStream(new CommandXMLInputStream(commandName), false);
		} catch (Exception e) {
			if (e instanceof UnsupportedOperationException) {
				//command doesn't have a syntax xml file
				System.out.println(e.getMessage());
				return null;
			}
			e.printStackTrace();
			return null;
		}
	}

	/** {@link #getSyntaxList()} */
	private final List<SQFCommandSyntax> syntaxList;
	/** {@link #getCommandName()} */
	private final String commandName;
	/** {@link #getGameVersion()} */
	private String gameVersion;
	/** {@link #getGameIntroducedIn()} */
	private final BIGame game;

	/** {@link #setDeprecated(boolean)} */
	private boolean deprecated = false;
	/** {@link #setUncertain(boolean)} */
	private boolean uncertain = false;

	private boolean striclyNular, canBeNular, strictlyBinary, canBeBinary, strictlyUnary, canBeUnary;

	public SQFCommand(@NotNull String commandName) {
		this.commandName = commandName;
		syntaxList = Collections.emptyList();
		game = BIGame.UNKNOWN;
	}

	public SQFCommand(@NotNull String commandName,
					  @NotNull List<SQFCommandSyntax> syntaxList,
					  @NotNull String gameVersion,
					  @NotNull BIGame game) {
		this.syntaxList = syntaxList;
		this.commandName = commandName;
		this.gameVersion = gameVersion;
		this.game = game;

		striclyNular = detectStrictlyNular();
		canBeNular = detectCanBeNular();
		strictlyBinary = detectStrictlyBinary();
		canBeBinary = detectCanBeBinary();
		strictlyUnary = detectStrictlyUnary();
		canBeUnary = detectCanBeUnary();
	}

	@Override
	@NotNull
	public String getName() {
		return commandName;
	}

	@Override
	public boolean isStrictlyNular() {
		return striclyNular;
	}

	@Override
	public boolean canBeNular() {
		return canBeNular;
	}

	@Override
	public boolean isStrictlyBinary() {
		return strictlyBinary;
	}

	@Override
	public boolean canBeBinary() {
		return canBeBinary;
	}

	@Override
	public boolean isStrictlyUnary() {
		return strictlyUnary;
	}

	@Override
	public boolean canBeUnary() {
		return canBeUnary;
	}

	private boolean detectStrictlyNular() {
		for (SQFCommandSyntax cs : syntaxList) {
			if (cs.getLeftParam() != null || cs.getRightParam() != null) {
				return false;
			}
		}
		return true;
	}

	private boolean detectCanBeNular() {
		for (SQFCommandSyntax cs : syntaxList) {
			if (cs.getLeftParam() == null && cs.getRightParam() == null) {
				return true;
			}
		}
		return false;
	}

	private boolean detectStrictlyBinary() {
		for (SQFCommandSyntax cs : syntaxList) {
			if (cs.getLeftParam() == null || cs.getRightParam() == null) {
				return false;
			}
		}
		return true;
	}

	private boolean detectCanBeBinary() {
		for (SQFCommandSyntax cs : syntaxList) {
			if (cs.getLeftParam() != null && cs.getRightParam() != null) {
				return true;
			}
		}
		return false;
	}

	private boolean detectStrictlyUnary() {
		for (SQFCommandSyntax cs : syntaxList) {
			if (cs.getLeftParam() != null || cs.getRightParam() == null) {
				return false;
			}
		}
		return true;
	}

	private boolean detectCanBeUnary() {
		for (SQFCommandSyntax cs : syntaxList) {
			if (cs.getLeftParam() == null && cs.getRightParam() != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return a list of {@link SQFCommandSyntax} instances for this command
	 */
	@Override
	@NotNull
	public List<SQFCommandSyntax> getSyntaxList() {
		return syntaxList;
	}

	@Override
	public int getPrecedence() {
		throw new UnsupportedOperationException("todo");
	}

	/**
	 * @return the command's case-sensitive name
	 */
	@NotNull
	public String getCommandName() {
		return commandName;
	}

	@Override
	@NotNull
	public BIGame getGameIntroducedIn() {
		return game;
	}

	/**
	 * @return game version of {@link #getGameIntroducedIn()}
	 */
	@NotNull
	public String getGameVersion() {
		return gameVersion;
	}

	@Override
	public boolean isDeprecated() {
		return deprecated;
	}

	void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	/**
	 * @return true if the syntaxes for the command aren't exactly known and the current syntaxes are estimates
	 */
	public boolean isUncertain() {
		return uncertain;
	}

	void setUncertain(boolean uncertain) {
		this.uncertain = uncertain;
	}

	/**
	 * @return a list of all literals across all {@link Param}s
	 * @see Param#getLiterals()
	 */
	@NotNull
	public Iterable<String> getAllLiterals() {
		List<String> all = new ArrayList<>();
		for (SQFCommandSyntax syntax : syntaxList) {
			for (Param p : syntax.getAllParams()) {
				all.addAll(p.getLiterals());
			}
		}
		return all;
	}

	@Override
	public int hashCode() {
		return commandName.hashCode();
	}

	@Override
	public String toString() {
		return "CommandDescriptor{" +
				"commandName='" + commandName + '\'' +
				", deprecated=" + deprecated +
				", uncertain=" + uncertain +
				'}';
	}

	@Override
	@NotNull
	public CharSequence getKey() {
		return commandName;
	}

	@Override
	public void memCompact() {
		if (syntaxList instanceof ArrayList) {
			((ArrayList<SQFCommandSyntax>) syntaxList).trimToSize();
		}
		for (SQFCommandSyntax cs : syntaxList) {
			cs.memCompact();
		}
	}
}
