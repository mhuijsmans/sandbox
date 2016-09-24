package org.mahu.rxjava;

import rx.Observable;

public class App {

    public static void main(String[] args) {
        Observable.just("Hello, world!").map(s -> s + " -Dan").subscribe(s -> System.out.println(s));
        Observable.just("Hello, world!").map(s -> s.hashCode()).map(i -> Integer.toString(i))
                .subscribe(s -> System.out.println(s));
    }
}
