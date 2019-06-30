package arma.orinocosqf.tokenprocessing;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.OrinocoTokenProcessor;
import arma.orinocosqf.lexer.OrinocoLexerContext;

/**
 * This implementation of a {@link OrinocoTokenProcessor} will simply write the content of the preprocessed TextBuffer to a provided writer
 * once lexing (and preprocessing) has finished
 * 
 * @author Raven
 *
 */
public class OutputTokenProcessor extends OrinocoTokenProcessorAdapter {

	/**
	 * The writer that shall be used to write out the preprocessed TextBuffer
	 */
	protected OutputStreamWriter outWriter;


	/**
	 * Creates an instance of this class, without specifying a {@link OutputStreamWriter}. The use of this constructor is discouraged.<br>
	 * If you do so nontheless, <b>you promise to set the writer via {@link #setOutputWriter(OutputStreamWriter)} before the lexer calls
	 * {@link #end(OrinocoLexerContext)}</b>.
	 */
	public OutputTokenProcessor() {
	}

	/**
	 * @param outWriter The writer that shall be used to write out the preprocessed TextBuffer
	 */
	public OutputTokenProcessor(@NotNull OutputStreamWriter outWriter) {
		setOutputWriter(outWriter);
	}

	/**
	 * Sets the {@link OutputStreamWriter} this instance shall use
	 * 
	 * @param outWriter The new writer that shall be used to write out the preprocessed TextBuffer
	 */
	public void setOutputWriter(@NotNull OutputStreamWriter outWriter) {
		this.outWriter = outWriter;
	}

	@Override
	public void begin(@NotNull OrinocoLexerContext ctx) {
		if (!ctx.isTextBufferingEnabled()) {
			throw new IllegalStateException("Can't use " + this.getClass().getSimpleName() + " while text-buffering is disabled in lexer!");
		}
	}


	@Override
	public void end(@NotNull OrinocoLexerContext ctx) {
		if (!ctx.isTextBufferingEnabled()) {
			throw new IllegalStateException("Can't use " + this.getClass().getSimpleName() + " while text-buffering is disabled in lexer!");
		}

		if (outWriter == null) {
			throw new NullPointerException("No outWriter has been set!");
		}

		// Write the preprocessed buffer to the outWriter
		try {
			outWriter.write(ctx.getTextBufferPreprocessed().getText());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reset() {
		try {
			outWriter.flush();
			outWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		outWriter = null;
	}

}
