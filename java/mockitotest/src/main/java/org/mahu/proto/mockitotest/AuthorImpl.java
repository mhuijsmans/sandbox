package org.mahu.proto.mockitotest;

public class AuthorImpl implements Author {
	
	public static int AUTHOR_AGE = 100;
	
	private boolean isAlive=true;
	
	public AuthorImpl() {
		this.isAlive = true;
	}
	
	public AuthorImpl(final boolean isAlive) {
		this.isAlive = isAlive;
	}	

	@Override
	public boolean isAlive() {
		return isAlive;
	}

	@Override
	public int age() {
		return AUTHOR_AGE;
	}

}
