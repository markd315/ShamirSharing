import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

class modInverseTest {

	@Test
	void test() {
		BigInteger res = ShamirAPI.modInverse(new BigInteger("9"), new BigInteger("7"));
		assertEquals(new BigInteger("4"), res);
		res = ShamirAPI.modInverse(new BigInteger("19"), new BigInteger("43"));
		assertEquals(new BigInteger("34"), res);
		res = ShamirAPI.modInverse(new BigInteger("167"), new BigInteger("19"));
		assertEquals(new BigInteger("14"), res);
		res = ShamirAPI.modInverse(new BigInteger("21721"), new BigInteger("121"));
		assertEquals(new BigInteger("41"), res);
	}
}
