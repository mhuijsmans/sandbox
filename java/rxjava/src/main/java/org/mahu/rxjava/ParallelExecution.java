package org.mahu.rxjava;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ParallelExecution {

    private final static ExecutorService executor = Executors.newFixedThreadPool(2);
    // catching exception extend class and override protected void
    // afterExecute(Runnable r, Throwable t)
    // https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ThreadPoolExecutor.html#afterExecute(java.lang.Runnable,%20java.lang.Throwable)

    // source:
    // http://stackoverflow.com/questions/26249030/rxjava-fetching-observables-in-parallel
    // works like a charm

    public static void main(String[] args) {
        System.out.println("------------ mergingAsync");
        mergingAsync();
        System.out.println("------------ mergingSync");
        mergingSync();
        System.out.println("------------ mergingSyncMadeAsync");
        mergingSyncMadeAsync();
        System.out.println("------------ flatMapExampleSync");
        flatMapExampleSync();
        System.out.println("------------ flatMapExampleAsync1");
        flatMapExampleAsync1();
        System.out.println("------------ flatMapExampleAsync2");
        flatMapExampleAsync2();
        System.out.println("------------ flatMapExampleAsync3 (ExecutorService)");
        flatMapExampleAsync3();
        System.out.println("------------ trampolineExample");
        trampolineExample();
        System.out.println("------------");
    }

    private static void trampolineExample() {
        Action1<Integer> onNext = new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                // This will execute in the main thread
                System.out.println(Thread.currentThread().getName() + "-number=" + integer);
            }
        };
        Observable.just(2, 4, 6, 8, 10).subscribeOn(Schedulers.trampoline()).subscribe(onNext);
        Observable.just(1, 3, 5, 7, 9).subscribeOn(Schedulers.trampoline()).subscribe(onNext);
    }

    private static void mergingAsync() {
        Observable.merge(getDataAsync(1), getDataAsync(2)).map(s -> "Dan-" + s).toBlocking()
                .forEach(System.out::println);
    }

    private static void mergingSync() {
        // here you'll see the delay as each is executed synchronously
        Observable.merge(getDataSync(1), getDataSync(2)).toBlocking().forEach(System.out::println);
    }

    private static void mergingSyncMadeAsync() {
        // if you have something synchronous and want to make it async, you can
        // schedule it like this so here we see both executed concurrently
        Observable.merge(getDataSync(1).subscribeOn(Schedulers.io()), getDataSync(2).subscribeOn(Schedulers.io()))
                .toBlocking().forEach(System.out::println);
    }

    private static void flatMapExampleAsync1() {
        // flatmap: http://reactivex.io/documentation/operators/flatmap.html
        Observable.range(0, 5).flatMap(i -> {
            return getDataAsync(i);
        }).toBlocking().forEach(System.out::println);
    }

    private static void flatMapExampleAsync2() {
        // Using method reference to put results in object
        // ref:
        // https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
        StringBuilder sb = new StringBuilder();
        Observable.range(0, 5).flatMap(i -> {
            // This will execute in other thread
            return getDataAsync(i);
        }).map(s -> s + "\n").toBlocking().forEach(sb::append);
        System.out.println(sb.toString());
    }

    private static void flatMapExampleAsync3() {
        // flatmap: http://reactivex.io/documentation/operators/flatmap.html
        Observable.range(0, 5).flatMap(i -> {
            return getDataAsync(i, executor);
        }).toBlocking().forEach(System.out::println);
    }

    private static void flatMapExampleSync() {
        // This will execute in the main thread
        Observable.range(0, 5).flatMap(i -> {
            // This will execute in the main thread
            return getDataSync(i);
        }).toBlocking().forEach(System.out::println);
    }

    // artificial representations of IO work
    static Observable<String> getDataAsync(int i) {
        return getDataSync(i).subscribeOn(Schedulers.io());
    }

    static Observable<String> getDataAsync(int i, ExecutorService executor) {
        return getDataSync(i).subscribeOn(Schedulers.from(executor));
    }

    static Observable<String> getDataSync(int i) {
        return Observable.create((Subscriber<? super String> s) -> {
            // simulate latency
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            s.onNext(Thread.currentThread().getName() + "-" + i);
            s.onCompleted();
        });
    }
}
