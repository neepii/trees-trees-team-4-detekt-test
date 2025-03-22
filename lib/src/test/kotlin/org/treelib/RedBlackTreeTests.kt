package org.treelib

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.RepeatedTest
import kotlin.random.Random
import kotlin.test.BeforeTest

class RBTreeInvariantCheck<K: Comparable<K>, V: Any>(var tree: RedBlackTree<K, V>) {

    fun isBlackBalanced(): Boolean {
        return subtreeBlackCountDifference(tree.root) != -1
    }

    private fun subtreeBlackCountDifference(node: RBNode<K, V>?): Int {
        node?.let {
            var left = subtreeBlackCountDifference(node.left)
            var right = subtreeBlackCountDifference(node.right)
            if (left == right) {
                return left
            } else {
                return -1
            }

        } ?:let {
            return 1
        }
    }
    fun assertRedLinkAreLeaningLeft() {
        assertRedLinkAreLeaningLeft(tree.root)
    }
    private fun assertRedLinkAreLeaningLeft(node: RBNode<K, V>?) {
        node?.let {
            if (node.rightIsRed()) {
                throw Exception("Tree with right leaning red link")
            }
            assertRedLinkAreLeaningLeft(node.left)
            assertRedLinkAreLeaningLeft(node.right)
        }
    }
}

class RedBlackTreeTests {
    lateinit var intTree: RedBlackTree<Int, Int>
    lateinit var check: RBTreeInvariantCheck<Int, Int>
    @BeforeTest
    fun before() {
        intTree = RedBlackTree<Int, Int>()
        check = RBTreeInvariantCheck<Int, Int>(intTree)
    }
    @Test
    fun `assert root is null right after initialization`() {
        assertNull(intTree.root)
    }

    @Test
    fun `insert 1`() {
        intTree.insert(1,1)
        assertEquals(intTree.root?.data, 1)
        assertEquals(intTree.root?.key, 1)
    }

    @Test
    fun `insert 2`() {
        intTree.insert(2, 257)
        intTree.insert(1, 256)
        intTree.insert(3, 247)
        assertEquals(intTree.root?.data, 257)
        assertEquals(intTree.root?.left?.data, 256)
        assertEquals(intTree.root?.right?.data, 247)
    }

    @Test
    fun `is perfect black balanced 1`() {
        var arr = arrayOf(10, 44, 3, 88, 2, 86, 20, 60)
        arr.forEachIndexed { ind, value -> intTree.insert(value , ind)}
        var bool = check.isBlackBalanced()
        assertEquals(bool, true)
    }

    @RepeatedTest(10)
    fun `is perfect black balanced 2`() {
        for (i in 1..25) {
            var randomKey = Random.nextInt(0,1000)
            intTree.insert(randomKey, i)
        }

        assertEquals(check.isBlackBalanced(), true)
    }

    @RepeatedTest(10)
    fun `tree is with red leaning links`() {
        for (i in 1..25) {
            var randomKey = Random.nextInt(0,1000)
            intTree.insert(randomKey, i)
        }
        check.assertRedLinkAreLeaningLeft()
    }

    @Test
    fun `search function test 1`() {
        var arr = arrayOf(95, 26, 90, 48, 15, 55, 21)
        arr.forEachIndexed { ind, value -> intTree.insert(value, ind)}
        for (i in 0..(arr.size - 1)) {
            var value = intTree.search(arr[i])?.data
            assertEquals(value, i)
        }
    }

    @RepeatedTest(10)
    fun `search function test 2`() {
        var arr = Array<Int>(8) {
            Random.nextInt(0, 100)
        }
        var list = arr.distinct()
        list.forEachIndexed { ind, value -> intTree.insert(value, ind) }

        for (i in 0..(list.size - 1)) {
            var value = intTree.search(list[i])?.data
            assertEquals(value, i)
        }
    }

    @Test
    fun `deleteMin 1`() {
        var arr = arrayOf(10, 44, 3, 88, 2, 86, 20, 60)
        arr.forEachIndexed { ind, value -> intTree.insert(value , ind)}
        arr.sort()
        for (i in 0..(arr.size - 1)) {
            intTree.deleteMin()
            var node = intTree.search(arr[i])
            assertEquals(node, null)
        }
        assertEquals(intTree.root, null)
    }

    @Test
    fun `deleteMin 2`() {
        var arr = Array<Int>(8) {
            Random.nextInt(0, 100)
        }
        var list = arr.distinct().toTypedArray()
        list.forEachIndexed { ind, value -> intTree.insert(value , ind)}
        list.sort()
        for (i in 0..(arr.size - 1)) {
            intTree.deleteMin()
            var node = intTree.search(list[i])
            assertEquals(node, null)
        }
        assertEquals(intTree.root, null)
    }
}
