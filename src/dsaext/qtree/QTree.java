package dsaext.qtree;

import dsaext.QIterator;
import dsaext.MapEntry;

/**
 * Quick balanced binary search tree
 *
 * @version 2016-03-21_001
 * @author  Robert Altnoeder (r.altnoeder@gmx.net)
 *
 * Copyright (C) 2011 - 2016 Robert ALTNOEDER
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
final public class QTree<K extends Comparable<K>, V>
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

    final private static class Node<K extends Comparable<K>, V>
    {
        /* key and value objects */
        protected K key;
        protected V value;

        /* references to parent and child nodes */
        protected Node<K, V> parent;
        protected Node<K, V> less;
        protected Node<K, V> greater;

        /* balance number */
        protected int balance;

        protected Node(K keyRef, V valRef)
        {
            key   = keyRef;
            value = valRef;

            balance  = 0;

            parent   = null;
            less     = null;
            greater  = null;
        }
    }

    final private static class QTreeEnumeration<T>
        implements java.util.Enumeration<T>
    {
        private QTreeEnumerationNode<T> next;
        private QTreeEnumerationNode<T> current;

        QTreeEnumeration(QTreeEnumerationNode<T> root)
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

            current = next;
            next    = next.next;
            if (current != null)
            {
                enumVal = current.enumVal;
            }

            return enumVal;
        }
    }

    final private class QTreeValuesIterator implements QIterator<V>
    {
        private QTree<K, V> container;
        private Node<K, V> current;
        // saved next node
        private Node<K, V> next;

        protected QTreeValuesIterator(QTree<K, V> containerRef)
        {
            container = containerRef;
            current = null;
            next = root;
            if (next != null)
            {
                while (next.less != null)
                {
                    next = next.less;
                }
            }
        }

        @Override
        final public long getSize()
        {
            return size;
        }

        @Override
        final public boolean hasNext()
        {
            return (next != null);
        }

        @Override
        final public V next()
        {
            V value = null;
            current = next;

            if (current != null)
            {
                if (next.greater != null)
                {
                    for (next = next.greater;
                         next.less != null;
                         next = next.less)
                    {
                        // intentional no-op block
                    }
                }
                else
                {
                    do
                    {
                        if (next.parent != null)
                        {
                            if (next.parent.less == next)
                            {
                                next = next.parent;
                                break;
                            }
                        }
                        next = next.parent;
                    }
                    while (next != null);
                }

                value = current.value;
            }

            return value;
        }

        @Override
        final public void remove()
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

    final private class QTreeKeysIterator implements QIterator<K>
    {
        private QTree<K, V> container;
        private Node<K, V> current;
        // saved next node
        private Node<K, V> next;

        protected QTreeKeysIterator(QTree<K, V> containerRef)
        {
            container = containerRef;
            current = null;
            next = root;
            if (next != null)
            {
                while (next.less != null)
                {
                    next = next.less;
                }
            }
        }

        @Override
        final public long getSize()
        {
            return size;
        }

        @Override
        final public boolean hasNext()
        {
            return (next != null);
        }

        @Override
        final public K next()
        {
            K key = null;
            current = next;

            if (current != null)
            {
                if (next.greater != null)
                {
                    for (next = next.greater;
                         next.less != null;
                         next = next.less)
                    {
                        // intentional no-op block
                    }
                }
                else
                {
                    do
                    {
                        if (next.parent != null)
                        {
                            if (next.parent.less == next)
                            {
                                next = next.parent;
                                break;
                            }
                        }
                        next = next.parent;
                    }
                    while (next != null);
                }

                key = current.key;
            }

            return key;
        }

        final public V getValue()
        {
            V value = null;
            if (current != null)
            {
                value = current.value;
            }
            return value;
        }

        @Override
        final public void remove()
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

    final private class QTreeEntriesIterator
        implements QIterator<MapEntry<K, V>>
    {
        private QTree<K, V> container;
        private Node<K, V> current;
        // saved next node
        private Node<K, V> next;

        protected QTreeEntriesIterator(QTree<K, V> containerRef)
        {
            container = containerRef;
            current = null;
            next = root;
            if (next != null)
            {
                while (next.less != null)
                {
                    next = next.less;
                }
            }
        }

        @Override
        final public long getSize()
        {
            return size;
        }

        @Override
        final public boolean hasNext()
        {
            return (next != null);
        }

        @Override
        final public MapEntry<K, V> next()
        {
            current = next;

            MapEntry<K, V> entry = null;

            if (current != null)
            {
                if (next.greater != null)
                {
                    for (next = next.greater;
                         next.less != null;
                         next = next.less)
                    {
                        // intentional no-op block
                    }
                }
                else
                {
                    do
                    {
                        if (next.parent != null)
                        {
                            if (next.parent.less == next)
                            {
                                next = next.parent;
                                break;
                            }
                        }
                        next = next.parent;
                    }
                    while (next != null);
                }

                entry = new MapEntry(current.key, current.value);
            }

            return entry;
        }

        @Override
        final public void remove()
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

    final private static class QTreeEnumerationNode<T>
    {
        QTreeEnumerationNode<T> next = null;
        T enumVal = null;
    }

    final public void insert(K key, V val)
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

    final public V get(K key)
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

    final public boolean contains(K key)
    {
        boolean found = false;

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
                found = true;
                break;
            }
        }

        return found;
    }

    final public void remove(K key)
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
                    for (replaceNode = rmNode.less;
                         replaceNode.greater != null;
                         replaceNode = replaceNode.greater)
                    {
                        // intentional no-op block
                    }
                }
                else
                {
                    for (replaceNode = rmNode.greater;
                         replaceNode.less != null;
                         replaceNode = replaceNode.less)
                    {
                        // intentional no-op block
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

    final public void clear()
    {
        root = null;
        size = 0;
    }

    final public long getSize()
    {
        return size;
    }

    final public java.util.Enumeration<V> valuesEn()
    {
        QTreeEnumerationNode<V> valsEnumRoot = null;

        if (root != null)
        {
            Node<K, V> node = root;
            while (node != null)
            {
                node = node.less;
            }
            valsEnumRoot = new QTreeEnumerationNode();
            valsEnumRoot.enumVal = node.value;

            QTreeEnumerationNode<V> enumNode = valsEnumRoot;
            while (node != null)
            {
                if (node.greater != null)
                {
                    for (node = node.greater;
                         node.less != null;
                         node = node.less)
                    {
                        // intentional no-op block
                    }
                    enumNode.next    = new QTreeEnumerationNode();
                    enumNode.enumVal = node.value;
                    enumNode         = enumNode.next;
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
                                enumNode.next    = new QTreeEnumerationNode();
                                enumNode.enumVal = node.value;
                                enumNode         = enumNode.next;
                                break;
                            }
                        }
                        node = node.parent;
                    }
                    while (node != null);
                }
            }
            enumNode.next = null;
        }
        else
        {
            valsEnumRoot = null;
        }

        java.util.Enumeration<V> valsEnum = new QTreeEnumeration(valsEnumRoot);

        return valsEnum;
    }

    final public java.util.Enumeration<K> keysEn()
    {
        QTreeEnumerationNode<K> keysEnumRoot = null;

        if (root != null)
        {
            Node<K, V> node = root;
            while (node.less != null)
            {
                node = node.less;
            }
            keysEnumRoot = new QTreeEnumerationNode();
            keysEnumRoot.enumVal = node.key;

            QTreeEnumerationNode<K> enumNode = keysEnumRoot;
            while (node != null)
            {
                if (node.greater != null)
                {
                    for (node = node.greater;
                         node.less != null;
                         node = node.less)
                    {
                        // intentional no-op block
                    }
                    enumNode.next    = new QTreeEnumerationNode();
                    enumNode.enumVal = node.key;
                    enumNode         = enumNode.next;
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
                                enumNode.next    = new QTreeEnumerationNode();
                                enumNode.enumVal = node.key;
                                enumNode         = enumNode.next;
                                break;
                            }
                        }
                        node = node.parent;
                    }
                    while (node != null);
                }
            }
            enumNode.next = null;
        }

        java.util.Enumeration<K> keysEnum = new QTreeEnumeration(keysEnumRoot);

        return keysEnum;
    }

    final public QIterator<K> keys()
    {
        return new QTreeKeysIterator(this);
    }

    final public QIterator<V> values()
    {
        return new QTreeValuesIterator(this);
    }

    @Override
    final public QIterator<MapEntry<K, V>> iterator()
    {
        return new QTreeEntriesIterator(this);
    }

    @SuppressWarnings("unchecked")
    final public K[] keysArray()
    {
        K[] keys = null;

        if (size <= (long) Integer.MAX_VALUE)
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
                    keys[index] = node.key;
                    ++index;
                    if (node.greater != null)
                    {
                        for (node = node.greater;
                             node.less != null;
                             node = node.less)
                        {
                            // intentional no-op block
                        }
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
                                    break;
                                }
                            }
                            node = node.parent;
                        }
                        while (node != null);
                    }
                }
            }
        }
        else
        {
            keys = null;
        }

        return keys;
    }

    @SuppressWarnings("unchecked")
    final public V[] valuesArray()
    {
        V[] vals = null;

        if (size <= (long) Integer.MAX_VALUE)
        {
            vals = (V[]) new Object[(int) size];

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
                    vals[index] = node.value;
                    ++index;
                    if (node.greater != null)
                    {
                        for (node = node.greater;
                             node.less != null;
                             node = node.less)
                        {
                            // intentional no-op block
                        }
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
                                    break;
                                }
                            }
                            node = node.parent;
                        }
                        while (node != null);
                    }
                }
            }
        }

        return vals;
    }
}
