package arma.orinocosqf.helpers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import arma.orinocosqf.exceptions.InvalidPathException;
import arma.orinocosqf.preprocessing.ArmaFile;
import arma.orinocosqf.preprocessing.ArmaFilesystem;

/**
 * A virtual implementation of a {@link ArmaFilesystem}. It is intended for testing purposes only. It doesn't perform any lookups and is
 * thus not suited for testing the actual FileSystem class. It operators on a map that maps "filenames" to a given content.
 * 
 * @author Raven
 *
 */
public class VirtualArmaFileSystem extends ArmaFilesystem {

	protected Map<String, String> virtualFileMap;

	public VirtualArmaFileSystem() {
		super(new File("__VirtualCWD__").toPath(), new ArrayList<>(0));

		virtualFileMap = new HashMap<String, String>();
	}

	/**
	 * Clears the map of virtual files
	 */
	public void clearFiles() {
		virtualFileMap.clear();
	}

	/**
	 * Adds a virtual file (Aka adds an entry to {@link #virtualFileMap})
	 * 
	 * @param name The name of the virtual file
	 * @param content Its content
	 */
	public void addVirtualFile(@NotNull String name, @NotNull String content) {
		virtualFileMap.put(name, content);
	}

	@Override
	@Nullable
	public ArmaFile resolve(@NotNull String path) throws InvalidPathException {
		return virtualFileMap.containsKey(path) ? new VirtualArmaFile(path) : null;
	}


	/**
	 * A virtual implementation of a {@link ArmaFile}. It is intended for testing-purposes only
	 * 
	 * @author Raven
	 *
	 */
	class VirtualArmaFile extends ArmaFile {

		protected String name;

		public VirtualArmaFile(@NotNull String name) {
			super(new File("__VirtualFile: " + name));

			this.name = name;
		}

		@Override
		@NotNull
		public InputStream getStream() throws FileNotFoundException {
			return new ByteArrayInputStream(virtualFileMap.get(name).getBytes(Charset.forName("utf-8")));
		}

	}

}
