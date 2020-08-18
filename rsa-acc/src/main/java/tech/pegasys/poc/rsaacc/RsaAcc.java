package tech.pegasys.poc.rsaacc;


import java.math.BigInteger;

// Read: https://blog.goodaudience.com/deep-dive-on-rsa-accumulators-230bc84144d9

public class RsaAcc {
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

  private BigInteger product;

  // TODO This might have to be a random large prime.
  // It must be a non-zero, non-one value.
  private static final BigInteger GENERATOR = BigInteger.valueOf(65537);



  public RsaAcc() {
    this.accumulator = GENERATOR;
    this.product = BigInteger.ONE;
  }

  public void add(BigInteger prime) {
    // If remove function implemented, then need to check that primes haven't been added twice
    this.accumulator = this.accumulator.modPow(prime, MODULUS);
    this.product = this.product.multiply(prime);
  }

  public void remove(BigInteger prime) {
    this.product = this.product.divide(prime);
    this.accumulator = GENERATOR.modPow(this.product, MODULUS);

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


  public boolean isNonMemberUsingNonMembershipProof(BigInteger prime, BigInteger[] proof) {
    BigInteger val1 = proof[0].modPow(prime, MODULUS);
    BigInteger val2 = this.accumulator.modPow(proof[1], MODULUS);
    BigInteger check = val1.multiply(val2).mod(MODULUS);
    return check.compareTo(GENERATOR) == 0;
  }



  /**
   * Create a proof that can be given to isMemberUsingProveMembership
   * @param prime The prime to prove.
   * @return 0 if the prime is not part of the accumulator, otherwise the proof.
   */
  public BigInteger createMembershipProof(BigInteger prime) {
    BigInteger[] quotientAndRemainer = this.product.divideAndRemainder(prime);
    if (quotientAndRemainer[1].compareTo(BigInteger.ZERO) != 0) {
      System.out.println(" *****Error: the prime is not part of the accumulator. Can't create proof");
      return BigInteger.ZERO;
    }
    BigInteger productWithoutPrime = quotientAndRemainer[0];
    return GENERATOR.modPow(productWithoutPrime, MODULUS);
  }

  public BigInteger[] createNonMembershipProof(BigInteger prime) {
    BigInteger[] gcdArgs = new BigInteger[] {
        prime,
        this.product,
    };

    BigInteger[] bezoutCoefficients = new BigInteger[2];
    BigInteger gcd = ExtendedGCD.extendedGCD(gcdArgs, bezoutCoefficients);

    if (gcd.compareTo(BigInteger.ONE) != 0) {
      System.out.println(" *****Error: the prime is part of the accumulator. Can't create proof");
    }

    System.out.println();
    System.out.println("  GCD: "+gcd.toString());
    for (int i = 0; i < bezoutCoefficients.length; i++) {
      System.out.println("  Bezout Coefficients: [" + i + "]: " + bezoutCoefficients[i].toString());
    }
    BigInteger temp1 = bezoutCoefficients[0].multiply(prime);
    BigInteger temp2 = bezoutCoefficients[1].multiply(this.product);
    BigInteger temp3 = temp1.add(temp2);
    System.out.println("  Temp 1: " + temp1);
    System.out.println("  Temp 2: " + temp2);
    System.out.println("  Temp 3: " + temp3);

    BigInteger[] proof = new BigInteger[2];
    proof[0] = GENERATOR.modPow(bezoutCoefficients[0], MODULUS);
    proof[1] = bezoutCoefficients[1];
    return proof;
  }



  BigInteger getAccumulator() {
    return this.accumulator;
  }
  BigInteger getProduct() {
    return this.product;
  }

}