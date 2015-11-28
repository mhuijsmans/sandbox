package org.mahu.proto.restapp.engine;

class SessionIdImpl implements SessionId {

	private final Long id;
	private final SharedObject resultData;

	SessionIdImpl(long id) {
		this.id = new Long(id);
		resultData = new SharedObject();
	}

	void SetIsTerminated(final Object resultData) {
		this.resultData.Set(resultData);
	}

	@Override
	public String toString() {
		return String.valueOf(id);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj != null) && (obj instanceof SessionIdImpl)
				&& (id.longValue() == ((SessionIdImpl) obj).id.longValue());
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public Object WaitForCompletion() throws InterruptedException {
		return resultData.WaitForDataSet();
	}

}
