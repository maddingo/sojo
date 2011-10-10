package test.net.sf.sojo.common;

import net.sf.sojo.common.WalkerInterceptor;

public class TestWalkerInterceptor implements WalkerInterceptor {
	
	private Object whenThisObjectThanCanelWalk = null;
	
	public TestWalkerInterceptor(Object pvWhenThisObjectThanCanelWalk) {
		whenThisObjectThanCanelWalk = pvWhenThisObjectThanCanelWalk;
	}

	public void startWalk(Object pvStartObject) { }
	
	public void endWalk() { }

	public boolean visitElement(Object pvKey, int pvIndex, Object pvValue, int pvType, String pvPath, int pvNumberOfRecursion) {
		if (whenThisObjectThanCanelWalk != null && whenThisObjectThanCanelWalk.equals(pvValue)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void visitIterateableElement(Object pvValue, int pvType, String pvPath, int pvBeginEnd) {
		
	}


	public Object getWhenThisObjectThanCanelWalk() {
		return whenThisObjectThanCanelWalk;
	}
}
