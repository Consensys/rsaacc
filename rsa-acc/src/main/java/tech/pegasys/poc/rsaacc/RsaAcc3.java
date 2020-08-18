package tech.pegasys.poc.rsaacc;


import java.math.BigInteger;
import java.security.SecureRandom;

// Read: https://blog.goodaudience.com/deep-dive-on-rsa-accumulators-230bc84144d9

public class RsaAcc3 {


  private BigInteger accumulator;

  // TODO This might have to be a random large prime.
  // It must be a non-zero, non-one value.
  private static final BigInteger GENERATOR = BigInteger.valueOf(65537);
  public static BigInteger MODULUS;
  public static BigInteger PHI_N;

  public static BigInteger p;
  public static BigInteger q;

  public RsaAcc3() {
    // TODO Fix initialization of SecureRandom
    SecureRandom rand = new SecureRandom();
    p = BigInteger.probablePrime(128, rand);
    q = BigInteger.probablePrime(128, rand);
    MODULUS = p.multiply(q);
    PHI_N = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

    this.accumulator = GENERATOR;
  }

  public void add(BigInteger prime) {
    // If remove function implemented, then need to check that primes haven't been added twice
    this.accumulator = this.accumulator.modPow(prime, MODULUS);
  }

  public void remove(BigInteger prime) {
    BigInteger modInv = prime.modInverse(PHI_N);
    this.accumulator = this.accumulator.modPow(modInv, MODULUS);
  }


  /**
   * Work out if the prime has been added.
   *
   * @param prime The prime to check.
   * @param proof The accumulator, without the prime combined into it.
   * @return true if the prime is part of the accumulator.
   */
  public boolean isMemberUsingMembershipProof(BigInteger prime, BigInteger proof) {
    if (proof.compareTo(BigInteger.ZERO) == 0) {
      throw new RuntimeException("Proof can't be zero");
    }
    return proof.modPow(prime, MODULUS).compareTo(this.accumulator) == 0;
  }


//  public boolean isNonMemberUsingNonMembershipProof(BigInteger prime, BigInteger[] proof) {
//    BigInteger val1 = proof[0].modPow(prime, MODULUS);
//    BigInteger val2 = this.accumulator.modPow(proof[1], MODULUS);
//    BigInteger check = val1.multiply(val2).mod(MODULUS);
//    return check.compareTo(GENERATOR) == 0;
//  }


  void setAccumulator(BigInteger acc) {
    this.accumulator = acc;
  }

  BigInteger getAccumulator() {
    return this.accumulator;
  }
}