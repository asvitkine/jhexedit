package jhexedit.bdoc.test;

import jhexedit.bdoc.BinaryDocument;

import junit.framework.TestCase;

public class TestBinaryDocument extends TestCase {
	public void testInsertingLargeAmountOfData() throws Exception {
		BinaryDocument doc = new BinaryDocument();
		assertEquals(0, doc.length());
		byte[] buf = new byte[512];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = (byte) i;
		}
		doc.insert(doc.createOffset(0), buf);
		assertEquals(buf.length, doc.length());
		byte[] buf2 = new byte[buf.length];
		assertEquals(buf.length, doc.read(doc.createOffset(0), buf2));
		for (int i = 0; i < buf.length; i++) {
			assertEquals(buf[i], buf2[i]);
		}
	}
}
