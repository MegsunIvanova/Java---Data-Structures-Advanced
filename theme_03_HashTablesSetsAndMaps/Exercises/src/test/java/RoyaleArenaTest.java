import org.junit.Test;

import static org.junit.Assert.*;

public class RoyaleArenaTest {


    @Test
    public void testAddWorksCorrectly() {
        RoyaleArena royaleArena = new RoyaleArena();
        Battlecard battlecard = new Battlecard(1, CardType.MELEE, "test name", 100, 50);
        royaleArena.add(battlecard);
        assertTrue(royaleArena.contains(battlecard));
        assertEquals(1, royaleArena.count());
    }

    @Test
    public void testChangeType() {
        RoyaleArena royaleArena = new RoyaleArena();
        Battlecard battlecard = new Battlecard(1, CardType.MELEE, "test name", 100, 50);
        royaleArena.add(battlecard);
        royaleArena.changeCardType(1, CardType.SPELL);
        assertEquals(CardType.SPELL, battlecard.getType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeTypeShouldThrow() {
        RoyaleArena royaleArena = new RoyaleArena();
        Battlecard battlecard = new Battlecard(1, CardType.MELEE, "test name", 100, 50);
        royaleArena.add(battlecard);
        royaleArena.changeCardType(2, CardType.SPELL);
    }

    @Test
    public void testRemove() {
        RoyaleArena royaleArena = new RoyaleArena();
        Battlecard battlecard = new Battlecard(1, CardType.MELEE, "test name", 100, 50);
        royaleArena.add(battlecard);
        royaleArena.removeById(1);
        assertFalse(royaleArena.contains(battlecard));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveShouldThrow() {
        RoyaleArena royaleArena = new RoyaleArena();
        Battlecard battlecard = new Battlecard(1, CardType.MELEE, "test name", 100, 50);
        royaleArena.add(battlecard);
        royaleArena.removeById(2);
    }
}