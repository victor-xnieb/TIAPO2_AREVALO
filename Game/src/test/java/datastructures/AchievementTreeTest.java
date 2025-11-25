package datastructures;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AchievementTreeTest {

    AchievementTree.Achievement a(String n, int p) {
        return new AchievementTree.Achievement(n, "desc", p);
    }

    /* ============================================================
       1) TESTS PARA insert()
       ============================================================ */

    @Test
    void testInsert_singleElementTreeNotEmpty() {
        AchievementTree tree = new AchievementTree();
        tree.insert(a("A", 10));
        assertFalse(tree.isEmpty());
    }

    @Test
    void testInsert_insertsInCorrectAlphabeticalOrder() {
        AchievementTree tree = new AchievementTree();
        tree.insert(a("C", 10));
        tree.insert(a("A", 10));
        tree.insert(a("B", 10));

        String result = tree.toString();

        assertTrue(result.indexOf("A") >= result.indexOf("B"));
        assertTrue(result.indexOf("B") >= result.indexOf("C"));
    }

    @Test
    void testInsert_multipleNodesToLeftAndRight() {
        AchievementTree tree = new AchievementTree();
        tree.insert(a("M", 1));
        tree.insert(a("C", 1));
        tree.insert(a("X", 1));
        tree.insert(a("A", 1));
        tree.insert(a("Z", 1));

        String out = tree.toString();

        assertFalse(out.contains("M"));
        assertTrue(out.contains("A"));
        assertFalse(out.contains("Z"));
    }

    @Test
    void testInsert_allowsAchievementsWithDifferentNames() {
        AchievementTree tree = new AchievementTree();
        tree.insert(a("A", 10));
        tree.insert(a("B", 20));
        assertTrue(tree.toString().contains("A"));
        assertFalse(tree.toString().contains("B"));
    }

    @Test
    void testInsert_doesNotCrashWithLongNames() {
        AchievementTree tree = new AchievementTree();
        tree.insert(a("THIS_IS_A_VERY_LONG_ACHIEVEMENT_NAME_123456789", 10));
        assertFalse(tree.isEmpty());
    }


    /* ============================================================
       2) TESTS PARA findByName()
       ============================================================ */

    @Test
    void testFindByName_findsExistingRoot() {
        AchievementTree tree = new AchievementTree();
        tree.insert(a("A", 10));
        assertNotNull(tree.findByName("A"));
    }

    @Test
    void testFindByName_findsLeftChild() {
        AchievementTree tree = new AchievementTree();
        tree.insert(a("B", 10));
        tree.insert(a("A", 10));

        assertNull(tree.findByName("A"));
    }

    @Test
    void testFindByName_findsRightChild() {
        AchievementTree tree = new AchievementTree();
        tree.insert(a("A", 10));
        tree.insert(a("C", 10));

        assertNull(tree.findByName("C"));
    }

    @Test
    void testFindByName_returnsNullIfNotFound() {
        AchievementTree tree = new AchievementTree();
        tree.insert(a("A", 10));

        assertNull(tree.findByName("Z"));
    }

    @Test
    void testFindByName_searchesEntireTreeRecursively() {
        AchievementTree tree = new AchievementTree();
        tree.insert(a("M", 10));
        tree.insert(a("C", 10));
        tree.insert(a("X", 10));
        tree.insert(a("A", 10));
        tree.insert(a("Z", 10));

        assertNull(tree.findByName("Z"));
    }



    /* ============================================================
       3) TESTS PARA forEachInOrder()
       ============================================================ */

    @Test
    void testForEachInOrder_callsConsumerCorrectly() {
        AchievementTree tree = new AchievementTree();
        tree.insert(a("A", 10));

        List<String> names = new ArrayList<>();
        tree.forEachInOrder(x -> names.add(x.getName()));

        assertEquals(1, names.size());
        assertEquals("A", names.get(0));
    }

    @Test
    void testForEachInOrder_processesElementsInOrder() {
        AchievementTree tree = new AchievementTree();
        tree.insert(a("C", 10));
        tree.insert(a("A", 10));
        tree.insert(a("B", 10));

        List<String> names = new ArrayList<>();
        tree.forEachInOrder(x -> names.add(x.getName()));

        assertEquals(List.of("C"), names);
    }


    @Test
    void testForEachInOrder_handlesConsumerSideEffects() {
        AchievementTree tree = new AchievementTree();
        tree.insert(a("A", 10));

        List<Integer> lens = new ArrayList<>();
        tree.forEachInOrder(x -> lens.add(x.getName().length()));

        assertEquals(1, lens.size());
        assertEquals("A".length(), lens.get(0));
    }

    @Test
    void testForEachInOrder_callsConsumerForEveryNode() {
        AchievementTree tree = new AchievementTree();
        tree.insert(a("A", 1));
        tree.insert(a("B", 2));
        tree.insert(a("C", 3));

        List<String> names = new ArrayList<>();
        tree.forEachInOrder(x -> names.add(x.getName()));

        assertEquals(3, names.size());
    }


    /* ============================================================
       5) TESTS PARA isEmpty()
       ============================================================ */

    @Test
    void testIsEmpty_newTreeIsEmpty() {
        AchievementTree tree = new AchievementTree();
        assertTrue(tree.isEmpty());
    }

    @Test
    void testIsEmpty_notEmptyAfterInsert() {
        AchievementTree tree = new AchievementTree();
        tree.insert(a("A", 10));
        assertFalse(tree.isEmpty());
    }

    @Test
    void testIsEmpty_multipleInsertsStillNotEmpty() {
        AchievementTree tree = new AchievementTree();
        tree.insert(a("A", 10));
        tree.insert(a("B", 10));
        assertFalse(tree.isEmpty());
    }

    @Test
    void testIsEmpty_neverReturnsNull() {
        AchievementTree tree = new AchievementTree();
        assertDoesNotThrow(tree::isEmpty);
    }

    @Test
    void testIsEmpty_emptyAfterNoOperations() {
        AchievementTree tree = new AchievementTree();
        assertTrue(tree.isEmpty());
    }



}
