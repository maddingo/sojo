package test.net.sf.sojo.common;

import net.sf.sojo.common.WalkerInterceptor;

public class TestWalkerInterceptor implements WalkerInterceptor {
	
	private Object whenThisObjectThanCancelWalk = null;
	
	public TestWalkerInterceptor(Object pvWhenThisObjectThanCanelWalk) {
		whenThisObjectThanCancelWalk = pvWhenThisObjectThanCanelWalk;
	}

	@Override
	public void startWalk(Object pvStartObject) { }
	
	@Override
	public void endWalk() { }

	@Override
	public boolean visitElement(Object pvKey, int pvIndex, Object pvValue, int pvType, String pvPath, int pvNumberOfRecursion) {
		if (whenThisObjectThanCancelWalk != null && whenThisObjectThanCancelWalk.equals(pvValue)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void visitIterateableElement(Object pvValue, int pvType, String pvPath, int pvBeginEnd) {
		
	}


	public Object getWhenThisObjectThanCanelWalk() {
		return whenThisObjectThanCancelWalk;
	}
}
