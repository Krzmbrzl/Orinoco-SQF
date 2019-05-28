package arma.orinocosqf.exceptions;

import org.jetbrains.annotations.Nullable;

import arma.orinocosqf.preprocessing.bodySegments.BodySegment;

/**
 * An exception thrown during preprocessing
 * 
 * @author Raven
 *
 */
public class OrinocoPreprocessorException extends OrinocoException {
	private static final long serialVersionUID = -673179213828861313L;

	BodySegment context;

	/**
	 * Constructs a new exception with {@code null} as its detail message. The cause is not initialized, and may subsequently be initialized
	 * by a call to {@link #initCause}.
	 * 
	 * @param context The {@link BodySegment} in whose context this exception occurred
	 */
	public OrinocoPreprocessorException(@Nullable BodySegment context) {
		super();

		this.context = context;
	}

	/**
	 * Constructs a new exception with the specified detail message. The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 *
	 * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
	 * @param context The {@link BodySegment} in whose context this exception occurred
	 */
	public OrinocoPreprocessorException(String message, @Nullable BodySegment context) {
		super(message);

		this.context = context;
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p>
	 * Note that the detail message associated with {@code cause} is <i>not</i> automatically incorporated in this exception's detail
	 * message.
	 *
	 * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
	 * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code null} value is permitted,
	 *        and indicates that the cause is nonexistent or unknown.)
	 * @param context The {@link BodySegment} in whose context this exception occurred
	 */
	public OrinocoPreprocessorException(String message, Throwable cause, @Nullable BodySegment context) {
		super(message, cause);

		this.context = context;
	}

	/**
	 * Constructs a new exception with the specified cause and a detail message of {@code (cause==null ? null : cause.toString())} (which
	 * typically contains the class and detail message of {@code cause}). This constructor is useful for exceptions that are little more
	 * than wrappers for other throwables (for example, {@link java.security.PrivilegedActionException}).
	 *
	 * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code null} value is permitted,
	 *        and indicates that the cause is nonexistent or unknown.)
	 * @param context The {@link BodySegment} in whose context this exception occurred
	 */
	public OrinocoPreprocessorException(Throwable cause, @Nullable BodySegment context) {
		super(cause);

		this.context = context;
	}

	/**
	 * Constructs a new exception with the specified detail message, cause, suppression enabled or disabled, and writable stack trace
	 * enabled or disabled.
	 *
	 * @param message the detail message.
	 * @param cause the cause. (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
	 * @param enableSuppression whether or not suppression is enabled or disabled
	 * @param writableStackTrace whether or not the stack trace should be writable
	 * @param context The {@link BodySegment} in whose context this exception occurred
	 */
	protected OrinocoPreprocessorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
			@Nullable BodySegment context) {
		super(message, cause, enableSuppression, writableStackTrace);

		this.context = context;
	}

	/**
	 * @return The context {@link BodySegment} of this exception
	 */
	@Nullable
	public BodySegment getContext() {
		return context;
	}
}
