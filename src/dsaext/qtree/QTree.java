package dsaext.qtree;

import dsaext.QIterator;

/**
 * Quick balanced binary search tree
 *
 * @version 2014-05-30_001
 * @author  Robert Altnoeder (r.altnoeder@gmx.net)
 *
 * Copyright (C) 2011, 2014 Robert ALTNOEDER
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
{
    private Node<K, V> root;
    private long size;

    final private static int STATE_ENTER_L = 0;
    final private static int STATE_ENTER_H = 1;
    final private static int STATE_LEAVE   = 2;

    public QTree()
    {
        root = null;
        size = 0;
    }

    final private static class Node<K extends Comparable<K>, V>
    {
        /* key and value objects */
        protected K key;
        protected V val;

        /* references to parent and child nodes */
        protected Node<K, V> parent;
        protected Node<K, V> less;
        protected Node<K, V> greater;

        /* balance number */
        protected int balance;

        protected Node(K key, V val)
        {
            this.key = key;
            this.val = val;

            balance  = 0;

            parent   = null;
            less     = null;
            greater  = null;
        }
    }

    final private static class QTreeEnumeration<T>
        implements java.util.Enumeration<T>
    {
        private QTreeEnumerationNode<T> node;
        private QTreeEnumerationNode<T> retNode;

        QTreeEnumeration(QTreeEnumerationNode<T> root)
        {
            node = root;
        }

        @Override
        public boolean hasMoreElements()
        {
            return (node != null);
        }

        @Override
        public T nextElement()
        {
            retNode = node;
            node    = node.next;
            if (retNode != null)
            {
                return retNode.enumVal;
            }
            else
            {
                return null;
            }
        }
    }

    final private class QTreeValuesIterator implements QIterator<V>
    {
        /* saved next node */
        private Node<K, V> next;

        protected QTreeValuesIterator()
        {
            if (root != null)
            {
                for (next = root; next.less != null; next = next.less)
                {
                    /* intentional no-op block */
                }
            }
            else
            {
                next = null;
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
            Node<K, V> retNode;
            Node<K, V> nextNode;

            retNode = next;

            if (retNode != null)
            {
                nextNode = retNode;
                if (nextNode.greater != null)
                {
                    for (nextNode = nextNode.greater;
                         nextNode.less != null;
                         nextNode = nextNode.less)
                    {
                        /* intentional no-op block */
                    }
                }
                else
                {
                    do
                    {
                        if (nextNode.parent != null)
                        {
                            if (nextNode.parent.less == nextNode)
                            {
                                nextNode = nextNode.parent;
                                break;
                            }
                        }
                        nextNode = nextNode.parent;
                    }
                    while (nextNode != null);
                }
                next = nextNode;

                return retNode.val;
            }

            return null;
        }

        @Override
        final public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    final private class QTreeKeysIterator implements QIterator<K>
    {
        /* saved next node */
        private Node<K, V> next;

        protected QTreeKeysIterator()
        {
            if (root != null)
            {
                for (next = root; next.less != null; next = next.less)
                {
                    /* intentional no-op block */
                }
            }
            else
            {
                next = null;
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
            Node<K, V> retNode;
            Node<K, V> nextNode;

            retNode = next;

            if (retNode != null)
            {
                nextNode = retNode;
                if (nextNode.greater != null)
                {
                    for (nextNode = nextNode.greater;
                         nextNode.less != null;
                         nextNode = nextNode.less)
                    {
                        /* intentional no-op block */
                    }
                }
                else
                {
                    do
                    {
                        if (nextNode.parent != null)
                        {
                            if (nextNode.parent.less == nextNode)
                            {
                                nextNode = nextNode.parent;
                                break;
                            }
                        }
                        nextNode = nextNode.parent;
                    }
                    while (nextNode != null);
                }
                next = nextNode;

                return retNode.key;
            }

            return null;
        }

        final public V getValue()
        {
            if (next != null)
            {
                return next.val;
            }

            return null;
        }

        @Override
        final public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    final private static class QTreeEnumerationNode<T>
    {
        QTreeEnumerationNode<T> next;

        T enumVal;
    }

    final public void insert(K key, V val)
    {
        Node<K, V> insNode;
        Node<K, V> parentNode;
        int        rc;

        insNode = new Node<K, V>(key, val);

        if (root == null)
        {
            root = insNode;
            ++size;
        }
        else
        {
            parentNode = root;
            for (;;)
            {
               rc = insNode.key.compareTo(parentNode.key);
               if (rc < 0)
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
               if (rc > 0)
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
                   parentNode.key = insNode.key;
                   parentNode.val = insNode.val;
                   return;
               }
            }

            /* update balance and perform rotations */
            do
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
            while (parentNode != null);
        }
    }

    final public V get(K key)
    {
        Node<K, V> node;
        int rc;

        if (root == null)
        {
            return null;
        }
        else
        {
            node = root;
            while (node != null)
            {
               rc = key.compareTo(node.key);
               if (rc < 0)
               {
                   node = node.less;
               }
               else
               if (rc > 0)
               {
                   node = node.greater;
               }
               else
               {
                   return node.val;
               }
            }
        }

        return null;
    }

    final public boolean contains(K key)
    {
        Node<K, V> crt;
        int  rc;

        crt = root;
        while (crt != null)
        {
            rc = key.compareTo(crt.key);
            if (rc == 0)
            {
                return true;
            }
            else
            if (rc < 0)
            {
                crt = crt.less;
            }
            else
            {
                crt = crt.greater;
            }
        }

        return false;
    }

    final public void remove(K key)
    {
        Node<K, V> rmNode;
        Node<K, V> repNode;
        Node<K, V> rotNode;
        int        dir;
        int        rc;

        if (root != null)
        {
            /* find the node to be deleted */
            rmNode = root;
            for (;;)
            {
                rc = key.compareTo(rmNode.key);
                if (rc < 0)
                {
                    rmNode = rmNode.less;
                }
                else
                if (rc > 0)
                {
                    rmNode = rmNode.greater;
                }
                else
                {
                    break;
                }

                if (rmNode == null)
                {
                    return;
                }
            }
            --size;

            if (rmNode.less == null && rmNode.greater == null)
            {
                /* leaf node */
                if (root == rmNode)
                {
                    /* root node leaf */
                    root    = null;
                    rotNode = null;
                    dir     = 0;
                }
                else
                {
                    /* non-root node leaf */
                    rotNode = rmNode.parent;
                    if (rotNode.less == rmNode)
                    {
                        /* node to remove is in the left subtree *
                         * of its parent                         */

                        /* save direction */
                        dir = -1;
                        rotNode.less = null;
                    }
                    else
                    {
                        /* node to remove is in the right subtree *
                         * of its parent                          */

                        /* save direction */
                        dir = 1;
                        rotNode.greater = null;
                    }
                }
            }
            else
            {
                /* not a leaf node, removal by replacement                       *
                 * at least one child, or a child and a subtree, or two subtrees *
                 * find replacement node                                         */
                if (rmNode.balance == -1)
                {
                    for (repNode = rmNode.less; repNode.greater != null; repNode = repNode.greater)
                    {
                        /* intentional no-op block */
                    }
                }
                else
                {
                    for (repNode = rmNode.greater; repNode.less != null; repNode = repNode.less)
                    {
                        /* intentional no-op block */
                    }
                }
                rotNode = repNode.parent;
                if (rotNode.less == repNode)
                {
                    /* node to remove is in the left subtree *
                     * of its parent                         */

                    /* save direction */
                    dir = -1;

                    if (repNode.less != null)
                    {
                        /* replace node by its left child */
                        rotNode.less = repNode.less;
                        repNode.less.parent = rotNode;
                    }
                    else
                    if (repNode.greater != null)
                    {
                        /* replace node by its right child */
                        rotNode.less = repNode.greater;
                        repNode.greater.parent = rotNode;
                    }
                    else
                    {
                        /* non-root leaf node */
                        rotNode.less = null;
                    }
                }
                else
                {
                    /* node to remove is in the right subtree *
                     * of its parent                          */

                    /* save direction */
                    dir = 1;

                    if (repNode.less != null)
                    {
                        /* replace node by its left child */
                        rotNode.greater = repNode.less;
                        repNode.less.parent = rotNode;
                    }
                    else
                    if (repNode.greater != null)
                    {
                        /* replace node by its right child */
                        rotNode.greater = repNode.greater;
                        repNode.greater.parent = rotNode;
                    }
                    else
                    {
                        /* non-root leaf node */
                        rotNode.greater = null;
                    }
                }

                rmNode.key = repNode.key;
                rmNode.val = repNode.val;
            }

            /* update balance and perform rotations */
            for (; rotNode != null; rotNode = rotNode.parent)
            {
                if (dir < 0)
                {
                    /* node was removed from left subtree */
                    if (++rotNode.balance == 1)
                    {
                        break;
                    }
                }
                else
                {
                    /* node was removed from right subtree */
                    if (--rotNode.balance == -1)
                    {
                        break;
                    }
                }

                if (rotNode.parent != null)
                {
                    if (rotNode.parent.less == rotNode)
                    {
                        dir = -1;
                    }
                    else
                    {
                        dir = 1;
                    }
                }

                /* update balance and perform rotations */
                if (rotNode.balance == -2)
                {
                    repNode = rotNode.less;
                    /* 0 or -1 */
                    if (repNode.balance <= 0)
                    {
                        /* rotate R */
                        repNode.parent = rotNode.parent;
                        if (rotNode.parent != null)
                        {
                            if (rotNode.parent.less == rotNode)
                            {
                                rotNode.parent.less = repNode;
                            }
                            else
                            {
                                rotNode.parent.greater = repNode;
                            }
                        }
                        else
                        {
                            root = repNode;
                        }

                        rotNode.less = repNode.greater;
                        if (repNode.greater != null)
                        {
                            repNode.greater.parent = rotNode;
                        }

                        repNode.greater = rotNode;
                        rotNode.parent  = repNode;

                        if (repNode.balance == 0)
                        {
                            rotNode.balance = -1;
                            repNode.balance = 1;
                            break;
                        }
                        else
                        {
                            rotNode.balance = 0;
                            repNode.balance = 0;
                        }
                    }
                    else
                    {
                        /* rotate LR */
                        if (repNode.greater.balance == -1)
                        {
                            repNode.balance = 0;
                            rotNode.balance = 1;
                        }
                        else
                        if (repNode.greater.balance == 1)
                        {
                            repNode.balance = -1;
                            rotNode.balance = 0;
                        }
                        else
                        {
                            repNode.balance = 0;
                            rotNode.balance = 0;
                        }
                        repNode.greater.balance = 0;

                        repNode.parent        = repNode.greater;
                        repNode.greater       = repNode.greater.less;
                        repNode.parent.less   = repNode;
                        rotNode.less          = repNode.parent.greater;
                        repNode.parent.parent = rotNode.parent;
                        if (repNode.greater != null)
                        {
                            repNode.greater.parent = repNode;
                        }
                        if (rotNode.less != null)
                        {
                            rotNode.less.parent = rotNode;
                        }

                        if (rotNode.parent != null)
                        {
                            if (rotNode.parent.less == rotNode)
                            {
                                rotNode.parent.less = repNode.parent;
                            }
                            else
                            {
                                rotNode.parent.greater = repNode.parent;
                            }
                        }
                        else
                        {
                            root = repNode.parent;
                        }

                        rotNode.parent         = repNode.parent;
                        repNode.parent.greater = rotNode;
                    }
                    rotNode = rotNode.parent;
                    /* end of R / LR rotations */
                }
                else
                if (rotNode.balance == 2)
                {
                    repNode = rotNode.greater;
                    /* 0 or 1 */
                    if (repNode.balance >= 0)
                    {
                        /* rotate L */
                        repNode.parent = rotNode.parent;
                        if (rotNode.parent != null)
                        {
                            if (rotNode.parent.less == rotNode)
                            {
                                rotNode.parent.less = repNode;
                            }
                            else
                            {
                                rotNode.parent.greater = repNode;
                            }
                        }
                        else
                        {
                            root = repNode;
                        }

                        rotNode.greater = repNode.less;
                        if (repNode.less != null)
                        {
                            repNode.less.parent = rotNode;
                        }

                        repNode.less   = rotNode;
                        rotNode.parent = repNode;
                        if (repNode.balance == 0)
                        {
                            rotNode.balance = 1;
                            repNode.balance = -1;
                            break;
                        }
                        else
                        {
                            rotNode.balance = 0;
                            repNode.balance = 0;
                        }
                    }
                    else
                    {
                        /* rotate RL */
                        if (repNode.less.balance == -1)
                        {
                            repNode.balance = 1;
                            rotNode.balance = 0;
                        }
                        else
                        if (repNode.less.balance == 1)
                        {
                            repNode.balance = 0;
                            rotNode.balance = -1;
                        }
                        else
                        {
                            repNode.balance = 0;
                            rotNode.balance = 0;
                        }
                        repNode.less.balance = 0;

                        repNode.parent         = repNode.less;
                        repNode.less           = repNode.less.greater;
                        repNode.parent.greater = repNode;
                        rotNode.greater        = repNode.parent.less;
                        repNode.parent.parent  = rotNode.parent;
                        if (repNode.less != null)
                        {
                            repNode.less.parent = repNode;
                        }
                        if (rotNode.greater != null)
                        {
                            rotNode.greater.parent = rotNode;
                        }

                        if (rotNode.parent != null)
                        {
                            if (rotNode.parent.less == rotNode)
                            {
                                rotNode.parent.less = repNode.parent;
                            }
                            else
                            {
                                rotNode.parent.greater = repNode.parent;
                            }
                        }
                        else
                        {
                            root = repNode.parent;
                        }

                        rotNode.parent      = repNode.parent;
                        repNode.parent.less = rotNode;
                    }
                    rotNode = rotNode.parent;
                    /* end of L / RL rotations */
                }
            } /* end update */
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
        java.util.Enumeration<V> valsEnum;
        QTreeEnumerationNode<V>  valsEnumRoot;
        QTreeEnumerationNode<V>  enumNode;

        Node<K, V> node;

        if (root != null)
        {
            for (node = root; node.less != null; node = node.less)
            {
                /* intentional no-op block */
            }
            valsEnumRoot = new QTreeEnumerationNode();
            valsEnumRoot.enumVal = node.val;

            for (enumNode = valsEnumRoot; node != null;)
            {
                if (node.greater != null)
                {
                    for (node = node.greater;
                         node.less != null;
                         node = node.less)
                    {
                        /* intentional no-op block */
                    }
                    enumNode.next    = new QTreeEnumerationNode();
                    enumNode.enumVal = node.val;
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
                                enumNode.enumVal = node.val;
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

        valsEnum = new QTreeEnumeration(valsEnumRoot);

        return valsEnum;
    }

    final public java.util.Enumeration<K> keysEn()
    {
        java.util.Enumeration<K> keysEnum;
        QTreeEnumerationNode<K>  keysEnumRoot;
        QTreeEnumerationNode<K>  enumNode;

        Node<K, V> node;

        if (root != null)
        {
            for (node = root; node.less != null; node = node.less)
            {
                /* intentional no-op block */
            }
            keysEnumRoot = new QTreeEnumerationNode();
            keysEnumRoot.enumVal = node.key;

            for (enumNode = keysEnumRoot; node != null;)
            {
                if (node.greater != null)
                {
                    for (node = node.greater;
                         node.less != null;
                         node = node.less)
                    {
                        /* intentional no-op block */
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
        else
        {
            keysEnumRoot = null;
        }

        keysEnum = new QTreeEnumeration(keysEnumRoot);

        return keysEnum;
    }

    final public QIterator<K> keys()
    {
        return new QTreeKeysIterator();
    }

    final public QIterator<V> values()
    {
        return new QTreeValuesIterator();
    }

    @SuppressWarnings("unchecked")
    final public K[] keysArray()
    {
        K[] keys;
        int idx;

        Node<K, V> node;

        if (size <= (long) Integer.MAX_VALUE)
        {
            keys = (K[]) new Comparable[(int) size];

            if (root != null)
            {
                for (node = root; node.less != null; node = node.less)
                {
                    /* intentional no-op block */
                }

                for (idx = 0; node != null;)
                {
                    keys[idx++] = node.key;
                    if (node.greater != null)
                    {
                        for (node = node.greater;
                             node.less != null;
                             node = node.less)
                        {
                            /* intentional no-op block */
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
        V[] vals;
        int idx;

        Node<K, V> node;

        if (size <= (long) Integer.MAX_VALUE)
        {
            vals = (V[]) new Object[(int) size];

            if (root != null)
            {
                for (node = root; node.less != null; node = node.less)
                {
                    /* intentional no-op block */
                }

                for (idx = 0; node != null;)
                {
                    vals[idx++] = node.val;
                    if (node.greater != null)
                    {
                        for (node = node.greater;
                             node.less != null;
                             node = node.less)
                        {
                            /* intentional no-op block */
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
            vals = null;
        }

        return vals;
    }



    /* ========== DEBUG CODE ========== */

    void dump(Node<K, V> d)
    {
        String txt;

        System.out.println("DUMP Node " + d.key.toString());
        if (d.parent != null)
        {
            txt = d.parent.key.toString();
        }
        else
        {
            txt = "null";
        }
        System.out.println("  p: " + txt);

        if (d.less != null)
        {
            txt = d.less.key.toString();
        }
        else
        {
            txt = "null";
        }
        System.out.println("  l: " + txt);

        if (d.greater != null)
        {
            txt = d.greater.key.toString();
        }
        else
        {
            txt = "null";
        }
        System.out.println("  r: " + txt);

        System.out.println("  blnc: " + d.balance);
        System.out.println();
    }

    public void near()
    {
        Node<K, V> c;
        Node<K, V> p;

        int level;
        int ctr;

        c = root;
        p = null;

        /* the tree can not have a height of more than 92 with
         * (2 ^ 64 - 1) elements */
        level = 93;
        ctr = 1;

        for (; c != null;)
        {
            if (p == null)
            {
                if (c.less != null)
                {
                    /* enter left subtree */
                    c = c.less;
                    ++ctr;
                }
                else
                if (c.greater != null)
                {
                    /* if there is no left subtree, enter right subtree */
                    c = c.greater;
                    ++ctr;
                }
                else
                {
                    /* leaf, go back up */
                    p = c;
                    c = c.parent;
                    if (ctr < level)
                    {
                        level = ctr;
                    }
                    --ctr;
                }
            }
            else
            {
                if (c.less == p)
                {
                    if (c.greater != null)
                    {
                        /* enter right subtree */
                        p = null;
                        c = c.greater;
                        ++ctr;
                    }
                    else
                    {
                        /* if there is no right subtree, go further up */
                        p = c;
                        c = c.parent;
                        --ctr;
                    }
                }
                else
                {
                    /* go further up */
                    p = c;
                    c = c.parent;
                    --ctr;
                }
            }
        }
        if (level >= 93)
        {
            level = 0;
        }
        System.out.println("Nearest leaf: " + level);
    }

    public void far()
    {
        Node<K, V> c;
        Node<K, V> p;

        int level;
        int ctr;

        c = root;
        p = null;

        level = 0;
        ctr = 1;

        for (; c != null;)
        {
            if (p == null)
            {
                if (c.less != null)
                {
                    /* enter left subtree */
                    c = c.less;
                    ++ctr;
                }
                else
                if (c.greater != null)
                {
                    /* if there is no left subtree, enter right subtree */
                    c = c.greater;
                    ++ctr;
                }
                else
                {
                    /* leaf, go back up */
                    p = c;
                    c = c.parent;
                    if (ctr > level)
                    {
                        level = ctr;
                    }
                    --ctr;
                }
            }
            else
            {
                if (c.less == p)
                {
                    if (c.greater != null)
                    {
                        /* enter right subtree */
                        p = null;
                        c = c.greater;
                        ++ctr;
                    }
                    else
                    {
                        /* if there is no right subtree, go further up */
                        p = c;
                        c = c.parent;
                        --ctr;
                    }
                }
                else
                {
                    /* go further up */
                    p = c;
                    c = c.parent;
                    --ctr;
                }
            }
        }
        System.out.println("Farthest leaf: " + level);
    }

    public void test(boolean viewBlnc)
    {
        Node<K, V> crt;
        Node<K, V> p;

        int debugctr;

        crt = root;
        p = null;

        debugctr = 0;

        System.out.print("R");
        for (; crt != null && debugctr < 200; ++debugctr)
        {
            if (p == null)
            {
                System.out.print("{ " + crt.key.toString());
                if (viewBlnc)
                {
                    System.out.print("(" + crt.balance + ") ");
                }
                else
                {
                    System.out.print(" ");
                }

                if (crt.less != null)
                {
                    /* enter left subtree */
                    System.out.print("<");
                    crt = crt.less;
                }
                else
                if (crt.greater != null)
                {
                    /* if there is no left subtree, enter right subtree */
                    System.out.print(">");
                    crt = crt.greater;
                }
                else
                {
                    /* leaf, go back up */
                    System.out.print("} ");
                    p = crt;
                    crt = crt.parent;
                }
            }
            else
            {
                if (crt.less == p)
                {
                    if (crt.greater != null)
                    {
                        /* enter right subtree */
                        System.out.print(">");
                        p = null;
                        crt = crt.greater;
                    }
                    else
                    {
                        /* if there is no right subtree, go further up */
                        System.out.print("} ");
                        p = crt;
                        crt = crt.parent;
                    }
                }
                else
                {
                    /* go further up */
                    System.out.print("} ");
                    p = crt;
                    crt = crt.parent;
                }
            }
        }
        System.out.println();
        System.out.flush();
    }

}
