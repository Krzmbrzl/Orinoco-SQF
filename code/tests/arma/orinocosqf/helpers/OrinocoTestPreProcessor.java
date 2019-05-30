package arma.orinocosqf.helpers;

import arma.orinocosqf.preprocessing.OrinocoPreProcessor;
import arma.orinocosqf.tokenprocessing.OrinocoTokenProcessorAdapter;

public class OrinocoTestPreProcessor extends OrinocoPreProcessor {

	public OrinocoTestPreProcessor() {
		super(new OrinocoTokenProcessorAdapter() {
		}, new DummyFileSystem());
	}

	public boolean isMacroNamePart(char c, boolean isFirstLetter) {
		return super.isMacroNamePart(c, isFirstLetter);
	}
}
