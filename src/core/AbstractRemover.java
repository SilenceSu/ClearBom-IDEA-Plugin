package core;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by SilenceSu on 2017/5/19.
 * Email:silence.sx@gmail.com
 */
public class AbstractRemover implements Remover {

	public static int PROCESSED;
	public static int UPDATED;

	protected Parameters parameters;

	public AbstractRemover(Parameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public int checkBOM(File file) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int checkBOM(String str) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int checkBOM(StringBuilder builder) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection work() {
		throw new UnsupportedOperationException();
	}
}
