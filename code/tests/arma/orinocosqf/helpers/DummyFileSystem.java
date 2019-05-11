package arma.orinocosqf.helpers;

import java.io.File;
import java.util.ArrayList;

import arma.orinocosqf.preprocessing.ArmaFilesystem;

/**
 * This dummy extension of an {@link ArmaFilesystem} won't find anything and is intended for testing only
 * 
 * @author Raven
 *
 */
public class DummyFileSystem extends ArmaFilesystem {

	public DummyFileSystem() {
		super(new File("").toPath(), new ArrayList<>());
	}

}
