package arma.orinocosqf.helpers;

import arma.orinocosqf.OrinocoTokenProcessorAdapter;
import arma.orinocosqf.preprocessing.OrinocoPreProcessor;

public class TestOrinocoPreprocessor extends OrinocoPreProcessor {

	public TestOrinocoPreprocessor() {
		super(new OrinocoTokenProcessorAdapter() {
		}, new DummyFileSystem());
	}

	public boolean isMacroNamePart(char c, boolean isFirstLetter) {
		return super.isMacroNamePart(c, isFirstLetter);
	}
}
