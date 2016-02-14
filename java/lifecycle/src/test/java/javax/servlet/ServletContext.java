package javax.servlet;

public interface ServletContext {

	void setAttribute(String name, Object Object);
	
	Object getAttribute(String name);
}
