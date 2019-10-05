package arma.orinocosqf;

import arma.orinocosqf.parsing.postfix.OrinocoNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A token class that can either hold text or an id for things like
 * {@link OrinocoSQFTokenType#Command}
 *
 * @author K
 * @since 02/21/2019
 */
public class OrinocoToken implements OrinocoNode {
	private String text;
	private int id;
	private OrinocoTokenType tokenType;
	private int preprocessedOffset;
	private int preprocessedLength;
	private int originalOffset;
	private int originalLength;

	public OrinocoToken(@NotNull String text, @NotNull OrinocoTokenType tokenType, int preprocessedOffset, int preprocessedLength, int originalOffset,
						int originalLength) {
		this(tokenType, preprocessedOffset, preprocessedLength, originalOffset, originalLength);

		if (tokenType.isIdBased()) {
			throw new IllegalArgumentException("Tried to instantiate Id-based token with text!");
		}
		this.text = text;
		this.id = -1;
	}

	public OrinocoToken(int id, @NotNull OrinocoTokenType tokenType, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength) {
		this(tokenType, preprocessedOffset, preprocessedLength, originalOffset, originalLength);

		if (!tokenType.isIdBased()) {
			throw new IllegalArgumentException("Tried to instantiate text-based token with Id!");
		}
		this.text = null;
		this.id = id;
	}

	private OrinocoToken(OrinocoTokenType tokenType, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength) {
		this.preprocessedLength = preprocessedLength;
		this.tokenType = tokenType;
		this.preprocessedOffset = preprocessedOffset;
		this.originalOffset = originalOffset;
		this.originalLength = originalLength;
	}

	/**
	 * @return null if this token is id based, or the text if the token is text
	 *         based
	 */
	@Nullable
	public String getText() {
		return text;
	}

	/** @return -1 if the token is text based, or the id if id based */
	public int getId() {
		return id;
	}

	@NotNull
	public OrinocoTokenType getTokenType() {
		return tokenType;
	}

	public int getPreprocessedOffset() {
		return preprocessedOffset;
	}

	public int getPreprocessedLength() {
		return preprocessedLength;
	}

	public int getOriginalOffset() {
		return originalOffset;
	}

	public int getOriginalLength() {
		return originalLength;
	}


	@Override
	public boolean isScopeNode() {
		return false;
	}

	@Override
	public boolean isArrayNode() {
		return false;
	}

	@Override
	public boolean isCollectionNode() {
		return false;
	}
}
