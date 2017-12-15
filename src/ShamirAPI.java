import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class ShamirAPI {
	static BigInteger prime = new BigInteger(
			"1040793219466439908192524032736408553861526224726670480531911235040360805967336029801223944173232418" + 
			"4842421613954281007791383566248323464908139906605677320762924129509389220345773183349661583550472959" + 
			"4205476898112116936771475484788669625013844382602917323488853111608285384165850282556046662248318909" + 
			"18801847068222203140521026698435488732958028878050869736186900714720710555703168729087");
	//386 decimal digits == 1278 bits of random number generation. Perfectly sufficient.
	
	/* Split number into the shares */
	public static BigInteger[] split(BigInteger number, int available, int needed) {
		BigInteger[] coef = new BigInteger[needed]; //2 points define a line: takes ax +b, 3 points define a parabola: takes ax^2 + bx + c
	    //TODO Populate coef with number as LSB and random generated info for higher order.
		coef[0] = number;
		
		for(int c = 1; c < needed; c++)
			coef[c] = new BigInteger(1024, new Random());
			//TODO previous line will fail. Can't multiply double by bigint
	    int x, exp, c;
		BigInteger y;
	    BigInteger[] shares = new BigInteger[available * 2]; //TODO 
	    /* Normally, we use the line:
	     * 
	     * where (prime - 1) is the maximum allowable value.
	     * However, to follow this example, we hardcode the values:
	     * coef = [number, 166, 94];
	     * For production, replace the hardcoded value with the random loop
	     * For each share that is requested to be available, run through the formula plugging the corresponding coefficient
	     * The result is f(x), where x is the byte we are sharing (in the example, 1234)
	     */
	    for(x = 0; x < available;) {
	        /* coef = [1234, 166, 94] which is 1234x^0 + 166x^1 + 94x^2 */
	    	y = coef[0];
	    	for(exp = 1; exp < needed; exp++) {
	    		int xpow = (int) Math.pow(x, exp);
	        	y = coef[exp].multiply(BigInteger.valueOf(xpow)).add(y);// y+= coef * (x^exp)
	    	}
	        /* Store values as tuples like (1, 1155), (2, 781), (3, 112), (4, 385), (5, 363) (6, 46) */
	        shares[x] = BigInteger.valueOf(x);
	        shares[x+1] = y;
	        x+=2;
	    }
	    return shares;
	}

	/* Gives the decomposition of the gcd of a and b.  Returns [x,y,z] such that x = gcd(a,b) and y*a + z*b = x */
	public static BigInteger[] gcdD(BigInteger a, BigInteger b) { 
	    if (b.equals(BigInteger.ZERO)) { 
	    	BigInteger[] re = new BigInteger[3];
	    	re[0] = a;
	    	re[1] = BigInteger.ONE;
	    	re[2] = BigInteger.ZERO;
	    	return re; 
	    }
	    else { 
	        BigInteger n = a.divide(b);
	        BigInteger c = a.mod(b);
	        BigInteger[] r = gcdD(b,c); 
	        BigInteger[] re = new BigInteger[3];
	    	re[0] = r[0];
	    	re[1] = r[2];
	    	re[2] = r[1].subtract(r[2].multiply(n));
	        return re;
	    }
	}

	/* Gives the multiplicative inverse of k mod prime.  In other words (k * modInverse(k)) % prime = 1 for all prime > k >= 1  */
	public static BigInteger modInverse(BigInteger k) { 
	    return modInverse(k, prime);
	}
	
	/* Gives the multiplicative inverse of k mod prime.  In other words (k * modInverse(k)) % prime = 1 for all prime > k >= 1  */
	public static BigInteger modInverse(BigInteger k, BigInteger prime) { 
	    k = k.mod(prime); // k%=prime
	    BigInteger r = (k.compareTo(BigInteger.ZERO) < 0) ? gcdD(prime, k.negate())[2].negate() : gcdD(prime,k)[2];
	    //x.compareTo(y) positive for x>y, negative for x<y
	    return (prime.add(r)).mod(prime);
	}

	/* Join the shares into a number */
	public static BigInteger join(BigInteger[] shares) {
	    int formula, count, numerator, denominator;
		BigInteger accum, startposition, nextposition, value;
	    for(formula=0, accum = BigInteger.ZERO; formula < shares.length; formula++) {
	        /* Multiply the numerator across the top and denominators across the bottom to do Lagrange's interpolation
	         * Result is x0(2), x1(4), x2(5) -> -4*-5 and (2-4=-2)(2-5=-3), etc for l0, l1, l2...
	         */
	        for(count = 0, numerator = denominator = 1; count < shares.length; count++) {
	            if(formula == count) continue; // If not the same value
	            startposition = shares[formula][0];
	            nextposition = shares[count][0];
	            numerator = (numerator * -nextposition) % prime;
	            denominator = (denominator * (startposition - nextposition)) % prime;
	        }
	        value = shares[formula][1];
	        accum = (prime + accum + (value * numerator * modInverse(denominator))) % prime;
	    }
	    return accum;
	}
}
