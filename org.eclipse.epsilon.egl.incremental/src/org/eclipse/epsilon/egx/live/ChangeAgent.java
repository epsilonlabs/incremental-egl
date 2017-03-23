package org.eclipse.epsilon.egx.live;

import java.util.ArrayList;

public class ChangeAgent {
	
	private ArrayList<ChangeData> changeData = new ArrayList<ChangeData>();
	
	public ArrayList<ChangeData> getChanges() {
		return changeData;
	}
	
	public void updateChangeData(ChangeData change) {
		if(!changeData.contains(change))
			changeData.add(change);
	}

}
