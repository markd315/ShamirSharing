import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

class splitRejoinShouldReturnOriginal {

	@Test
	void test() {
		BigInteger[] shares = ShamirAPI.split(new BigInteger("123456"), 6, 3); /* split the secret value 1234 into 6 components - at least 3 of which will be needed to figure out the secret value */
		for(BigInteger share : shares)
			System.out.println(share);
		//var newshares = [sh[1], sh[3], sh[4]]; /* pick any selection of 3 shared keys from sh */
		System.out.println(ShamirAPI.join(shares));
		assertTrue(ShamirAPI.join(shares).equals(new BigInteger("123456")));
		fail("Not yet implemented");
	}

}
