package tech.pegasys.poc.rsaacc;


import java.math.BigInteger;

// Read: https://blog.goodaudience.com/deep-dive-on-rsa-accumulators-230bc84144d9

public class RsaAcc2 {
  // The modulus must be a number "with an unknown prime factorization. That is because all
  // proofs provided are in the generic group model for groups of unknown order, and require
  // the Strong RSA Assumption and the Adaptive Root Assumption."
  // https://rsa.cash/rsa-assumptions/
  //
  //
  // Use a well known composite that the prime factors aren't known. Given it is a well known
  // modulus, maybe it would be a good candidate.1
  //https://en.wikipedia.org/wiki/RSA_numbers#RSA-2048
  public static final BigInteger MODULUS = new BigInteger(
    "2519590847565789349402718324004839857142928212620403202777713783604366202070" +
    "7595556264018525880784406918290641249515082189298559149176184502808489120072" +
    "8449926873928072877767359714183472702618963750149718246911650776133798590957" +
    "0009733045974880842840179742910064245869181719511874612151517265463228221686" +
    "9987549182422433637259085141865462043576798423387184774447920739934236584823" +
    "8242811981638150106748104516603773060562016196762561338441436038339044149526" +
    "3443219011465754445417842402092461651572335077870774981712577246796292638635" +
    "6373289912154831438167899885040445364023527381951378636564391212010397122822" +
    "120720357");

  private BigInteger accumulator;

  // TODO This might have to be a random large prime.
  // It must be a non-zero, non-one value.
  private static final BigInteger GENERATOR = BigInteger.valueOf(65537);



  public RsaAcc2() {
    this.accumulator = GENERATOR;
  }

  public void add(BigInteger prime) {
    // If remove function implemented, then need to check that primes haven't been added twice
    this.accumulator = this.accumulator.modPow(prime, MODULUS);
  }

  public void remove(BigInteger prime) {

    // TODO how to have anotherAccumulator == this.accumulator
    BigInteger anotherAccumulator = this.accumulator.modPow(prime, MODULUS);
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