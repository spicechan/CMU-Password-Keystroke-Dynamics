package rhythmKeyPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class PersistentDataStorageTest {

	KeyPress k1, k2, k3;
	Session s1, s2;
	List<KeyPress> l1, l2;
	PersistentDataStorage pds;
	
	@Before
	public void setUp(){
		k1 = new KeyPress("A", 1.0, 2.0);
		k2 = new KeyPress("B", 2.5, 3.0);
		k3 = new KeyPress("C", 3.0, 4.0);
		
		l1 = new ArrayList<KeyPress>();
		l1.add(k1);
		l1.add(k2);
		l1.add(k3);
		
		l2 = new ArrayList<KeyPress>();
		l2.add(k3);
		l2.add(k1);
		l2.add(k2);
		
		s1 = new Session(0, l1);
		s2 = new Session(0, l2);
		
		pds = new PersistentDataStorage("Bob");
	}
	
	@Test
	public void testStoreData() throws IOException{
		pds.storeData(s1);
		pds.storeData(s2);
		
	}
}
