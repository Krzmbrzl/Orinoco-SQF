package arma.orinocosqf;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	 * A helper class for resolving absolute paths agains PBO-prefixes
	 * 
	 * @author Raven
	 *
	 */
	static class PrefixPath {
		/**
		 * The PBO prefix
		 */
		public Path prefix;
		/**
		 * The path that is to be set equivalent to the PBO prefix
		 */
		public Path prefixLocation;

		public PrefixPath(@NotNull Path prefix, @NotNull Path prefixLocation) {
			this.prefix = prefix;
			this.prefixLocation = prefixLocation;
		}

		/**
		 * @param currentPath The path currently searched for (potentially starting with the given prefix)
		 * @return The path in the actual FileSystem corresponding to the provided path. This resolution is based on {@link #prefix} and
		 *         {@link #prefixLocation}
		 */
		public Path prefixPath(@NotNull Path currentPath) {
			if (currentPath.startsWith(prefix)) {
				return prefixLocation.resolve(prefix.relativize(currentPath));
			} else {
				return currentPath;
			}
		}
	}

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

		// Add CWD to search path if it isn't included anyways
		File cwdFile = cwd.toFile();
		if (!searchPaths.contains(cwdFile)) {
			searchPaths.add(cwdFile);
		}
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
				ArmaFile target = createArmaFile(resolveAbsolute(currentSearchDir, path, null), path);

				if (target != null) {
					return target;
				}
			}

			return null;
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
	 * @param prefixPath The {@link PrefixPath} that is used to describe the current directories once a PBO prefix has been determined. If
	 *        no prefix has been encountered (yet), this should be <code>null</code>.
	 * @return A {@link File} corresponding to the given path or <code>null</code> if the path couldn't be found
	 */
	@Nullable
	protected File resolveAbsolute(@NotNull File currentFile, @NotNull String path, @Nullable PrefixPath prefixPath) {
		if (prefixPath != null) {
			// Try to match against found prefix
			if (!path.startsWith(prefixPath.prefix.toString())) {
				// This directory is labeled with this prefix and it doesn't match the searched path -> the search won't succeed in this
				// directory
				return null;
			}

			File target = prefixPath.prefixPath(Paths.get(path)).toFile();

			if (target.exists()) {
				return target;
			} else {
				// This search won't succeed in this directory
				return null;
			}
		} else {
			// try to match against existing file structure
			if (path.startsWith(File.separator + currentFile.getName())) {
				File target = new File(currentFile.getParentFile(), path.substring(1)); // substring to remove leading separator

				if (target.exists()) {
					return target;
				}
			}
		}

		if (currentFile.isDirectory()) {
			for (File currentChild : currentFile.listFiles()) {
				File found = resolveAbsolute(currentChild, path, prefixPath);
				if (found != null) {
					return found;
				}
			}
		} else {
			if (currentFile.isFile()) {
				if (prefixPath == null && currentFile.getName().toLowerCase().toLowerCase().contains("pboprefix") && currentFile.canRead()
						&& currentFile.length() <= 5000) {
					// Read the prefix and check against path but don't even consider files greater 5kB
					try {
						String prefix = new String(Files.readAllBytes(currentFile.toPath()), Charset.defaultCharset()).replace('\\',
								File.separatorChar);
						if (prefix.contains("\n")) {
							prefix = prefix.substring(0, prefix.indexOf("\n")).trim();
						}
						if (!prefix.startsWith(File.separator)) {
							prefix = File.separator + prefix;
						}
						if (!prefix.endsWith(File.separator)) {
							prefix = prefix + File.separator;
						}

						if (path.startsWith(prefix)) {
							// This directory is to be interpreted as if it was reachable under the path specified in the prefix file
							return resolveAbsolute(currentFile.getParentFile(), path,
									new PrefixPath(Paths.get(prefix), currentFile.getParentFile().toPath()));
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
