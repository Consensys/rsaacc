package tech.pegasys.poc.rsaacc;

import java.math.BigInteger;
import java.util.Date;

public class SimpleAccumulator {
//  public static int NUM_PRIMES = 3;
  public static int NUM_PRIMES = 100;
  public static BigInteger[] primes = new BigInteger[NUM_PRIMES];

  public static void main(String[] args) throws Exception {

    // Make stdout and stderr one stream. Have them both non-buffered.
    // What this means is that if an error or exception stack trace is thrown,
    // it will be shown in the context of the other output.
    System.setOut(System.err);

    System.out.println("Test: Start");
    System.out.println(" Date: " + (new Date().toString()));
    System.out.println();



    BigInteger num = BigInteger.valueOf(2);
    num = num.pow(253);
    num = num.subtract(BigInteger.TEN);
    long start = System.nanoTime();
    for (int i = 0; i < NUM_PRIMES; i++) {
      num = num.nextProbablePrime();
      primes[i] = num;
    }
    long end = System.nanoTime();
    System.out.println("Duration for finding " + NUM_PRIMES + " primes: " + (end - start)/1000000 + " ms");

    System.out.println("Create main and witness accumulators");
    RsaAcc3 mainAccumulator = new RsaAcc3();
    RsaAcc3[] witnessAccumulators = new RsaAcc3[NUM_PRIMES];
    for (int i = 0; i < NUM_PRIMES; i++) {
      witnessAccumulators[i] = new RsaAcc3();
    }

    System.out.println("Add primes to main accumulators");
    start = System.nanoTime();
    for (int i = 0; i < NUM_PRIMES; i++) {
      mainAccumulator.add(primes[i]);
    }
    end = System.nanoTime();
    System.out.println("Duration for accumulating "+ NUM_PRIMES + " primes: " + (end - start)/1000000 + " ms");

    System.out.println("Add primes to all witness accumulators");
    start = System.nanoTime();
    for (int i = 0; i < NUM_PRIMES; i++) {
      for (int j = 0; j < NUM_PRIMES; j++) {
        if (i != j) {
          witnessAccumulators[i].add(primes[j]);
        }
      }
    }
    end = System.nanoTime();
    System.out.println("Duration for accumulating "+ NUM_PRIMES * NUM_PRIMES + " primes: " + (end - start)/1000000 + " ms");

    proveMembership(mainAccumulator, witnessAccumulators, 0);
    proveMembership(mainAccumulator, witnessAccumulators, 1);

    // Remove the first prime.
    int indexOfPrimeToRemove = NUM_PRIMES - 1;
    BigInteger accOfPrimeToRemove = witnessAccumulators[indexOfPrimeToRemove].getAccumulator();
    BigInteger primeToRemove = primes[indexOfPrimeToRemove];
    System.out.println("Remove prime with index " + indexOfPrimeToRemove + ": " + primeToRemove);
    start = System.nanoTime();
    for (int i = 0; i < NUM_PRIMES; i++) {
      if (i == indexOfPrimeToRemove) {
        continue;
      }

      witnessAccumulators[i].remove(primeToRemove);
    }
    end = System.nanoTime();
    System.out.println("Duration for removing a prime from all accumulators: " + (end - start)/1000000 + " ms");

    mainAccumulator = witnessAccumulators[indexOfPrimeToRemove];

    proveMembership(mainAccumulator, witnessAccumulators, 0);
    proveMembership(mainAccumulator, witnessAccumulators, 1);

    System.out.println();
    System.out.println(" Date: " + (new Date().toString()));
    System.out.println("Test: End");
  }


  public static void proveMembership(RsaAcc3 mainAcc, RsaAcc3[] witnessAccs, int indexOfPrime) {
    System.out.println("Check membership for prime[" + indexOfPrime + "]: " + primes[indexOfPrime]);
    System.out.println(" Proof correct: " + mainAcc.isMemberUsingMembershipProof(primes[indexOfPrime], witnessAccs[indexOfPrime].getAccumulator()));
  }
}
