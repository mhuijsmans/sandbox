package org.mahu.proto.mockitotest;

public class BookImpl implements Book {

	private final Author author;

	public BookImpl(final Author author) {
		this.author = author;
	}

	@Override
	public boolean isAuthorAlive() {
		return author.isAlive();
	}

	@Override
	public int authorAge() {
		return author.age();
	}

}
