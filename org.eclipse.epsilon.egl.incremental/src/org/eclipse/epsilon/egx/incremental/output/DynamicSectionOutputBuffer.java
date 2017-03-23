package org.eclipse.epsilon.egx.incremental.output;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.epsilon.egl.exceptions.EglRuntimeException;
import org.eclipse.epsilon.egl.exceptions.EglStoppedException;
import org.eclipse.epsilon.egl.formatter.Formatter;
import org.eclipse.epsilon.egl.output.IOutputBuffer;

public class DynamicSectionOutputBuffer implements IOutputBuffer {
	
	private final StringBuilder buffer = new StringBuilder();
	private final ArrayList<String> signatureValuesList = new ArrayList<String>();
	
	
	@Override
	public String toString() {
		return buffer.toString();
	}

	public List<String> toList() {
		return signatureValuesList;
	}
	
	@Override
	public void chop(int numberOfCharacters) {

	}

	@Override
	public void print(Object o) {
		// TODO Auto-generated method stub

	}

	@Override
	public void println() {
		// TODO Auto-generated method stub

	}

	@Override
	public void println(Object o) {
		// TODO Auto-generated method stub

	}

	@Override
	public void printdyn(Object o) {
		//buffer.append(o);
		signatureValuesList.add(o.toString());
	}

	@Override
	public void prinx(Object o) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSpaces(int howMany) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContentType(String name) throws EglRuntimeException {
		// TODO Auto-generated method stub

	}

	@Override
	public String preserve(String id, boolean enabled, String contents)
			throws EglRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String preserve(String startComment, String endComment, String id,
			boolean enabled, String contents) throws EglRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String startPreserve(String id, boolean enabled)
			throws EglRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String startPreserve(String startComment, String endComment,
			String id, boolean enabled) throws EglRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String stopPreserve() throws EglRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stop() throws EglStoppedException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCurrentLineNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCurrentColumnNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void formatWith(Formatter formatter) {
		// TODO Auto-generated method stub

	}

}

