package jhexedit.bdoc.test;

import java.util.*;

import jhexedit.bdoc.BinaryDocument;
import jhexedit.bdoc.ByteSpan;
import jhexedit.bdoc.ContentChangedEvent;

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

	public void testWritingLargeAmountOfData() throws Exception {
		BinaryDocument doc = new BinaryDocument();
		assertEquals(0, doc.length());
		byte[] buf = new byte[512];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = (byte) i;
		}
		doc.write(doc.createOffset(0), buf);
		assertEquals(buf.length, doc.length());
		byte[] buf2 = new byte[buf.length];
		assertEquals(buf.length, doc.read(doc.createOffset(0), buf2));
		for (int i = 0; i < buf.length; i++) {
			assertEquals(buf[i], buf2[i]);
		}
	}

	public void testInsertUpdateEvent() throws Exception {
		DocumentObserver observer = new DocumentObserver();
		BinaryDocument doc = new BinaryDocument();
		assertEquals(0, doc.length());
		doc.addObserver(observer);
		doc.insert(doc.createOffset(0), new byte[] {0x20});
		assertEquals(1, observer.events.size());
		assertTrue(observer.events.getFirst() instanceof ContentChangedEvent);
		ContentChangedEvent event = (ContentChangedEvent) observer.events.getFirst();
		assertEquals(ContentChangedEvent.INSERTED, event.getType());
		ByteSpan span = event.getSpan();
		assertEquals(1, span.length());
		assertEquals(0, span.getStartLocation().getOffset());
	}

	private static class DocumentObserver implements Observer {
		public LinkedList events = new LinkedList();
		public synchronized void update(Observable o, Object arg) {
			events.add(arg);
		}
	}
}
