package arma.orinocosqf.exceptions;

/**
 * An exception thrown during preprocessing
 * 
 * @author Raven
 *
 */
public class OrinocoPreprocessorException extends OrinocoException {
	private static final long serialVersionUID = -673179213828861313L;

	int offset;
	int length;

	/**
	 * Constructs a new exception with {@code null} as its detail message. The cause is not initialized, and may subsequently be initialized
	 * by a call to {@link #initCause}.
	 * 
	 * @param offset The offset at which this exception occured
	 * @param length The length of the affected area
	 */
	public OrinocoPreprocessorException(int offset, int length) {
		super();

		this.offset = offset;
		this.length = length;
	}

	/**
	 * Constructs a new exception with the specified detail message. The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 *
	 * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
	 * @param offset The offset at which this exception occured
	 * @param length The length of the affected area
	 */
	public OrinocoPreprocessorException(String message, int offset, int length) {
		super(message);

		this.offset = offset;
		this.length = length;
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
	 * @param offset The offset at which this exception occured
	 * @param length The length of the affected area
	 */
	public OrinocoPreprocessorException(String message, Throwable cause, int offset, int length) {
		super(message, cause);

		this.offset = offset;
		this.length = length;
	}

	/**
	 * Constructs a new exception with the specified cause and a detail message of {@code (cause==null ? null : cause.toString())} (which
	 * typically contains the class and detail message of {@code cause}). This constructor is useful for exceptions that are little more
	 * than wrappers for other throwables (for example, {@link java.security.PrivilegedActionException}).
	 *
	 * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code null} value is permitted,
	 *        and indicates that the cause is nonexistent or unknown.)
	 * @param offset The offset at which this exception occured
	 * @param length The length of the affected area
	 */
	public OrinocoPreprocessorException(Throwable cause, int offset, int length) {
		super(cause);

		this.offset = offset;
		this.length = length;
	}

	/**
	 * Constructs a new exception with the specified detail message, cause, suppression enabled or disabled, and writable stack trace
	 * enabled or disabled.
	 *
	 * @param message the detail message.
	 * @param cause the cause. (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
	 * @param enableSuppression whether or not suppression is enabled or disabled
	 * @param writableStackTrace whether or not the stack trace should be writable
	 * @param offset The offset at which this exception occured
	 * @param length The length of the affected areas
	 */
	protected OrinocoPreprocessorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
			int offset, int length) {
		super(message, cause, enableSuppression, writableStackTrace);

		this.offset = offset;
		this.length = length;
	}

	/**
	 * @return The offset at which this exception occurred
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @return The length of the area this exception occurred in
	 */
	public int getLength() {
		return length;
	}
}
