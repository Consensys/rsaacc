package tech.pegasys.poc.rsaacc;

import java.math.BigInteger;

// https://stackoverflow.com/questions/59536376/finding-bezout-coefficients-via-extended-euclidean-algorithm-on-array-of-arbitra
//find the smallest non-zero element of the array
//    compute the remainders for all other elements using the smallest element as the divisor
//    recursively call the function until only one non-zero element remains
//    that element is the gcd
//    the coefficient for that element starts at 1
//    all other coefficients start at 0
//    on the way back up the call stack, update the coefficient of the smallest element as follows
//    for each element of the array that was reduced
//    compute the difference between the original value and the reduced value
//    divide by the smallest element (the difference is guaranteed to be a multiple of the smallest value)
//    multiply by the coefficient for the reduced element
//    subtract the result from the coefficient of the smallest element

public class ExtendedGCD {

  public static void main(String[] args) {
    BigInteger[] gcdArgs = new BigInteger[] {
        BigInteger.valueOf(550),
        BigInteger.valueOf(420),
    //    BigInteger.valueOf(3515)
    };
    BigInteger[] bezoutCoefficients = new BigInteger[2];
    BigInteger gcd = ExtendedGCD.extendedGCD(gcdArgs, bezoutCoefficients);
    System.out.println("GCD: "+gcd.toString());
    System.out.println("Bezout Coefficients: ");
    for (int i = 0; i < bezoutCoefficients.length; i++) {
      System.out.print(" "+bezoutCoefficients[i].toString());
    }
  }

  /**
   * Calculate the extended GCD
   *
   * @param gcdArgs
   *            an array of positive BigInteger numbers
   * @param bezoutsCoefficients
   *            returns the Bezout Coefficients
   * @return
   */
  public static BigInteger extendedGCD(final BigInteger[] gcdArgs, BigInteger[] bezoutsCoefficients) {
    BigInteger factor;
    BigInteger gcd = gcdArgs[0];
    Object[] stepResult = extendedGCD(gcdArgs[1], gcd);

    gcd = (BigInteger) stepResult[0];
    bezoutsCoefficients[0] = ((BigInteger[]) stepResult[1])[0];
    bezoutsCoefficients[1] = ((BigInteger[]) stepResult[1])[1];

    for (int i = 2; i < gcdArgs.length; i++) {
      stepResult = extendedGCD(gcdArgs[i], gcd);
      gcd = (BigInteger) stepResult[0];
      factor = ((BigInteger[]) stepResult[1])[0];
      for (int j = 0; j < i; j++) {
        bezoutsCoefficients[j] = bezoutsCoefficients[j].multiply(factor);
      }
      bezoutsCoefficients[i] = ((BigInteger[]) stepResult[1])[1];
    }
    return gcd;
  }

  /**
   * Returns the gcd of two positive numbers plus the bezout relation
   */
  public static Object[] extendedGCD(BigInteger numberOne, BigInteger numberTwo) throws ArithmeticException {
    Object[] results = new Object[2];
    BigInteger dividend;
    BigInteger divisor;
    BigInteger quotient;
    BigInteger remainder;
    BigInteger xValue;
    BigInteger yValue;
    BigInteger tempValue;
    BigInteger lastxValue;
    BigInteger lastyValue;
    BigInteger gcd = BigInteger.ONE;
    BigInteger mValue = BigInteger.ONE;
    BigInteger nValue = BigInteger.ONE;
    boolean exchange;

    remainder = BigInteger.ONE;
    xValue = BigInteger.ZERO;
    lastxValue = BigInteger.ONE;
    yValue = BigInteger.ONE;
    lastyValue = BigInteger.ZERO;
    if ((!((numberOne.compareTo(BigInteger.ZERO) == 0) || (numberTwo.compareTo(BigInteger.ZERO) == 0)))
        && (((numberOne.compareTo(BigInteger.ZERO) == 1) && (numberTwo.compareTo(BigInteger.ZERO) == 1)))) {
      if (numberOne.compareTo(numberTwo) == 1) {
        exchange = false;
        dividend = numberOne;
        divisor = numberTwo;
      } else {
        exchange = true;
        dividend = numberTwo;
        divisor = numberOne;
      }

      BigInteger[] divisionResult = null;
      while (remainder.compareTo(BigInteger.ZERO) != 0) {
        divisionResult = dividend.divideAndRemainder(divisor);
        quotient = divisionResult[0];
        remainder = divisionResult[1];

        dividend = divisor;
        divisor = remainder;

        tempValue = xValue;
        xValue = lastxValue.subtract(quotient.multiply(xValue));
        lastxValue = tempValue;

        tempValue = yValue;
        yValue = lastyValue.subtract(quotient.multiply(yValue));
        lastyValue = tempValue;
      }

      gcd = dividend;
      if (exchange) {
        mValue = lastyValue;
        nValue = lastxValue;
      } else {
        mValue = lastxValue;
        nValue = lastyValue;
      }
    } else {
      throw new ArithmeticException("ExtendedGCD contains wrong arguments");
    }
    results[0] = gcd;
    BigInteger[] values = new BigInteger[2];
    values[0] = nValue;
    values[1] = mValue;
    results[1] = values;
    return results;
  }
}
