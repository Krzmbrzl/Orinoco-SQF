package arma.orinocosqf;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
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
	protected List<File> searchPaths;
	/**
	 * The current working directory
	 */
	protected Path cwd;


	public ArmaFilesystem(@NotNull Path cwd, @NotNull List<File> searchPaths) {
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
			for (File currentSearchDir : searchPaths) {
				ArmaFile target = createArmaFile(resolveAbsolute(currentSearchDir, path), path);

				if (target != null) {
					return target;
				}
			}
			throw new InvalidPathException("Absolute path handling is not yet implemented!");
		} else {
			// relative path -> can be treated as a normal file-path
			File includeFile = cwd.resolve(path).toFile();

			return createArmaFile(includeFile, path);
		}
	}

	/**
	 * Converts the given file into a {@link ArmaFile}
	 * 
	 * @param includeFile The file to convert
	 * @param includePath The include-path that lead to this file (needed for PBO-resolving)
	 * @return The converted file or <code>null</code> if the given file is <code>null</code> or doesn't exist
	 * @throws InvalidPathException
	 */
	@Nullable
	protected ArmaFile createArmaFile(@Nullable File includeFile, @NotNull String includePath) throws InvalidPathException {
		if (includeFile == null || !includeFile.exists()) {
			return null;
		}

		if (!includeFile.isFile()) {
			throw new InvalidPathException("The path \"" + includePath + "\" exists, but is not a file");
		}

		if (includeFile.getName().toLowerCase().endsWith(".pbo")) {
			throw new UnsupportedOperationException("PBO scanning is not yet implemented!");
		} else {
			return new ArmaFile(includeFile);
		}
	}

	/**
	 * Resolves an absolute path
	 * 
	 * @param currentFile The current file to search
	 * @param path The path to search for
	 * @return A {@link File} corresponding to the given path or <code>null</code> if the path couldn't be found
	 */
	@Nullable
	protected File resolveAbsolute(@NotNull File currentFile, @NotNull String path) {
		if (currentFile.isDirectory()) {
			for (File currentChild : currentFile.listFiles()) {
				if (path.startsWith(File.separator + currentChild.getName())) {
					// try to match against existing file structure
					File target = new File(currentFile, path.substring(1)); // substring to remove leading separator

					if (target.exists()) {
						return target;
					}
				}

				File found = resolveAbsolute(currentChild, path);
				if (found != null) {
					return found;
				}
			}
		} else {
			if (currentFile.isFile()) {
				if (currentFile.getName().toLowerCase().toLowerCase().contains("pboprefix") && currentFile.canRead()
						&& currentFile.length() <= 5000) {
					// Read the prefix and check against path but don't even consider files greater 5kB
					try {
						String content = new String(Files.readAllBytes(currentFile.toPath()), Charset.defaultCharset()).replace('\\',
								File.separatorChar);
						if (content.contains("\n")) {
							content = content.substring(0, content.indexOf("\n")).trim();
						}
						if (!content.startsWith(File.separator)) {
							content = File.separator + content;
						}
						if (!content.endsWith(File.separator)) {
							content = content + File.separator;
						}

						if (path.startsWith(content)) {
							// This directory is to be interpreted as if it was reachable under the path specified in the prefix file
							// adjust path to point to this directory and start searching this directory again
							path = path.substring(content.length());

							resolveAbsolute(currentFile, path);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					if (currentFile.getName().toLowerCase().endsWith(".pbo")) {
						// TODO: Check PBOs as well
					}
				}
			}
		}

		return null;
	}
}
