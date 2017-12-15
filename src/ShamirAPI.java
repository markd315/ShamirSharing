import java.math.BigInteger;
import java.util.Random;

public class ShamirAPI {
	static BigInteger prime = new BigInteger(
			"1040793219466439908192524032736408553861526224726670480531911235040360805967336029801223944173232418"
					+ "4842421613954281007791383566248323464908139906605677320762924129509389220345773183349661583550472959"
					+ "4205476898112116936771475484788669625013844382602917323488853111608285384165850282556046662248318909"
					+ "18801847068222203140521026698435488732958028878050869736186900714720710555703168729087");
	// 386 decimal digits == 1278 bits of random number generation. Perfectly
	// sufficient.

	/* Split number into the shares */
	public static BigInteger[] split(BigInteger number, int available, int needed) {
		BigInteger[] coef = new BigInteger[needed]; // 2 points define a line: takes ax +b, 3 points define a parabola:
													// takes ax^2 + bx + c
		coef[0] = number;

		for (int c = 1; c < needed; c++)
			coef[c] = new BigInteger(1024, new Random());
		int x, exp, c;
		BigInteger y;
		BigInteger[] shares = new BigInteger[available * 2];
		/*
		 * Normally, we use the line:
		 * 
		 * where (prime - 1) is the maximum allowable value. However, to follow this
		 * example, we hardcode the values: coef = [number, 166, 94]; For production,
		 * replace the hardcoded value with the random loop For each share that is
		 * requested to be available, run through the formula plugging the corresponding
		 * coefficient The result is f(x), where x is the byte we are sharing (in the
		 * example, 1234)
		 */
		for (x = 0; x < 2 * available;) {
			/* coef = [1234, 166, 94] which is 1234x^0 + 166x^1 + 94x^2 */
			y = coef[0]; // Set the y intercept (secret key)
			for (exp = 1; exp < needed; exp++) {
				int xpow = (int) Math.pow(x - 1, exp);// We want odd x values to avoid the x=0 case which would just
														// reveal our y-intercept in share index 0.
				y = coef[exp].multiply(BigInteger.valueOf(xpow)).add(y);// y+= coef * (x^exp)
			}
			/*
			 * Store values as tuples like (1, 1155), (2, 781), (3, 112), (4, 385), (5, 363)
			 * (6, 46)
			 */
			System.out.println("wrote to (and +1) " + x);
			shares[x] = BigInteger.valueOf(x - 1); // We should make sure we accurately store the x value we calculated.
			shares[x + 1] = y;
			x += 2;
		}
		return shares;
	}

	/*
	 * Gives the decomposition of the gcd of a and b. Returns [x,y,z] such that x =
	 * gcd(a,b) and y*a + z*b = x
	 */
	public static BigInteger[] gcdD(BigInteger a, BigInteger b) {
		if (b.equals(BigInteger.ZERO)) {
			BigInteger[] re = new BigInteger[3];
			re[0] = a;
			re[1] = BigInteger.ONE;
			re[2] = BigInteger.ZERO;
			return re;
		} else {
			BigInteger n = a.divide(b);
			BigInteger c = a.mod(b);
			BigInteger[] r = gcdD(b, c);
			BigInteger[] re = new BigInteger[3];
			re[0] = r[0];
			re[1] = r[2];
			re[2] = r[1].subtract(r[2].multiply(n));
			return re;
		}
	}

	/*
	 * Gives the multiplicative inverse of k mod prime. In other words (k *
	 * modInverse(k)) % prime = 1 for all prime > k >= 1
	 */
	public static BigInteger modInverse(BigInteger k) {
		return modInverse(k, prime);
	}

	/*
	 * Gives the multiplicative inverse of k mod prime. In other words (k *
	 * modInverse(k)) % prime = 1 for all prime > k >= 1
	 */
	public static BigInteger modInverse(BigInteger k, BigInteger prime) {
		k = k.mod(prime); // k%=prime
		BigInteger r = (k.compareTo(BigInteger.ZERO) < 0) ? gcdD(prime, k.negate())[2].negate() : gcdD(prime, k)[2];
		// x.compareTo(y) positive for x>y, negative for x<y
		return (prime.add(r)).mod(prime);
	}

	/*
	 * Returns a coefficient vector representing the xy points passed in.
	 */
	public static double lagrangeInterpolate(double[] x, double[] y) {
		int a = 0;// We are solving for the y-intercept
		double sum=0;
		for (int i = 0; i < x.length; i++) {
			int mult = 1;
			for (int j = 0; j < x.length; j++) {
				if (j != i)
					mult *= (a - x[j]) / (x[i] - x[j]);
			}
			sum += mult * y[i];
		}
		return sum;
	}

	/* Join the shares into a number */
	public static BigInteger join(BigInteger[] shares) {
		/*// return the Y intercept of the lagrange polynomial
		BigInteger[] x = new BigInteger[shares.length / 2];
		BigInteger[] y = new BigInteger[shares.length / 2];
		for (int i = 0; i < shares.length; i += 2) {
			x[i / 2] = shares[i];
			y[i / 2] = shares[i + 1];
		}
		return x[0];//lagrangeInterpolate(x, y)[0];*/
		BigInteger accum = BigInteger.ZERO, startposition, nextposition, numerator, denominator, value;
		int count;
	    for(int formula = 0; formula < shares.length; formula++) {
	        /* Multiply the numerator across the top and denominators across the bottom to do Lagrange's interpolation
	         * Result is x0(2), x1(4), x2(5) -> -4*-5 and (2-4=-2)(2-5=-3), etc for l0, l1, l2...
	         */
	    	numerator = denominator = BigInteger.ONE;
	        for(count = 0; count < shares.length; count++) {
	            if(formula == count) continue; // If not the same value
	            startposition = shares[formula];
	            nextposition = shares[count];
	            numerator = nextposition.negate().multiply(numerator).mod(prime); //(numerator * -nextposition) % prime;
	            denominator = startposition.subtract(nextposition).multiply(denominator).mod(prime); //(denominator * (startposition - nextposition)) % prime;
	        }
	        value = shares[formula];
	        accum = modInverse(denominator).multiply(value).multiply(numerator).add(prime).add(accum).mod(prime); //(prime + accum + (value * numerator * modInverse(denominator))) % prime;
	    }
	    return accum;
		
	}
}
