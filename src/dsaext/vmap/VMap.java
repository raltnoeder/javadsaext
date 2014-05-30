package dsaext.vmap;

/**
 * Vector map
 *
 * @version 2014-05-30_001
 * @author  R. Altnoeder (r.altnoeder@gmx.net)
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
final public class VMap<K extends Comparable, V>
{
    private Node<K, V> head;
    private Node<K, V> tail;
    private int        size;

    public VMap()
    {
        head = null;
        tail = null;
        size = 0;
    }

    public void prepend(K key, V val)
    {
        Node<K, V> insNode;

        insNode = new Node(key, val);

        insNode.next = head;
        head         = insNode;
        if (tail == null)
        {
            tail = insNode;
        }

        ++size;
    }

    public void append(K key, V val)
    {
        Node<K, V> insNode;

        insNode = new Node(key, val);

        if (tail == null)
        {
            head = insNode;
        }
        else
        {
            tail.next = insNode;
        }
        tail = insNode;

        ++size;
    }

    public void insert(K key, V val, int idx)
    {
        Node<K, V> insNode;
        Node<K, V> prevNode;
        int        pos;

        insNode = new Node(key, val);

        if (idx < 0 || idx > size)
        {
            throw new IndexOutOfBoundsException(
                "VMap.insert(): invalid index " + idx);
        }

        if (idx == 0)
        {
            if (tail == null)
            {
                tail = insNode;
            }
            insNode.next = head;
            head         = insNode;
        }
        else
        if (idx == size)
        {
            tail.next = insNode;
            tail      = insNode;
        }
        else
        {
            for (pos = 1, prevNode = head;
                 pos < idx;
                 ++pos, prevNode = prevNode.next)
            {
                /* intentional no-op block */
            }
            insNode.next  = prevNode.next;
            prevNode.next = insNode;
        }
        ++size;
    }

    public void remove(K key)
    {
        Node<K, V> prevNode;

        if (head != null)
        {
            if (head.key.equals(key))
            {
                if (head == tail)
                {
                    head = null;
                    tail = null;
                }
                else
                {
                    head = head.next;
                }
                --size;
            }
            else
            {
                for (prevNode = head;
                     prevNode.next != null;
                     prevNode = prevNode.next)
                {
                    if (prevNode.next.key.equals(key))
                    {
                        if (prevNode.next != tail)
                        {
                            prevNode.next = prevNode.next.next;
                        }
                        else
                        {
                            tail          = prevNode;
                            prevNode.next = null;
                        }
                        --size;
                        break;
                    }
                }
            }
        }
    }

    public V get(K key)
    {
        Node<K, V> node;

        for (node = head; node != null; node = node.next)
        {
            if (node.key.equals(key))
            {
                return node.val;
            }
        }

        return null;
    }

    public int getSize()
    {
        return size;
    }

    @SuppressWarnings("unchecked")
    public K[] keysArray()
    {
        Node<K, V> node;
        K[]        values;
        int        idx;

        values = (K[]) new Object[size];
        for (node = head, idx = 0; idx < size; node = node.next, ++idx)
        {
            values[idx] = node.key;
        }

        return values;
    }

    @SuppressWarnings("unchecked")
    public V[] valuesArray()
    {
        Node<K, V> node;
        V[]        values;
        int        idx;

        values = (V[]) new Object[size];
        for (node = head, idx = 0; idx < size; node = node.next, ++idx)
        {
            values[idx] = node.val;
        }

        return values;
    }

    public java.util.Iterator<K> keys()
    {
        return new VMapKeysIterator();
    }

    public java.util.Iterator<V> values()
    {
        return new VMapValuesIterator();
    }

    final private class VMapKeysIterator implements java.util.Iterator<K>
    {
        private Node<K, V> nextNode;

        protected VMapKeysIterator()
        {
            nextNode = head;
        }

        @Override
        public boolean hasNext()
        {
            return (nextNode != null);
        }

        @Override
        public K next()
        {
            Node<K, V> retNode;

            if (nextNode != null)
            {
                retNode  = nextNode;
                nextNode = nextNode.next;
                return retNode.key;
            }

            return null;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    final private class VMapValuesIterator implements java.util.Iterator<V>
    {
        private Node<K, V> nextNode;

        protected VMapValuesIterator()
        {
            nextNode = head;
        }

        @Override
        public boolean hasNext()
        {
            return (nextNode != null);
        }

        @Override
        public V next()
        {
            Node<K, V> retNode;

            if (nextNode != null)
            {
                retNode  = nextNode;
                nextNode = nextNode.next;
                return retNode.val;
            }

            return null;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    final private static class Node<K extends Comparable, V>
    {
        protected K          key;
        protected V          val;
        protected Node<K, V> next;

        Node(K key, V val)
        {
            this.key = key;
            this.val = val;
            next     = null;
        }
    }
}
