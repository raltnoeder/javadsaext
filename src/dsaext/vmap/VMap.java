package dsaext.vmap;

/**
 * Vector map
 *
 * @version 2011-10-22_001
 * @author  R. Altnoeder (r.altnoeder@gmx.net)
 * 
 * Copyright (C) 2011, Robert ALTNOEDER
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
public class VMap<K extends Comparable, V>
{
    private Node<K, V> head;
    private Node<K, V> tail;
    private int size;
    
    public VMap()
    {
        head = null;
        tail = null;
        size = 0;
    }
    
    public void prepend(K key, V val)
    {
        Node<K, V> n;
        
        n = new Node<K, V>(key, val);
        n.next = head;
        
        head = n;
        if (tail == null)
        {
            tail = n;
        }
        ++size;
    }

    public void append(K key, V val)
    {
        Node<K, V> n;

        n = new Node<K, V>(key, val);

        if (head == null)
        {
            head = n;
        } else {
            tail.next = n;
        }
        tail = n;
        ++size;
    }

    public void insert(K key, V val, int idx)
    {
        Node<K, V> n;
        Node<K, V> p;
        int pos;

        n = new Node<K, V>(key, val);

        if (idx < 0 || idx > size)
        {
            throw new IndexOutOfBoundsException(
                "VMap.insert: invalid index " + idx);
        }

        if (idx == 0)
        {
            if (tail == null)
            {
                tail = n;
            }
            n.next = head;
            head = n;
        } else
        if (idx == size)
        {
            tail.next = n;
            tail = n;
        } else {
            for (pos = 1, p = head; pos < idx; ++pos, p = p.next)
            { }
            n.next = p.next;
            p.next = n;
        }
        ++size;
    }

    public void remove(K key)
    {
        Node<K, V> c;

        if (head == null)
        {
            return;
        }
        
        if (head.key.equals(key))
        {
            if (head == tail)
            {
                head = tail = null;
            } else {
                head = head.next;
            }
            --size;
        } else {
            for (c = head; c.next != null; c = c.next)
            {
                if (c.next.key.equals(key))
                {
                    if (c.next != tail)
                    {
                        c.next = c.next.next;
                    } else {
                        tail = c;
                        c.next = null;
                    }
                    --size;
                    break;
                }
            }
        }
    }

    public V get(K key)
    {
        Node<K, V> c;

        for (c = head; c != null; c = c.next)
        {
            if (c.key.equals(key))
            {
                return c.val;
            }
        }
        
        return null;
    }

    public int getSize()
    {
        return size;
    }

    public java.util.Iterator<K> keys()
    {
        return new VMapKeysIterator();
    }

    public java.util.Iterator<V> values()
    {
        return new VMapValuesIterator();
    }

    private class VMapKeysIterator implements java.util.Iterator<K>
    {
        private Node<K, V> c;

        protected VMapKeysIterator()
        {
            c = head;
        }

        public K next()
        {
            Node<K, V> p;

            if (c != null)
            {
                p = c;
                c = c.next;
                return p.key;
            }

            return null;
        }

        public boolean hasNext()
        {
            return (c != null);
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    private class VMapValuesIterator implements java.util.Iterator<V>
    {
        private Node<K, V> c;

        protected VMapValuesIterator()
        {
            c = head;
        }

        public V next()
        {
            Node<K, V> p;

            if (c != null)
            {
                p = c;
                c = c.next;
                return p.val;
            }

            return null;
        }

        public boolean hasNext()
        {
            return (c != null);
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    private class Node<K extends Comparable, V>
    {
        protected K key;
        protected V val;

        protected Node<K, V> next;

        Node(K key, V val)
        {
            this.key = key;
            this.val = val;

            next = null;
        }
    }
}
