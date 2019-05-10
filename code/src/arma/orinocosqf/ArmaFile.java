package arma.orinocosqf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.jetbrains.annotations.NotNull;

/**
 * A wrapper class for a file that might be located inside a PBO instead of in the normal filesystem
 * 
 * @author Raven
 *
 */
public class ArmaFile {
	protected File file;

	public ArmaFile(@NotNull File file) {
		this.file = file;
	}

	/**
	 * @return The file associated with this object. Note that the input of the returned file this isn't necessarily directly the desired
	 *         input. FOr accessing the file's content, use {@link #getStream()}
	 */
	@NotNull
	public File getFile() {
		return file;
	}

	/**
	 * @return An {@link InputStream} to the desired FileContent
	 * @throws FileNotFoundException
	 */
	@NotNull
	public InputStream getStream() throws FileNotFoundException {
		return new FileInputStream(file);
	}
	
	@Override
	public String toString() {
		return file.toString();
	}
}
