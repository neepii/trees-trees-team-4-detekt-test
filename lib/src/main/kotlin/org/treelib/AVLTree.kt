package org.treelib

import kotlin.math.max

typealias NSEE = NoSuchElementException

const val RIGHT_HEAVY = 2
const val LEFT_HEAVY = -2
const val LEFT_RIGHT_HEAVY = 1
const val RIGHT_LEFT_HEAVY = -1

/**
 * Represents a node in the AVL tree.
 *
 * @param K the type of keys maintained by this node.
 * @param V the type of mapped values.
 * @property key the key associated with this node.
 * @property data the value stored in this node.
 */
class AVLNode<K : Comparable<K>, V : Any>(key: K, data: V) : Node<K, V, AVLNode<K, V>>(key, data) {
	internal var height: Int = 1
}

/**
 * An AVL tree that maintains balance through rotations.
 *
 * @param K the type of keys maintained by this tree.
 * @param V the type of mapped values.
 * @property root the root node of the AVL tree.
 */
class AVLTree<K : Comparable<K>, V : Any>(override var root: AVLNode<K, V>? = null) :
	BinaryTree<K, V, AVLNode<K, V>>(root) {

	private fun getHeight(node: AVLNode<K, V>?): Int {
		return node?.height ?: 0
	}

	private fun updateHeight(node: AVLNode<K, V>) {
		node.height = max(getHeight(node.left), getHeight(node.right)) + 1
	}

	private fun getBalance(node: AVLNode<K, V>?): Int {
		return if (node == null) 0 else getHeight(node.right) - getHeight(node.left)
	}

	private fun balance(node: AVLNode<K, V>): AVLNode<K, V> {
		val balance = getBalance(node)
		if (balance == RIGHT_HEAVY && getBalance(node.right) == RIGHT_LEFT_HEAVY) {
			node.right = rotateRight(node.right ?: throw NSEE())
		} else if (balance == LEFT_HEAVY && getBalance(node.left) == LEFT_RIGHT_HEAVY) {
			node.left = rotateLeft(node.left ?: throw NSEE())
		}

		val rotatedNode = if (balance == RIGHT_HEAVY) rotateLeft(node)
		else if (balance == LEFT_HEAVY) rotateRight(node) else node

		return rotatedNode
	}

	private fun rotateLeft(node: AVLNode<K, V>): AVLNode<K, V> {
		val rightChild = node.right ?: return node
		val middleSubtree = rightChild.left

		node.right = middleSubtree
		rightChild.left = node

		updateHeight(node)
		updateHeight(rightChild)

		if (root == node) root = rightChild

		return rightChild
	}

	private fun rotateRight(node: AVLNode<K, V>): AVLNode<K, V> {
		val leftChild = node.left ?: return node
		val middleSubtree = leftChild.right

		node.left = middleSubtree
		leftChild.right = node

		updateHeight(node)
		updateHeight(leftChild)

		if (root == node) root = leftChild

		return leftChild
	}

	/**
	 * Inserts a new node with the specified [key] and [data] into the AVL tree.
	 *
	 * If a node with the same key already exists, its data is updated.
	 * The tree is rebalanced as needed after insertion.
	 *
	 * @param key the key to insert.
	 * @param data the value associated with the key.
	 * @return the inserted node, or the updated node if the key already exists.
	 */
	override fun insert(key: K, data: V): AVLNode<K, V>? {
		var insertedNode: AVLNode<K, V>? = null

		fun insertRec(node: AVLNode<K, V>?): AVLNode<K, V>? {
			if (node == null) {
				insertedNode = AVLNode(key, data)
				return insertedNode
			}
			when {
				key < node.key -> node.left = insertRec(node.left)
				key > node.key -> node.right = insertRec(node.right)
				else -> {
					node.data = data
					insertedNode = node
				}
			}
			updateHeight(node)
			return balance(node)

		}
		root = insertRec(root)
		return insertedNode
	}

	/**
	 * Deletes the node with the specified [key] from the AVL tree.
	 *
	 * The tree is rebalanced after deletion. If the node to delete is not found,
	 * a [NoSuchElementException] is thrown.
	 *
	 * Note that if the node being deleted is the root, the root is updated.
	 * In non-root deletion cases, the function returns the replacement node.
	 *
	 * @param key the key of the node to delete.
	 * @return the new root if the deleted node was the root, or the replacement node otherwise.
	 * @throws NoSuchElementException if a node with the specified key is not found.
	 */
	override fun delete(key: K): AVLNode<K, V>? {
		var swappedNode: AVLNode<K, V>? = null
		fun deleteRec(key: K, node: AVLNode<K, V>?): AVLNode<K, V>? {
			when {
				node == null -> throw NSEE()
				key < node.key -> node.left = deleteRec(key, node.left)
				key > node.key -> node.right = deleteRec(key, node.right)
				else -> {
					if (node.left == null || node.right == null) {
						swappedNode = node.left ?: node.right
						return swappedNode
					}
					val temp = findMax(node.left)
					node.key = temp?.key ?: throw NSEE()
					node.data = temp.data
					swappedNode = node
					node.left = deleteRec(temp.key, node.left)
				}
			}
			updateHeight(node)
			return balance(node)
		}

		if (key == root?.key) {
			root = deleteRec(key, root)
		} else root?.let {
			deleteRec(key, it)
			return swappedNode
		}
		return root
	}
}
