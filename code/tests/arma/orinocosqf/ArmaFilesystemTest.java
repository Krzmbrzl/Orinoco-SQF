package arma.orinocosqf;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.BeforeClass;
import org.junit.Test;

import arma.orinocosqf.exceptions.InvalidPathException;
import arma.orinocosqf.preprocessing.ArmaFile;
import arma.orinocosqf.preprocessing.ArmaFilesystem;

public class ArmaFilesystemTest {

	static ArmaFilesystem fs;
	static File base;
	static File cwd;
	static File searchPath1;
	static File searchPath2;

	@BeforeClass
	public static void setUp() throws Exception {
		base = new File(".." + File.separator + ".." + File.separator + "code" + File.separator + "tests" + File.separator + "resources"
				+ File.separator + "include");
		cwd = new File(base, "MyAwesomeMod" + File.separator + "addons");
		searchPath1 = new File(base, "SearchDir1");
		searchPath2 = new File(base, "SearchDir2");

		System.out.println("Base dir:\t\t\t" + base.toPath().toAbsolutePath().normalize().toString());
		System.out.println("CWD:\t\t\t" + cwd.toPath().toAbsolutePath().normalize().toString());
		System.out.println("searchPath1:\t\t" + searchPath1.toPath().toAbsolutePath().normalize().toString());
		System.out.println("searchPath2:\t\t" + searchPath2.toPath().toAbsolutePath().normalize().toString());

		assertTrue("Base dir doesn't exist", base.exists());
		assertTrue("CWD doesn't exist", cwd.exists());
		assertTrue("searchPath1 doesn't exist", searchPath1.exists());
		assertTrue("searchPath2 doesn't exist", searchPath2.exists());

		List<File> searchPaths = new ArrayList<>();
		searchPaths.add(searchPath1);
		searchPaths.add(searchPath2);

		fs = new ArmaFilesystem(cwd.toPath(), searchPaths);
	}

	void assertFindFile(@Nullable File target, @NotNull String path) {
		try {
			ArmaFile foundFile = fs.resolve(path);

			if (foundFile == null && target == null) {
				// Assertion passed
				return;
			}
			if (foundFile == null && target != null) {
				fail("The path \"" + path + "\" couldn't be resolved");
			}
			if (foundFile != null && target == null) {
				fail("The path \"" + path + "\" could be resolved, but shouldn't have been. It got resolved to "
						+ foundFile.getFile().toPath().normalize().toString());
			}

			// both are != null
			assertEquals("The path \"" + path + "\" was resolved wrongly", target.toPath().normalize(),
					foundFile.getFile().toPath().normalize());
		} catch (InvalidPathException e) {
			fail("An unexpected excpetion has been thrown: " + e.getMessage());
		}
	}

	@Test
	public void relativePaths_negative() {
		assertFindFile(null, "dummy.txt");
		assertFindFile(null, "..\\dummy.txt");
		assertFindFile(null, ".\\dummy.txt");
		assertFindFile(null, "..\\sub\\dummy.txt");
		assertFindFile(null, "sub\\dummy.txt");
		assertFindFile(null, "sub\\..\\sub\\dummy.txt");
	}

	@Test
	public void relativePaths() {
		assertFindFile(new File(cwd, "helpers.hpp"), "helpers.hpp");
		assertFindFile(new File(cwd, "helpers.hpp"), ".\\helpers.hpp");
		assertFindFile(new File(cwd, "helpers.hpp"), "subModule\\..\\helpers.hpp");
		assertFindFile(new File(cwd, "helpers.hpp"), ".\\subModule\\..\\helpers.hpp");

		assertFindFile(new File(cwd, "$PBOPREFIX$"), "$PBOPREFIX$");
		assertFindFile(new File(cwd, "$PBOPREFIX$"), ".\\$PBOPREFIX$");
		assertFindFile(new File(cwd, "$PBOPREFIX$"), "subModule\\..\\$PBOPREFIX$");
		assertFindFile(new File(cwd, "$PBOPREFIX$"), ".\\subModule\\..\\$PBOPREFIX$");
	}

	@Test
	public void absolutePaths_negative() {
		assertFindFile(null, "\\dummy.txt");
		assertFindFile(null, "\\test\\dummy.txt");
		assertFindFile(null, "\\someFolder\\dummy.txt");
		assertFindFile(null, "\\somethingElse\\dummy.txt");
		assertFindFile(null, "\\..\\dummy.txt");
		// These files shouldn't be found as the parent dir is labeled with a PBO prefix
		assertFindFile(null, "\\sub\\text.txt");
		assertFindFile(null, "\\text.txt");
		assertFindFile(null, "\\weird.hpp");
		assertFindFile(null, "\\addons\\weird.hpp");
	}

	@Test
	public void absolutePaths_prefix() {
		// Prefix to address local files
		assertFindFile(new File(cwd, "macros.hpp"), "\\test\\someReallyWeirdName\\Awesome\\macros.hpp");
		assertFindFile(new File(cwd, "macros.hpp"), "\\test\\someReallyWeirdName\\Awesome\\.\\macros.hpp");
		assertFindFile(new File(cwd, "macros.hpp"), "\\test\\someReallyWeirdName\\Awesome\\subModule\\..\\macros.hpp");

		assertFindFile(new File(cwd, "$PBOPREFIX$"), "\\test\\someReallyWeirdName\\Awesome\\$PBOPREFIX$");
		assertFindFile(new File(cwd, "$PBOPREFIX$"), "\\test\\someReallyWeirdName\\Awesome\\.\\$PBOPREFIX$");
		assertFindFile(new File(cwd, "$PBOPREFIX$"), "\\test\\someReallyWeirdName\\Awesome\\subModule\\..\\$PBOPREFIX$");


		// Prefix to address "remote" files
		assertFindFile(new File(searchPath2, "test.hpp"), "\\some\\prefix\\test.hpp");
		assertFindFile(new File(searchPath2, "test.hpp"), "\\some\\prefix\\.\\test.hpp");
		assertFindFile(new File(searchPath2, "test.hpp"), "\\some\\prefix\\sub\\..\\test.hpp");
	}

	@Test
	public void absolutePaths_fileStructure() {
		assertFindFile(new File(searchPath1, "AnotherAddon" + File.separator + "addons" + File.separator + "macros.hpp"),
				"\\AnotherAddon\\addons\\macros.hpp");
		assertFindFile(new File(searchPath1, "AnotherAddon" + File.separator + "addons" + File.separator + "macros.hpp"),
				"\\AnotherAddon\\addons\\.\\macros.hpp");
		assertFindFile(new File(searchPath1, "AnotherAddon" + File.separator + "addons" + File.separator + "macros.hpp"),
				"\\AnotherAddon\\addons\\anotherSub\\..\\macros.hpp");
		
		assertFindFile(new File(searchPath1, "AnotherAddon" + File.separator + "addons" + File.separator + "macros.hpp"),
				"\\addons\\macros.hpp");
		assertFindFile(new File(searchPath1, "AnotherAddon" + File.separator + "addons" + File.separator + "macros.hpp"),
				"\\addons\\.\\macros.hpp");
		assertFindFile(new File(searchPath1, "AnotherAddon" + File.separator + "addons" + File.separator + "macros.hpp"),
				"\\addons\\anotherSub\\..\\macros.hpp");
	}

}
