import jdk.internal.jline.internal.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * A token class that can either hold text or an id for things like {@link OrinocoTokenType#Command}
 *
 * @author K
 * @since 02/21/2019
 */
public class OrinocoToken {
	private String text;
	private int id;
	private OrinocoTokenType tokenType;

	public OrinocoToken(@NotNull String text, @NotNull OrinocoTokenType tokenType) {
		if(tokenType.isIdBased()) {
			throw new IllegalArgumentException();
		}
		this.text = text;
		this.id = -1;
		this.tokenType = tokenType;
	}

	public OrinocoToken(int id, @NotNull OrinocoTokenType tokenType) {
		if(!tokenType.isIdBased()) {
			throw new IllegalArgumentException();
		}
		this.text = null;
		this.id = id;
		this.tokenType = tokenType;
	}

	/** @return null if this token is id based, or the text if the token is text based */
	@Nullable
	public String getText() {
		return text;
	}

	/**@return -1 if the token is text based, or the id if id based*/
	public int getId() {
		return id;
	}

	@NotNull
	public OrinocoTokenType getTokenType() {
		return tokenType;
	}


}
