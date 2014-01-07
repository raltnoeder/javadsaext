package dsaext.vlist;

/**
 * Vector list
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
public class VList<V>
{
    private int size;
    private Node<V> head;
    private Node<V> tail;

    public VList()
    {
        head = null;
        tail = null;
        size = 0;
    }

    public void prepend(V val)
    {
        Node<V> n;

        n = new Node<V>(val);
        if (head == null)
        {
            head = tail = n;
        } else {
            n.next = head;
            head = n;
        }

        ++size;
    }

    public void append(V val)
    {
        Node<V> n;

        n = new Node<V>(val);
        if (tail == null)
        {
            head = tail = n;
        } else {
            tail.next = n;
            tail = n;
        }

        ++size;
    }

    public void remove(long idx) throws IndexOutOfBoundsException
    {
        Node<V> c;
        Node<V> p;
        long    pos;

        if (head == null || idx < 0 || idx > (size - 1))
        {
            throw new IndexOutOfBoundsException(
                "VList.remove(): invalid index " + idx);
        }

        if (idx == 0)
        {
            if (head == tail)
            {
                head = tail = null;
            } else {
                head = head.next;
            }
        } else {
            pos = 1;
            p = null;
            for (p = head, c = head.next; pos < idx; ++pos, p = c, c = c.next)
            { }
            if (c != tail)
            {
                p.next = c.next;
            } else{
                tail = p;
                p.next = null;
            }
        }

        --size;
    }

    public void insert(V val, long idx) throws IndexOutOfBoundsException
    {
        Node<V> n;
        Node<V> p;
        long    pos;

        n = new Node<V>(val);

        if (idx < 0 || idx > size)
        {
            throw new IndexOutOfBoundsException(
                "VList.insert(): index " + idx);
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

    public int getSize()
    {
        return size;
    }

    @SuppressWarnings("unchecked")
    public V[] toArray()
    {
        Node<V> n;
        V[] values;
        int idx;

        values = (V[]) new Object[size];
        for (n = head, idx = 0; idx < size; n = n.next, ++idx)
        {
            values[idx] = n.val;
        }

        return values;
    }

    public java.util.Iterator iterator()
    {
        return new VListIterator();
    }

    private static class Node<V>
    {
        protected V val;
        protected Node<V> next;

        Node(V val)
        {
            this.val = val;
            next = null;
        }
    }

    private class VListIterator implements java.util.Iterator<V>
    {
        private Node<V> c;

        VListIterator()
        {
            this.c = head;
        }

        public boolean hasNext()
        {
            return (c != null);
        }

        public V next()
        {
            Node<V> n;

            if (c != null)
            {
                n = c;
                c = c.next;
                return n.val;
            }
            
            return null;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
