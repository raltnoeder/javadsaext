package dsaext.qtree;

import dsaext.QIterator;
import dsaext.MapEntry;

/**
 * Quick balanced binary search tree
 *
 * @version 2018-07-18_001
 * @author  Robert Altnoeder (r.altnoeder@gmx.net)
 *
 * Copyright (C) 2011 - 2018 Robert ALTNOEDER
 *
 * Redistribution and use in source and binary forms,
 * with or without modification, are permitted provided that
 * the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public final class QTree<K extends Comparable<K>, V>
    implements Iterable<MapEntry<K, V>>
{
    private Node<K, V> root;
    private long size;

    private enum Direction
    {
        NONE,
        LESS,
        GREATER
    };

    public QTree()
    {
        root = null;
        size = 0;
    }

    private static final class Node<K extends Comparable<K>, V>
    {
        /* key and value objects */
        K key;
        V value;

        /* references to parent and child nodes */
        Node<K, V> parent;
        Node<K, V> less;
        Node<K, V> greater;

        /* balance number */
        int balance;

        Node(K keyRef, V valRef)
        {
            key   = keyRef;
            value = valRef;

            balance  = 0;

            parent   = null;
            less     = null;
            greater  = null;
        }
    }

    private static final class ItemEnumerationNode<T>
    {
        T value;
        ItemEnumerationNode next;

        ItemEnumerationNode(T valueRef)
        {
            value = valueRef;
            next = null;
        }
    }

    private static final class ItemEnumeration<T>
        implements java.util.Enumeration<T>
    {
        private ItemEnumerationNode<T> next;
        private ItemEnumerationNode<T> current;

        ItemEnumeration(ItemEnumerationNode<T> root)
        {
            next = root;
        }

        @Override
        public boolean hasMoreElements()
        {
            return (next != null);
        }

        @Override
        public T nextElement()
        {
            T enumVal = null;
            if (next != null)
            {
                enumVal = next.value;
                next = next.next;
            }
            return enumVal;
        }
    }

    private static class BaseIterator<K extends Comparable<K>, V>
    {
        QTree<K, V> container;
        Node<K, V> next;
        Node<K, V> current;

        BaseIterator(QTree<K, V> containerRef)
        {
            container = containerRef;
            current = null;
            next = container.root;
            if (next != null)
            {
                while (next.less != null)
                {
                    next = next.less;
                }
            }
        }

        BaseIterator(QTree<K, V> containerRef, Node<K, V> startNode)
        {
            container = containerRef;
            next = startNode;
        }

        public final long getSize()
        {
            return container.size;
        }

        public final boolean hasNext()
        {
            return next != null;
        }

        final Node<K, V> nextNode()
        {
            current = next;

            if (current != null)
            {
                if (next.greater != null)
                {
                    next = next.greater;
                    while (next.less != null)
                    {
                        next = next.less;
                    }
                }
                else
                {
                    while (next.parent != null && next.parent.greater == next)
                    {
                        next = next.parent;
                    }
                    next = next.parent;
                }
            }

            return current;
        }

        public final void remove()
        {
            if (current != null)
            {
                container.remove(current.key);
                current = null;
            }
            else
            {
                throw new IllegalStateException();
            }
        }
    }

    private static class BaseReverseIterator<K extends Comparable<K>, V>
    {
        QTree<K, V> container;
        Node<K, V> next;
        Node<K, V> current;

        BaseReverseIterator(QTree<K, V> containerRef)
        {
            container = containerRef;
            current = null;
            next = container.root;
            if (next != null)
            {
                while (next.greater != null)
                {
                    next = next.greater;
                }
            }
        }

        BaseReverseIterator(QTree<K, V> containerRef, Node<K, V> startNode)
        {
            container = containerRef;
            next = startNode;
        }

        public final long getSize()
        {
            return container.size;
        }

        public final boolean hasNext()
        {
            return next != null;
        }

        final Node<K, V> nextNode()
        {
            current = next;

            if (current != null)
            {
                if (next.less != null)
                {
                    next = next.less;
                    while (next.greater != null)
                    {
                        next = next.greater;
                    }
                }
                else
                {
                    while (next.parent != null && next.parent.less == next)
                    {
                        next = next.parent;
                    }
                    next = next.parent;
                }
            }

            return current;
        }

        public final void remove()
        {
            if (current != null)
            {
                container.remove(current.key);
                current = null;
            }
            else
            {
                throw new IllegalStateException();
            }
        }
    }

    private static final class ValuesIterator<K extends Comparable<K>, V>
        extends BaseIterator<K, V> implements QIterator<V>
    {
        ValuesIterator(QTree<K, V> containerRef)
        {
            super(containerRef);
        }

        ValuesIterator(QTree<K, V> containerRef, Node startNode)
        {
            super(containerRef, startNode);
        }

        @Override
        public final V next()
        {
            V value = null;
            Node<K, V> node = nextNode();
            if (node != null)
            {
                value = node.value;
            }
            return value;
        }
    }

    private static final class ValuesReverseIterator<K extends Comparable<K>, V>
        extends BaseReverseIterator<K, V> implements QIterator<V>
    {
        ValuesReverseIterator(QTree<K, V> containerRef)
        {
            super(containerRef);
        }

        ValuesReverseIterator(QTree<K, V> containerRef, Node startNode)
        {
            super(containerRef, startNode);
        }

        @Override
        public final V next()
        {
            V value = null;
            Node<K, V> node = nextNode();
            if (node != null)
            {
                value = node.value;
            }
            return value;
        }
    }

    private static final class KeysIterator<K extends Comparable<K>, V>
        extends BaseIterator<K, V> implements QIterator<K>
    {
        KeysIterator(QTree<K, V> containerRef)
        {
            super(containerRef);
        }

        KeysIterator(QTree<K, V> containerRef, Node startNode)
        {
            super(containerRef, startNode);
        }

        @Override
        public final K next()
        {
            K key = null;
            Node<K, V> node = nextNode();
            if (node != null)
            {
                key = node.key;
            }
            return key;
        }
    }

    private static final class KeysReverseIterator<K extends Comparable<K>, V>
        extends BaseReverseIterator<K, V> implements QIterator<K>
    {
        KeysReverseIterator(QTree<K, V> containerRef)
        {
            super(containerRef);
        }

        KeysReverseIterator(QTree<K, V> containerRef, Node startNode)
        {
            super(containerRef, startNode);
        }

        @Override
        public final K next()
        {
            K key = null;
            Node<K, V> node = nextNode();
            if (node != null)
            {
                key = node.key;
            }
            return key;
        }
    }

    private static final class EntriesIterator<K extends Comparable<K>, V>
        extends BaseIterator<K, V> implements QIterator<MapEntry<K, V>>
    {
        EntriesIterator(QTree<K, V> containerRef)
        {
            super(containerRef);
        }

        EntriesIterator(QTree<K, V> containerRef, Node<K, V> startNode)
        {
            super(containerRef, startNode);
        }

        @Override
        public final MapEntry<K, V> next()
        {
            MapEntry<K, V> entry = null;
            Node<K, V> node = nextNode();
            if (node != null)
            {
                entry = new MapEntry<>(node.key, node.value);
            }
            return entry;
        }
    }

    private static final class EntriesReverseIterator<K extends Comparable<K>, V>
        extends BaseReverseIterator<K, V> implements QIterator<MapEntry<K, V>>
    {
        EntriesReverseIterator(QTree<K, V> containerRef)
        {
            super(containerRef);
        }

        EntriesReverseIterator(QTree<K, V> containerRef, Node<K, V> startNode)
        {
            super(containerRef, startNode);
        }

        @Override
        public final MapEntry<K, V> next()
        {
            MapEntry<K, V> entry = null;
            Node<K, V> node = nextNode();
            if (node != null)
            {
                entry = new MapEntry<>(node.key, node.value);
            }
            return entry;
        }
    }

    public void insert(K key, V val)
    {
        Node<K, V> insNode = new Node<K, V>(key, val);

        if (root == null)
        {
            root = insNode;
            ++size;
        }
        else
        {
            Node<K, V> parentNode = root;
            while (true)
            {
               int cmpRc = insNode.key.compareTo(parentNode.key);
               if (cmpRc < 0)
               {
                   if (parentNode.less == null)
                   {
                       parentNode.less = insNode;
                       insNode.parent  = parentNode;
                       ++size;
                       break;
                   }
                   else
                   {
                       parentNode = parentNode.less;
                   }
               }
               else
               if (cmpRc > 0)
               {
                   if (parentNode.greater == null)
                   {
                       parentNode.greater = insNode;
                       insNode.parent     = parentNode;
                       ++size;
                       break;
                   }
                   else
                   {
                       parentNode = parentNode.greater;
                   }
               }
               else
               {
                   parentNode.key   = insNode.key;
                   parentNode.value = insNode.value;
                   parentNode = null;
                   break;
               }
            }

            /* update balance and perform rotations */
            while (parentNode != null)
            {
                if (parentNode.less == insNode)
                {
                    --parentNode.balance;
                }
                else
                {
                    ++parentNode.balance;
                }

                if (parentNode.balance == 0)
                {
                    break;
                }
                else
                if (parentNode.balance == -2)
                {
                    if (insNode.balance == -1)
                    {
                        /* rotate R */
                        parentNode.balance = 0;
                        insNode.balance    = 0;

                        insNode.parent = parentNode.parent;
                        if (parentNode.parent != null)
                        {
                            if (parentNode.parent.less == parentNode)
                            {
                                parentNode.parent.less = insNode;
                            }
                            else
                            {
                                parentNode.parent.greater = insNode;
                            }
                        }
                        else
                        {
                            root = insNode;
                        }

                        parentNode.less = insNode.greater;
                        if (insNode.greater != null)
                        {
                            insNode.greater.parent = parentNode;
                        }

                        insNode.greater   = parentNode;
                        parentNode.parent = insNode;
                    }
                    else
                    {
                        /* rotate LR */
                        if (insNode.greater.balance == -1)
                        {
                            insNode.balance    = 0;
                            parentNode.balance = 1;
                        }
                        else
                        if (insNode.greater.balance == 1)
                        {
                            insNode.balance    = -1;
                            parentNode.balance =  0;
                        }
                        else
                        {
                            insNode.balance    = 0;
                            parentNode.balance = 0;
                        }
                        insNode.greater.balance = 0;

                        insNode.parent        = insNode.greater;
                        insNode.greater       = insNode.greater.less;
                        insNode.parent.less   = insNode;
                        parentNode.less       = insNode.parent.greater;
                        insNode.parent.parent = parentNode.parent;
                        if (insNode.greater != null)
                        {
                            insNode.greater.parent = insNode;
                        }
                        if (parentNode.less != null)
                        {
                            parentNode.less.parent = parentNode;
                        }

                        if (parentNode.parent != null)
                        {
                            if (parentNode.parent.less == parentNode)
                            {
                                parentNode.parent.less = insNode.parent;
                            }
                            else
                            {
                                parentNode.parent.greater = insNode.parent;
                            }
                        }
                        else
                        {
                            root = insNode.parent;
                        }

                        parentNode.parent      = insNode.parent;
                        insNode.parent.greater = parentNode;
                    }
                    break;
                }
                else
                if (parentNode.balance == 2)
                {
                    if (insNode.balance == 1)
                    {
                        /* rotate L */
                        parentNode.balance = 0;
                        insNode.balance    = 0;

                        insNode.parent = parentNode.parent;
                        if (parentNode.parent != null)
                        {
                            if (parentNode.parent.less == parentNode)
                            {
                                parentNode.parent.less = insNode;
                            }
                            else
                            {
                                parentNode.parent.greater = insNode;
                            }
                        }
                        else
                        {
                            root = insNode;
                        }

                        parentNode.greater = insNode.less;
                        if (insNode.less != null)
                        {
                            insNode.less.parent = parentNode;
                        }

                        insNode.less      = parentNode;
                        parentNode.parent = insNode;
                    }
                    else
                    {
                        /* rotate RL */
                        if (insNode.less.balance == -1)
                        {
                            insNode.balance    = 1;
                            parentNode.balance = 0;
                        }
                        else
                        if (insNode.less.balance == 1)
                        {
                            insNode.balance    =  0;
                            parentNode.balance = -1;
                        }
                        else
                        {
                            insNode.balance    = 0;
                            parentNode.balance = 0;
                        }
                        insNode.less.balance = 0;

                        insNode.parent         = insNode.less;
                        insNode.less           = insNode.less.greater;
                        insNode.parent.greater = insNode;
                        parentNode.greater     = insNode.parent.less;
                        insNode.parent.parent  = parentNode.parent;
                        if (insNode.less != null)
                        {
                            insNode.less.parent = insNode;
                        }
                        if (parentNode.greater != null)
                        {
                            parentNode.greater.parent = parentNode;
                        }

                        if (parentNode.parent != null)
                        {
                            if (parentNode.parent.less == parentNode)
                            {
                                parentNode.parent.less = insNode.parent;
                            }
                            else
                            {
                                parentNode.parent.greater = insNode.parent;
                            }
                        }
                        else
                        {
                            root = insNode.parent;
                        }

                        parentNode.parent   = insNode.parent;
                        insNode.parent.less = parentNode;
                    }
                    break;
                }

                insNode    = parentNode;
                parentNode = parentNode.parent;
            }
        }
    }

    public V get(K key)
    {
        V value = null;

        Node<K, V> node = root;
        while (node != null)
        {
            int cmpRc = key.compareTo(node.key);
            if (cmpRc < 0)
            {
                node = node.less;
            }
            else
            if (cmpRc > 0)
            {
                node = node.greater;
            }
            else
            {
                value = node.value;
                break;
            }
        }

        return value;
    }

    public K getCeilingKey(K key)
    {
        K ceilingKey = null;
        Node<K, V> node = findCeilingNode(key);
        if (node != null)
        {
            ceilingKey = node.key;
        }
        return ceilingKey;
    }

    public K getFloorKey(K key)
    {
        K floorKey = null;
        Node<K, V> node = findFloorNode(key);
        if (node != null)
        {
            floorKey = node.key;
        }
        return floorKey;
    }

    public K getGreaterKey(K key)
    {
        K greaterKey = null;
        Node<K, V> node = findGreaterNode(key);
        if (node != null)
        {
            greaterKey = node.key;
        }
        return greaterKey;
    }

    public K getLessKey(K key)
    {
        K lessKey = null;
        Node<K, V> node = findLessNode(key);
        if (node != null)
        {
            lessKey = node.key;
        }
        return lessKey;
    }

    public V getCeilingValue(K key)
    {
        V ceilingValue = null;
        Node<K, V> node = findCeilingNode(key);
        if (node != null)
        {
            ceilingValue = node.value;
        }
        return ceilingValue;
    }

    public V getFloorValue(K key)
    {
        V floorValue = null;
        Node<K, V> node = findFloorNode(key);
        if (node != null)
        {
            floorValue = node.value;
        }
        return floorValue;
    }

    public V getGreaterValue(K key)
    {
        V greaterValue = null;
        Node<K, V> node = findGreaterNode(key);
        if (node != null)
        {
            greaterValue = node.value;
        }
        return greaterValue;
    }

    public V getLessValue(K key)
    {
        V lessValue = null;
        Node<K, V> node = findLessNode(key);
        if (node != null)
        {
            lessValue = node.value;
        }
        return lessValue;
    }

    private Node<K, V> findCeilingNode(K key)
    {
        Node<K, V> node = root;
        while (node != null)
        {
            int cmpRc = key.compareTo(node.key);
            if (cmpRc < 0)
            {
                if (node.less != null)
                {
                    node = node.less;
                }
                else
                {
                    break;
                }
            }
            else
            if (cmpRc > 0)
            {
                if (node.greater != null)
                {
                    node = node.greater;
                }
                else
                {
                    while (node.parent != null && node.parent.greater == node)
                    {
                        node = node.parent;
                    }
                    node = node.parent;
                    break;
                }
            }
            else
            {
                break;
            }
        }
        return node;
    }

    private Node<K, V> findFloorNode(K key)
    {
        Node<K, V> node = root;
        while (node != null)
        {
            int cmpRc = key.compareTo(node.key);
            if (cmpRc < 0)
            {
                if (node.less != null)
                {
                    node = node.less;
                }
                else
                {
                    while (node.parent != null && node.parent.less == node)
                    {
                        node = node.parent;
                    }
                    node = node.parent;
                    break;
                }
            }
            else
            if (cmpRc > 0)
            {
                if (node.greater != null)
                {
                    node = node.greater;
                }
                else
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }
        return node;
    }

    private Node<K, V> findGreaterNode(K key)
    {
        Node<K, V> node = root;
        while (node != null)
        {
            int cmpRc = key.compareTo(node.key);
            if (cmpRc < 0)
            {
                if (node.less != null)
                {
                    node = node.less;
                }
                else
                {
                    break;
                }
            }
            else
            {
                if (node.greater != null)
                {
                    node = node.greater;
                }
                else
                {
                    while (node.parent != null && node.parent.greater == node)
                    {
                        node = node.parent;
                    }
                    node = node.parent;
                    break;
                }
            }
        }
        return node;
    }

    private Node<K, V> findLessNode(K key)
    {
        Node<K, V> node = root;
        while (node != null)
        {
            int cmpRc = key.compareTo(node.key);
            if (cmpRc > 0)
            {
                if (node.greater != null)
                {
                    node = node.greater;
                }
                else
                {
                    break;
                }
            }
            else
            {
                if (node.less != null)
                {
                    node = node.less;
                }
                else
                {
                    while (node.parent != null && node.parent.less == node)
                    {
                        node = node.parent;
                    }
                    node = node.parent;
                    break;
                }
            }
        }
        return node;
    }

    private Node<K, V> findNode(K key)
    {
        Node<K, V> node = null;
        while (node != null)
        {
            int cmpRc = key.compareTo(node.key);
            if (cmpRc < 0)
            {
                node = node.less;
            }
            else
            if (cmpRc > 0)
            {
                node = node.greater;
            }
            else
            {
                break;
            }
        }
        return node;
    }

    public boolean contains(K key)
    {
        Node<K, V> node = root;
        while (node != null)
        {
            int cmpRc = key.compareTo(node.key);
            if (cmpRc < 0)
            {
                node = node.less;
            }
            else
            if (cmpRc > 0)
            {
                node = node.greater;
            }
            else
            {
                break;
            }
        }
        return node != null;
    }

    public void remove(K key)
    {
        Node<K, V> rotNode = null;
        Node<K, V> rmNode = root;
        while (rmNode != null)
        {
            int cmpRc = key.compareTo(rmNode.key);
            if (cmpRc < 0)
            {
                rmNode = rmNode.less;
            }
            else
            if (cmpRc > 0)
            {
                rmNode = rmNode.greater;
            }
            else
            {
                break;
            }
        }

        Direction dir = Direction.NONE;
        if (rmNode != null)
        {
            --size;

            if (rmNode.less == null && rmNode.greater == null)
            {
                if (root == rmNode)
                {
                    // root node leaf
                    root = null;
                }
                else
                {
                    // non-root node leaf
                    rotNode = rmNode.parent;

                    if (rotNode.less == rmNode)
                    {
                        // node to remove is in the left subtree
                        // of its parent

                        // save direction
                        dir = Direction.LESS;
                        rotNode.less = null;
                    }
                    else
                    {
                        dir = Direction.GREATER;
                        rotNode.greater = null;
                    }
                }
            }
            else
            {
                Node<K, V> replaceNode = null;
                // not a leaf node, removal by replacement
                // at least one child, or a child and a subtree, or two subtrees
                // find replacement node
                if (rmNode.balance == -1)
                {
                    replaceNode = rmNode.less;
                    while (replaceNode.greater != null)
                    {
                        replaceNode = replaceNode.greater;
                    }
                }
                else
                {
                    replaceNode = rmNode.greater;
                    while (replaceNode.less != null)
                    {
                        replaceNode = replaceNode.less;
                    }
                }
                rotNode = replaceNode.parent;

                if (rotNode.less == replaceNode)
                {
                    // node to remove is in the left subtree
                    // of its parent

                    // save direction
                    dir = Direction.LESS;

                    if (replaceNode.less != null)
                    {
                        // replace node by its left child
                        rotNode.less = replaceNode.less;
                        replaceNode.less.parent = rotNode;
                    }
                    else
                    if (replaceNode.greater != null)
                    {
                        // replace node by its right child
                        rotNode.less = replaceNode.greater;
                        replaceNode.greater.parent = rotNode;
                    }
                    else
                    {
                        // non-root leaf node
                        rotNode.less = null;
                    }
                }
                else
                {
                    // node to remove is in the right subtree
                    // of its parent

                    // save direction
                    dir = Direction.GREATER;

                    if (replaceNode.less != null)
                    {
                        // replace node by its left child
                        rotNode.greater = replaceNode.less;
                        replaceNode.less.parent = rotNode;
                    }
                    else
                    if (replaceNode.greater != null)
                    {
                        // replace node by its right child
                        rotNode.greater = replaceNode.greater;
                        replaceNode.greater.parent = rotNode;
                    }
                    else
                    {
                        // non-root leaf node
                        rotNode.greater = null;
                    }
                }

                // replace rmNode with replaceNode
                if (rmNode.parent == null)
                {
                    // Node to be removed is the root node
                    root = replaceNode;
                }
                else
                {
                    if (rmNode.parent.less == rmNode)
                    {
                        rmNode.parent.less = replaceNode;
                    }
                    else
                    {
                        rmNode.parent.greater = replaceNode;
                    }
                }
                if (rmNode.less != null)
                {
                    rmNode.less.parent = replaceNode;
                }
                if (rmNode.greater != null)
                {
                    rmNode.greater.parent = replaceNode;
                }
                replaceNode.parent  = rmNode.parent;
                replaceNode.less    = rmNode.less;
                replaceNode.greater = rmNode.greater;
                replaceNode.balance = rmNode.balance;

                if (rotNode == rmNode)
                {
                    rotNode = replaceNode;
                }
            }

            // update balance and perform rotations
            while (rotNode != null)
            {
                if (dir == Direction.LESS)
                {
                    // node was removed from left subtree
                    ++rotNode.balance;
                    if (rotNode.balance == 1)
                    {
                        break;
                    }
                }
                else
                {
                    /* node was removed from right subtree */
                    --rotNode.balance;
                    if (rotNode.balance == -1)
                    {
                        break;
                    }
                }

                if (rotNode.parent != null)
                {
                    if (rotNode.parent.less == rotNode)
                    {
                        dir = Direction.LESS;
                    }
                    else
                    {
                        dir = Direction.GREATER;
                    }
                }

                // update balance and perform rotations
                if (rotNode.balance == -2)
                {
                    Node<K, V> subNode = rotNode.less;
                    // 0 or -1
                    if (subNode.balance <= 0)
                    {
                        // rotate R
                        subNode.parent = rotNode.parent;
                        if (rotNode.parent != null)
                        {
                            if (rotNode.parent.less == rotNode)
                            {
                                rotNode.parent.less = subNode;
                            }
                            else
                            {
                                rotNode.parent.greater = subNode;
                            }
                        }
                        else
                        {
                            root = subNode;
                        }

                        rotNode.less = subNode.greater;
                        if (subNode.greater != null)
                        {
                            subNode.greater.parent = rotNode;
                        }

                        subNode.greater = rotNode;
                        rotNode.parent  = subNode;

                        if (subNode.balance == 0)
                        {
                            rotNode.balance = -1;
                            subNode.balance = 1;
                            break;
                        }
                        else
                        {
                            rotNode.balance = 0;
                            subNode.balance = 0;
                        }
                    }
                    else
                    {
                        // rotate LR
                        if (subNode.greater.balance == -1)
                        {
                            subNode.balance = 0;
                            rotNode.balance = 1;
                        }
                        else
                        if (subNode.greater.balance == 1)
                        {
                            subNode.balance = -1;
                            rotNode.balance = 0;
                        }
                        else
                        {
                            subNode.balance = 0;
                            rotNode.balance = 0;
                        }
                        subNode.greater.balance = 0;

                        subNode.parent        = subNode.greater;
                        subNode.greater       = subNode.greater.less;
                        subNode.parent.less   = subNode;
                        rotNode.less          = subNode.parent.greater;
                        subNode.parent.parent = rotNode.parent;
                        if (subNode.greater != null)
                        {
                            subNode.greater.parent = subNode;
                        }
                        if (rotNode.less != null)
                        {
                            rotNode.less.parent = rotNode;
                        }

                        if (rotNode.parent != null)
                        {
                            if (rotNode.parent.less == rotNode)
                            {
                                rotNode.parent.less = subNode.parent;
                            }
                            else
                            {
                                rotNode.parent.greater = subNode.parent;
                            }
                        }
                        else
                        {
                            root = subNode.parent;
                        }

                        rotNode.parent         = subNode.parent;
                        subNode.parent.greater = rotNode;
                    }
                    rotNode = rotNode.parent;
                    // end of R / LR rotations
                }
                else
                if (rotNode.balance == 2)
                {
                    Node<K, V> subNode = rotNode.greater;
                    // 0 or 1
                    if (subNode.balance >= 0)
                    {
                        // rotate L
                        subNode.parent = rotNode.parent;
                        if (rotNode.parent != null)
                        {
                            if (rotNode.parent.less == rotNode)
                            {
                                rotNode.parent.less = subNode;
                            }
                            else
                            {
                                rotNode.parent.greater = subNode;
                            }
                        }
                        else
                        {
                            root = subNode;
                        }

                        rotNode.greater = subNode.less;
                        if (subNode.less != null)
                        {
                            subNode.less.parent = rotNode;
                        }

                        subNode.less   = rotNode;
                        rotNode.parent = subNode;
                        if (subNode.balance == 0)
                        {
                            rotNode.balance = 1;
                            subNode.balance = -1;
                            break;
                        }
                        else
                        {
                            rotNode.balance = 0;
                            subNode.balance = 0;
                        }
                    }
                    else
                    {
                        // rotate RL
                        if (subNode.less.balance == -1)
                        {
                            subNode.balance = 1;
                            rotNode.balance = 0;
                        }
                        else
                        if (subNode.less.balance == 1)
                        {
                            subNode.balance = 0;
                            rotNode.balance = -1;
                        }
                        else
                        {
                            subNode.balance = 0;
                            rotNode.balance = 0;
                        }
                        subNode.less.balance = 0;

                        subNode.parent         = subNode.less;
                        subNode.less           = subNode.less.greater;
                        subNode.parent.greater = subNode;
                        rotNode.greater        = subNode.parent.less;
                        subNode.parent.parent  = rotNode.parent;
                        if (subNode.less != null)
                        {
                            subNode.less.parent = subNode;
                        }
                        if (rotNode.greater != null)
                        {
                            rotNode.greater.parent = rotNode;
                        }

                        if (rotNode.parent != null)
                        {
                            if (rotNode.parent.less == rotNode)
                            {
                                rotNode.parent.less = subNode.parent;
                            }
                            else
                            {
                                rotNode.parent.greater = subNode.parent;
                            }
                        }
                        else
                        {
                            root = subNode.parent;
                        }

                        rotNode.parent      = subNode.parent;
                        subNode.parent.less = rotNode;
                    }
                    rotNode = rotNode.parent;
                    // end of L / RL rotations
                }
                rotNode = rotNode.parent;
            }
        }
    }

    public void clear()
    {
        root = null;
        size = 0;
    }

    public long getSize()
    {
        return size;
    }

    public java.util.Enumeration<V> valuesEn()
    {
        ItemEnumerationNode<V> valuesEnumRoot = null;
        if (root != null)
        {
            Node<K, V> node = root;
            while (node != null)
            {
                node = node.less;
            }
            valuesEnumRoot = new ItemEnumerationNode(node.value);

            ItemEnumerationNode<V> enumNode = valuesEnumRoot;
            while (node != null)
            {
                if (node.greater != null)
                {
                    node = node.greater;
                    while (node.less != null)
                    {
                        node = node.less;
                    }
                    enumNode.next = new ItemEnumerationNode<>(node.value);
                    enumNode = enumNode.next;
                }
                else
                {
                    do
                    {
                        if (node.parent != null)
                        {
                            if (node.parent.less == node)
                            {
                                node = node.parent;
                                enumNode.next = new ItemEnumerationNode<>(node.value);
                                enumNode = enumNode.next;
                                break;
                            }
                        }
                        node = node.parent;
                    }
                    while (node != null);
                }
            }
        }

        return new ItemEnumeration(valuesEnumRoot);
    }

    public java.util.Enumeration<K> keysEn()
    {
        ItemEnumerationNode<K> valuesEnumRoot = null;
        if (root != null)
        {
            Node<K, V> node = root;
            while (node != null)
            {
                node = node.less;
            }
            valuesEnumRoot = new ItemEnumerationNode(node.key);

            ItemEnumerationNode<K> enumNode = valuesEnumRoot;
            while (node != null)
            {
                if (node.greater != null)
                {
                    node = node.greater;
                    while (node.less != null)
                    {
                        node = node.less;
                    }
                    enumNode.next = new ItemEnumerationNode<>(node.key);
                    enumNode = enumNode.next;
                }
                else
                {
                    do
                    {
                        if (node.parent != null)
                        {
                            if (node.parent.less == node)
                            {
                                node = node.parent;
                                enumNode.next = new ItemEnumerationNode<>(node.key);
                                enumNode = enumNode.next;
                                break;
                            }
                        }
                        node = node.parent;
                    }
                    while (node != null);
                }
            }
        }

        return new ItemEnumeration(valuesEnumRoot);
    }

    public QIterator<K> keys()
    {
        return new KeysIterator(this);
    }

    public QIterator<V> values()
    {
        return new ValuesIterator(this);
    }

    public QIterator<K> reverseKeys()
    {
        return new KeysReverseIterator(this);
    }

    public QIterator<V> reverseValues()
    {
        return new ValuesReverseIterator(this);
    }

    public QIterator<K> keys(K key)
    {
        Node<K, V> startNode = findNode(key);
        QIterator<K> iter = null;
        if (startNode != null)
        {
            iter = new KeysIterator(this, startNode);
        }
        return iter;
    }

    public QIterator<V> values(K key)
    {
        Node<K, V> startNode = findNode(key);
        QIterator<V> iter = null;
        if (startNode != null)
        {
            iter = new ValuesIterator(this, startNode);
        }
        return iter;
    }

    public QIterator<K> reverseKeys(K key)
    {
        Node<K, V> startNode = findNode(key);
        QIterator<K> iter = null;
        if (startNode != null)
        {
            iter = new KeysReverseIterator(this, startNode);
        }
        return iter;
    }

    public QIterator<V> reverseValues(K key)
    {
        Node<K, V> startNode = findNode(key);
        QIterator<V> iter = null;
        if (startNode != null)
        {
            iter = new ValuesReverseIterator(this, startNode);
        }
        return iter;
    }

    @Override
    public QIterator<MapEntry<K, V>> iterator()
    {
        return new EntriesIterator(this);
    }

    public QIterator<MapEntry<K, V>> reverseIterator()
    {
        return new EntriesReverseIterator(this);
    }

    @SuppressWarnings("unchecked")
    public K[] keysArray()
    {
        K[] keys = null;
        if (size <= Integer.MAX_VALUE)
        {
            keys = (K[]) new Comparable[(int) size];

            if (root != null)
            {
                Node<K, V> node = root;
                while (node.less != null)
                {
                    node = node.less;
                }

                int index = 0;
                while (node != null)
                {
                    keys[index] = (K) node.key;
                    ++index;
                    if (node.greater != null)
                    {
                        node = node.greater;
                        while (node.less != null)
                        {
                            node = node.less;
                        }
                    }
                    else
                    {
                        while (node.parent != null && node.parent.greater == node)
                        {
                            node = node.parent;
                        }
                        node = node.parent;
                    }
                }
            }
        }

        return keys;
    }

    @SuppressWarnings("unchecked")
    public V[] valuesArray()
    {
        V[] values = null;
        if (size <= Integer.MAX_VALUE)
        {
            values = (V[]) new Object[(int) size];

            if (root != null)
            {
                Node<K, V> node = root;
                while (node.less != null)
                {
                    node = node.less;
                }

                int index = 0;
                while (node != null)
                {
                    values[index] = (V) node.value;
                    ++index;
                    if (node.greater != null)
                    {
                        node = node.greater;
                        while (node.less != null)
                        {
                            node = node.less;
                        }
                    }
                    else
                    {
                        while (node.parent != null && node.parent.greater == node)
                        {
                            node = node.parent;
                        }
                        node = node.parent;
                    }
                }
            }
        }

        return values;
    }

    @SuppressWarnings("unchecked")
    public K[] reverseKeysArray()
    {
        K[] keys = null;
        if (size <= Integer.MAX_VALUE)
        {
            keys = (K[]) new Comparable[(int) size];

            if (root != null)
            {
                Node<K, V> node = root;
                while (node.greater != null)
                {
                    node = node.greater;
                }

                int index = 0;
                while (node != null)
                {
                    keys[index] = (K) node.key;
                    ++index;
                    if (node.less != null)
                    {
                        node = node.less;
                        while (node.greater != null)
                        {
                            node = node.greater;
                        }
                    }
                    else
                    {
                        while (node.parent != null && node.parent.less == node)
                        {
                            node = node.parent;
                        }
                        node = node.parent;
                    }
                }
            }
        }

        return keys;
    }

    @SuppressWarnings("unchecked")
    public V[] reverseValuesArray()
    {
        V[] values = null;
        if (size <= Integer.MAX_VALUE)
        {
            values = (V[]) new Object[(int) size];

            if (root != null)
            {
                Node<K, V> node = root;
                while (node.greater != null)
                {
                    node = node.greater;
                }

                int index = 0;
                while (node != null)
                {
                    values[index] = (V) node.value;
                    ++index;
                    if (node.less != null)
                    {
                        node = node.less;
                        while (node.greater != null)
                        {
                            node = node.greater;
                        }
                    }
                    else
                    {
                        while (node.parent != null && node.parent.less == node)
                        {
                            node = node.parent;
                        }
                        node = node.parent;
                    }
                }
            }
        }

        return values;
    }
}
