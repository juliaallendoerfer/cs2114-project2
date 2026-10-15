import java.util.NoSuchElementException;
import student.TestCase;
/**
 * // -------------------------------------------------------------------------
 * /** Test class for LinkedChain
 * 
 * @param <T>
 * @author Caleb Terstegen
 * @version 2026.10.12
 */
public class LinkedChainTest
    extends TestCase
{
    //~ Fields ................................................................
    private LinkedChain<String> chain1;
    //~ Constructors ..........................................................

    //~Public  Methods ........................................................
    /**
     * Sets up test cases
     */
    public void setUp() {
        chain1 = new LinkedChain<String>();
    }
    
    /**
     * tests the add method
     */
    public void testAdd() {
        IllegalArgumentException E = null;
        assertTrue(chain1.isEmpty());
        chain1.add("Card A");
        assertEquals("Card A", chain1.getHead());
        assertEquals("Card A", chain1.getTail());
        try{
            chain1.add(null);
        }catch(IllegalArgumentException e){
            E = e;
        }
        assertNotNull(E);
        chain1.add("Card B");
        assertEquals("Card B", chain1.getHead());
        assertEquals("Card A", chain1.getTail());
    }
    
    /**
     * tests the add to back method
     */
    public void testAddToBack() {
        chain1.addToBack("Card A");
        assertEquals("Card A", chain1.getHead());
        assertEquals("Card A", chain1.getTail());
        chain1.addToBack("Card B");
        assertEquals("Card A", chain1.getHead());
        assertEquals("Card B", chain1.getTail());
        assertEquals(2, chain1.getNumberOfEntries());
    }
    
    /**
     * tests the remove first method
     */
    public void TestRemoveFirst() {
        NoSuchElementException E = null;
        try{
            chain1.removeFirst();
        }catch(NoSuchElementException e){
            E = e;
        }
        assertNotNull(E);
        chain1.add("Card A");
        chain1.add("Card B");
        assertEquals("Card A", chain1.removeFirst());
        assertEquals("Card B", chain1.getHead());
        assertEquals(1, chain1.getNumberOfEntries());
    }
    
    /**
     * tests the cycle front to back method
     */
    public void testCycleFrontToBack() {
        chain1.add("Q1");
        chain1.cycleFrontToBack();
        assertEquals("Q1", chain1.getHead());
        assertEquals("Q1", chain1.getTail());
        chain1.add("Q2");
        chain1.add("Q3");
        chain1.cycleFrontToBack();
        assertEquals("Q2", chain1.getHead());
        assertEquals("Q3", chain1.getTail());
        
    }
    
    /**
     * tests the remove method
     */
    public void testRemove() {
        chain1.add("A");
        chain1.add("B");
        chain1.add("C");
        assertEquals("A", chain1.getTail());
        assertTrue(chain1.contains("B"));
        assertEquals("C", chain1.getHead());
        chain1.remove("B");
        assertEquals("A", chain1.getTail());
        assertFalse(chain1.contains("B"));
        assertEquals("C", chain1.getHead());
        assertFalse(chain1.remove("Z"));
        assertEquals("A", chain1.getTail());
        assertFalse(chain1.contains("B"));
        assertEquals("C", chain1.getHead());
    }
    
    /**
     * tests the clear method
     */
    public void testClear() {
        chain1.add("A");
        chain1.add("B");
        chain1.add("C");
        assertFalse(chain1.isEmpty());
        chain1.clear();
        assertTrue(chain1.isEmpty());
    }
    
    /**
     * tests the contains method
     */
    public void testContains() {
        chain1.add("A");
        chain1.add("B");
        chain1.add("C");
        assertTrue(chain1.contains("A"));
        assertFalse(chain1.contains("Z"));
    }
    
    /**
     * tests the is empty method
     */
    public void testIsEmpty() {
        assertTrue(chain1.isEmpty());
        chain1.add("A");
        assertFalse(chain1.isEmpty());
    }
    
    /**
     * tests the count method
     */
    public void testCount() {
        chain1.add("A");
        chain1.add("B");
        chain1.add("C");
        assertEquals(1, chain1.count("A"));
        chain1.add("A");
        assertEquals(2, chain1.count("A"));
        chain1.add("B");
        assertEquals(2, chain1.count("A"));
        assertEquals(2, chain1.count("B"));
    }
    
    /**
     * tests the number of entries method
     */
    public void testGetNumberOfEntries() {
        assertEquals(0, chain1.getNumberOfEntries());
        chain1.add("A");
        assertEquals(1, chain1.getNumberOfEntries());
        chain1.add("B");
        assertEquals(2, chain1.getNumberOfEntries());
    }
    
    /**
     * tests the to array method
     */
    public void testToArray() {
        IllegalArgumentException E = null;
        String[] ar1 = new String[3];
        String[] ar3 = {"C", "B", "A"};
        chain1.add("A");
        chain1.add("B");
        chain1.add("C");
        assertEquals(ar3[0], chain1.toArray(ar1)[0]);
        assertEquals(ar3[1], chain1.toArray(ar1)[1]);
        assertEquals(ar3[2], chain1.toArray(ar1)[2]);
        try{
            chain1.toArray(null);
        }catch(IllegalArgumentException e){
            E = e;
        }
        assertNotNull(E);
        E = null;
        chain1.add("D");
        try{
            chain1.toArray(ar3);
        }catch(IllegalArgumentException e){
            E = e;
        }
        assertNotNull(E);
    }
    
    /**
     * tests the to string method
     */
    public void testToString() {
        assertEquals("[]", chain1.toString());
        chain1.add("A");
        chain1.add("B");
        chain1.add("C");
        assertEquals("[C, B, A]", chain1.toString());
    }
}
