package com.example;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class StreamsWithoutReactor {

	@Test
	public void uselessSubscription() {

		// no demand generated.
		TestContent.getFinitePublisher().subscribe(new Subscriber<String>() {
			public void onSubscribe(Subscription subscription) {
				System.out.println("onSubscribe: " + subscription);
			}

			public void onNext(String s) {
				System.out.println("onNext: " + s);
			}

			public void onError(Throwable throwable) {
				System.out.println("onError: " + throwable.getMessage());
			}

			public void onComplete() {
				System.out.println("onComplete.");
			}
		});

	}


	@Test
	public void twoItemDemandAndBust() {

		// demand of 2 generated on request and... then nothing happens. The rest of the stream is never read.
		TestContent.getFinitePublisher().subscribe(new Subscriber<String>() {
			public void onSubscribe(Subscription subscription) {
				System.out.println("onSubscribe: " + subscription);

				subscription.request(2);
			}

			public void onNext(String s) {
				System.out.println("onNext: " + s);
			}

			public void onError(Throwable throwable) {
				System.out.println("onError: " + throwable.getMessage());
			}

			public void onComplete() {
				System.out.println("onComplete.");
			}
		});

	}

	@Test
	public void drainAllItemsOneAtATime() {

		// no demand generated.
		TestContent.getFinitePublisher().subscribe(new Subscriber<String>() {

			private Subscription mySubscription;

			public void onSubscribe(Subscription subscription) {
				System.out.println("onSubscribe: " + subscription);

				this.mySubscription = subscription;

				subscription.request(1);
			}

			public void onNext(String s) {
				System.out.println("onNext: " + s);

				// every time you process an element, request the next one from the subscription.
				this.mySubscription.request(1);
			}

			public void onError(Throwable throwable) {
				System.out.println("onError: " + throwable.getMessage());
			}

			// but who called this?! (reactor-core! because the test data is internally a Flux).
			// behold: https://github.com/reactor/reactor-core/blob/2a99fc678b34fc490282bb1f6762cccfd8dfbd4d/reactor-core/src/main/java/reactor/core/publisher/FluxArray.java#L305
			// This, btw, is why you don't roll your own reactor streams implementation. Devil, meet details.
			public void onComplete() {
				System.out.println("onComplete.");
			}
		});

	}


}
