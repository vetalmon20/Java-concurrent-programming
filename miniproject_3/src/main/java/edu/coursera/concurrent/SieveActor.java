package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;

import java.util.stream.IntStream;

import static edu.rice.pcdp.PCDP.finish;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     *
     *
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        SieveActorActor[] actorOuter = new SieveActorActor[1];
        finish(() -> {
            actorOuter[0] = new SieveActorActor(3);
            IntStream.range(4, limit).filter(i -> i % 2 == 1).forEach(actorOuter[0]::send);
        });

        SieveActorActor actorInner = actorOuter[0];
        int result = 1;

        while(actorInner != null){
            actorInner = actorInner.nextActor;
            result++;
        }
        return result;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        private SieveActorActor nextActor;
        private final int prime;

        SieveActorActor(final int prime){
            this.prime = prime;
        }

        /**
         * Process a single message sent to this actor.
         *
         *
         * @param msg Received message
         */
        @Override
        public void process(final Object msg) {
            int candidate = (Integer) msg;
            if (candidate % prime != 0) {
                if (nextActor == null)
                    nextActor = new SieveActorActor(candidate);
                else
                    nextActor.send(msg);
            }
        }
    }
}
