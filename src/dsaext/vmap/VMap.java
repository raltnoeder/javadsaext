package dsaext.vmap;

import dsaext.MapEntry;

/**
 * Vector map
 *
 * @version 2016-03-21_001
 * @author  R. Altnoeder (r.altnoeder@gmx.net)
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
final public class VMap<K extends Comparable<K>, V>
    implements Iterable<MapEntry<K, V>>
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
        Node<K, V> insNode = new Node(key, val);

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
        Node<K, V> insNode = new Node(key, val);

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
        if (idx < 0 || idx > size)
        {
            throw new IndexOutOfBoundsException("VMap.insert(): invalid index " + idx);
        }

        Node<K, V> insNode = new Node(key, val);
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
            Node<K, V> prevNode = head;
            for (long pos = 1; pos < idx; ++pos)
            {
                prevNode = prevNode.next;
            }
            insNode.next  = prevNode.next;
            prevNode.next = insNode;
        }
        ++size;
    }

    public void remove(K key)
    {
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
                for (Node<K, V> prevNode = head; prevNode.next != null; prevNode = prevNode.next)
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
        V value = null;
        for (Node<K, V> node = head; node != null; node = node.next)
        {
            if (node.key.equals(key))
            {
                value = node.value;
                break;
            }
        }
        return value;
    }

    public int getSize()
    {
        return size;
    }

    public void clear()
    {
        head = null;
        tail = null;
        size = 0;
    }

    @SuppressWarnings("unchecked")
    public K[] keysArray()
    {
        K[] keys = null;
        if (size <= Integer.MAX_VALUE)
        {
            keys = (K[]) new Object[size];
            Node<K, V> node = head;
            for (int index = 0; index < size; ++index)
            {
                keys[index] = node.key;
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
            values = (V[]) new Object[size];
            Node<K, V> node = head;
            for (int index = 0; index < size; ++index)
            {
                values[index] = node.value;
            }
        }
        return values;
    }

    public java.util.Iterator<K> keys()
    {
        return new VMapKeysIterator(this);
    }

    public java.util.Iterator<V> values()
    {
        return new VMapValuesIterator(this);
    }

    @Override
    public java.util.Iterator<MapEntry<K, V>> iterator()
    {
        return new VMapEntriesIterator(this);
    }

    final private class VMapKeysIterator implements java.util.Iterator<K>
    {
        private VMap<K, V> container;
        private Node<K, V> current;
        private Node<K, V> next;

        protected VMapKeysIterator(VMap<K, V> containerRef)
        {
            container = containerRef;
            current = null;
            next = head;
        }

        @Override
        public boolean hasNext()
        {
            return (next != null);
        }

        @Override
        public K next()
        {
            K key = null;
            current = next;
            if (current != null)
            {
                next = next.next;
                key = current.key;
            }
            return key;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    final private class VMapValuesIterator implements java.util.Iterator<V>
    {
        private VMap<K, V> container;
        private Node<K, V> current;
        private Node<K, V> next;

        protected VMapValuesIterator(VMap<K, V> containerRef)
        {
            container = containerRef;
            current = null;
            next = head;
        }

        @Override
        public boolean hasNext()
        {
            return (next != null);
        }

        @Override
        public V next()
        {
            V value = null;
            current = next;
            if (current != null)
            {
                next = next.next;
                value = current.value;
            }
            return value;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    final private class VMapEntriesIterator
        implements java.util.Iterator<MapEntry<K, V>>
    {
        private VMap<K, V> container;
        private Node<K, V> current;
        private Node<K, V> next;

        protected VMapEntriesIterator(VMap<K, V> containerRef)
        {
            container = containerRef;
            current = null;
            next = head;
        }

        @Override
        public boolean hasNext()
        {
            return (next != null);
        }

        @Override
        public MapEntry<K, V> next()
        {
            MapEntry<K, V> entry = null;
            current = next;
            if (current != null)
            {
                next = next.next;
                entry = new MapEntry<>(current.key, current.value);
            }
            return entry;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    final private static class Node<K extends Comparable<K>, V>
    {
        protected K          key;
        protected V          value;
        protected Node<K, V> next;

        Node(K keyRef, V valueRef)
        {
            this.key   = keyRef;
            this.value = valueRef;
            next       = null;
        }
    }
}
