package tech.pegasys.poc.rsaacc;

import java.math.BigInteger;
import java.util.Date;

public class FilecoinExample {

  public static void main(String[] args) throws Exception {

    // Make stdout and stderr one stream. Have them both non-buffered.
    // What this means is that if an error or exception stack trace is thrown,
    // it will be shown in the context of the other output.
    System.setOut(System.err);

    System.out.println("Test: Start");
    System.out.println(" Date: " + (new Date().toString()));
    System.out.println();

    int numPrimes = 1000;
    BigInteger[] primes = new BigInteger[numPrimes];

    BigInteger num = new BigInteger("100000000000000000000000000000000000000");
    for (int i  =0; i < numPrimes; i++) {
      num = num.nextProbablePrime();
      primes[i] = num;
    }


    BigInteger p1 = BigInteger.valueOf(23);

    RsaAcc acc = new RsaAcc();
    for (int i  =0; i < numPrimes; i++) {
      acc.add(primes[i]);
    }

    //System.out.println("Acc: " + acc.getAccumulator());
    System.out.println("Acc Size: " + acc.getAccumulator().bitLength() + " bits");
    //System.out.println("Product: " + acc.getProduct());
    System.out.println("Product Size: " + acc.getProduct().bitLength() + " bits");

    System.out.println("Acc1: " + acc.getAccumulator());
    acc.add(BigInteger.valueOf(7));
    System.out.println("Acc2: " + acc.getAccumulator());
    acc.remove(BigInteger.valueOf(7));
    System.out.println("Acc3: " + acc.getAccumulator());






    proveMembership(acc, primes[1]);
    proveNonMembership(acc, primes[1]);
    proveMembership(acc, p1);
    proveNonMembership(acc, p1);

    System.out.println();
    System.out.println(" Date: " + (new Date().toString()));
    System.out.println("Test: End");


  }


  public static void proveMembership(RsaAcc acc, BigInteger prime) {
    System.out.println("Check membership for prime: " + prime);
    BigInteger proof1 = acc.createMembershipProof(prime);
    if (proof1.compareTo(BigInteger.ZERO) == 0) {
      return;
    }
    System.out.println(" Prime is a member: " + acc.isMemberUsingMembershipProof(prime, proof1));
    System.out.println(" Proof size: " + proof1.bitLength());
    //System.out.println(" Proof: " + proof1);
  }
  public static void proveNonMembership(RsaAcc acc, BigInteger prime) {
    System.out.println("Check non-membership for prime: " + prime);
    BigInteger[] proof1 = acc.createNonMembershipProof(prime);
    System.out.println("Prime is NOT a member: " + acc.isNonMemberUsingNonMembershipProof(prime, proof1));
    System.out.println(" Proof[0] size: " + proof1[0].bitLength());
    System.out.println(" Proof[1] size: " + proof1[1].bitLength());
    System.out.println(" Proof[1]: " + proof1[1]);
  }



}
