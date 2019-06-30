package arma.orinocosqf.lexer;

import arma.orinocosqf.TextBuffer;
import arma.orinocosqf.util.SimpleTextBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation of a {@link OrinocoLexerContext} which will have text-buffering enabled
 * 
 * @author Raven
 *
 */
public class BufferingOrinocoLexerContext extends NonBufferingOrinocoLexerContext {
	/**
	 * The buffer for the original input
	 */
	protected TextBuffer originalText;
	/**
	 * The buffer for the preprocessed input
	 */
	protected SimpleTextBuffer preprocessedBuffer;

	
	/**
	 * 
	 * @param lexer The lexer this context is being instantiated for
	 */
	public BufferingOrinocoLexerContext(@NotNull OrinocoLexer lexer) {
		super(lexer);
		
		this.originalText = new SimpleTextBuffer();
		this.preprocessedBuffer = new SimpleTextBuffer();
	}

	
	@Override
	public boolean isTextBufferingEnabled() {
		return true;
	}

	@Override
	@Nullable
	public TextBuffer getTextBuffer() {
		return originalText;
	}

	@Override
	@Nullable
	public TextBuffer getTextBufferPreprocessed() {
		return preprocessedBuffer;
	}

}
