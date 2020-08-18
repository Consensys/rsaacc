package tech.pegasys.poc.rsaacc;

import java.math.BigInteger;
import java.util.Date;

public class MultiAccumulator2 {
  public static int NUM_PRIMES = 3;
//  public static int NUM_PRIMES = 100;
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
//    num = num.pow(253);
//    num = num.subtract(BigInteger.TEN);
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

    System.out.println("Add primes to all accumulators");
    start = System.nanoTime();
    for (int i = 0; i < NUM_PRIMES; i++) {
      mainAccumulator.add(primes[i]);
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

      // Determine Bezout Coefficients.
      BigInteger[] gcdArgs = new BigInteger[] {
          primeToRemove,
          primes[i],
      };
      BigInteger[] bezoutCoefficients = new BigInteger[2];
      BigInteger gcd = ExtendedGCD.extendedGCD(gcdArgs, bezoutCoefficients);
      if (gcd.compareTo(BigInteger.ONE) != 0) {
        System.out.println(" *****Error: the prime is part of the primes[i]...? Can't create proof");
        throw new Error("here");
      }
//      System.out.println();
//      System.out.println("  GCD: "+gcd.toString());
//      for (int j = 0; j < bezoutCoefficients.length; j++) {
//        System.out.println("  Bezout Coefficients: [" + j + "]: " + bezoutCoefficients[j].toString());
//      }

      BigInteger t1 = primeToRemove.multiply(bezoutCoefficients[0]);
      BigInteger t2 = primes[i].multiply(bezoutCoefficients[1]);
      BigInteger t3 = t1.add(t2);
      BigInteger t4 = t3.mod(RsaAcc2.MODULUS);
      System.out.println("Checking Bezout Coefficients: check value (a*x + b*y mod N): " + t4);


      // Calculate w1**b * w2**a
      BigInteger temp1 = accOfPrimeToRemove.modPow(bezoutCoefficients[0], RsaAcc2.MODULUS);
      BigInteger temp2 = witnessAccumulators[i].getAccumulator().modPow(bezoutCoefficients[1], RsaAcc2.MODULUS);
      BigInteger temp3 = temp1.multiply(temp2);
      BigInteger temp4 = temp3.mod(RsaAcc2.MODULUS);
      witnessAccumulators[i].setAccumulator(temp4);
    }
    end = System.nanoTime();
    System.out.println("Duration for removing a prime from all accumulators: " + (end - start)/1000000 + " ms");

    mainAccumulator = witnessAccumulators[indexOfPrimeToRemove];

    proveMembership(mainAccumulator, witnessAccumulators, 0);
    proveMembership(mainAccumulator, witnessAccumulators, 1);



    BigInteger five = BigInteger.valueOf(5);
    BigInteger n = BigInteger.valueOf(23);
    BigInteger modInv = five.modInverse(n);
    System.out.println("modInv: " + modInv);
    BigInteger gen = BigInteger.valueOf(3);


    BigInteger t1 = five.multiply(modInv);
    BigInteger t2 = gen.modPow(t1, n);
    System.out.println("t2: " + t2);
    BigInteger t3 = gen.modPow(five, n);
    BigInteger t4 = t3.modPow(modInv, n);
    System.out.println("t4: " + t4);
    BigInteger t5 = five.multiply(modInv);
    BigInteger t6 = t5.mod(n);
    System.out.println("t6: " + t6);

    BigInteger t7 = gen.modPow(n.add(BigInteger.ONE), n);
    System.out.println("t7: " + t7);



    System.out.println();
    System.out.println(" Date: " + (new Date().toString()));
    System.out.println("Test: End");


  }


  public static void proveMembership(RsaAcc3 mainAcc, RsaAcc3[] witnessAccs, int indexOfPrime) {
    System.out.println("Check membership for prime[" + indexOfPrime + "]: " + primes[indexOfPrime]);
    System.out.println(" Proof correct: " + mainAcc.isMemberUsingMembershipProof(primes[indexOfPrime], witnessAccs[indexOfPrime].getAccumulator()));
  }
}
