package arma.orinocosqf;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import arma.orinocosqf.exceptions.InvalidPathException;

/**
 * This class represents the Arma FileSystem as it would be used inside of Arma (in turns of resolving relative and absolute paths
 * 
 * @author Raven
 *
 */
public class ArmaFilesystem {
	/**
	 * The paths to search for resources when resolving an absolute path
	 */
	protected List<Path> searchPaths;
	/**
	 * The current working directory
	 */
	protected Path cwd;


	public ArmaFilesystem(@NotNull Path cwd, @NotNull List<Path> searchPaths) {
		this.cwd = cwd;
		this.searchPaths = searchPaths;
	}

	/**
	 * Resolves the given String as a path in the Arma filesystem
	 * 
	 * @param path The path to resolve
	 * @return An {@link ArmaFile} corresponding to the given path or <code>null</code> if it couldn't be resolved.
	 * @throws InvalidPathException If the given path is invalid
	 */
	@Nullable
	public ArmaFile resolve(@NotNull String path) throws InvalidPathException {
		if (path.contains("/")) {
			// Arma paths always have to use backslashes as path separators
			throw new InvalidPathException("Slashes are not permitted as path separators in Arma!");
		}
		if (path.isEmpty()) {
			throw new InvalidPathException("Empty path");
		}

		char separator = '\\';

		// if on non-windows systems: Replace all backslashes with forward slashes
		if (!System.getProperty("os.name").toLowerCase().contains("win")) {
			path = path.replace('\\', '/');
			separator = '/';
		}

		if (path.charAt(0) == separator) {
			// absolute path
			throw new InvalidPathException("Absolute path handling is not yet implemented!");
		} else {
			// relative path -> can be treated as a normal file-path
			File includeFile = cwd.resolve(path).toFile();

			return includeFile.exists() ? new ArmaFile(includeFile) : null;
		}
	}
}
