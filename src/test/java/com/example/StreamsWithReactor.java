package com.example;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

// let's do the same thing now, but without living like medieval peasants, milling our own grain.
public class StreamsWithReactor {

	@Test
	public void uselessSubscription() {

		TestContent.getFiniteFlux()
			.doOnNext(s -> System.out.println("onNext: " + s));

		// uh... nothing happened! I want my money back!
		// no demand generated because there is no subscription.
		// Like that one time when I spent two hours troubleshooting up and down the stack
		// because I forgot to subscribe on an Angular2 HttpClient observable. Don't be like me.
	}

	@Test
	public void subscribeForRealUnlimitedDemand() {

		TestContent.getFiniteFlux()
			.doOnNext(s -> System.out.println("onNext: " + s))
			.subscribe();

	}

	// this is it. That's all we need to do to reproduce behavior in StreamsWithoutReactor.drainAllItemsOneAtATime().

	// now let's see what Reactor actually does for us behind the scenes.

	@Test
	public void subscribeForRealUnlimitedDemandWithExtraLogging() {

		TestContent.getFiniteFlux()
			.doOnSubscribe(s -> System.out.println("onSubscribe: " + s))
			.doOnRequest(numRequested -> System.out.println("subscription onRequest: " + numRequested))
			.doOnComplete(() -> System.out.println("onComplete"))

			.doOnNext(s -> System.out.println("onNext: " + s))
			.subscribe();

		// About that 9223372036854775807... That's unbounded demand, represented as Long.MAX_VALUE.
	}


	// TBD play with demand

	@Test
	public void whatIfYouJustWantTwoItems() {

		TestContent.getFiniteFlux()
			.limitRequest(2)
			.doOnNext(s -> System.out.println("onNext: " + s))
			.subscribe();

		// By the way, that `doOnNext` is a side effect. A more typical usage is to put logic in `subscribe()`.
	}

	@Test
	public void whatIfYouJustWantTwoItemsWithSubscribeLogic() {

		TestContent.getFiniteFlux()
			.limitRequest(2)
			.subscribe(s -> System.out.println("onNext: " + s));

		// By the way, that `doOnNext` is a side effect. A more typical usage is to put logic in `subscribe()`.
	}




}
