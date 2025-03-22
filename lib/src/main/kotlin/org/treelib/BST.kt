package org.treelib

class BSTNode<K: Comparable<K>, V: Any>(key: K, data: V) : Node<K, V>(key, data)

class BST<K: Comparable<K>, V: Any>(key: K, data: V) : BinaryTree<K, V>(key, data) {

    override var root: Node<K, V>? = BSTNode(key, data)

    override fun insert(key: K, value: V): Node<K, V> {
        val resultNode: BSTNode<K, V> = BSTNode(key, value)
        var currentNode: BSTNode<K, V>? = root as BSTNode<K, V>?
        while (currentNode?.key != null) {
            currentNode =
                if (currentNode.key > resultNode.key) {
                    (currentNode.left ?: break) as BSTNode<K, V>?
                } else {
                    (currentNode.right ?: break) as BSTNode<K, V>?
            }
        }
        if (currentNode != null) {
            if (currentNode.key > resultNode.key) {
                currentNode.left = resultNode
            } else {
                currentNode.right = resultNode
            }
        }
        else{
            root = resultNode
        }
        return resultNode
    }

    override fun search(key: K): Node<K, V>? {
        TODO("Not yet implemented")
    }

    override fun delete(key: K): Node<K, V>? {
        TODO("Not yet implemented")
    }

    override fun iterator(key: K): Iterable<Node<K, V>> {
        TODO("Not yet implemented")
    }
}