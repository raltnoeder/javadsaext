package dsaext.vlist;

/**
 * Vector list
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
final public class VList<V>
{
    private int     size;
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
        Node<V> insNode;

        insNode = new Node(val);

        insNode.next = head;
        head         = insNode;
        if (tail == null)
        {
            tail = insNode;
        }

        ++size;
    }

    public void append(V val)
    {
        Node<V> insNode;

        insNode = new Node(val);

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

    public void insert(V val, long idx) throws IndexOutOfBoundsException
    {
        Node<V> insNode;
        Node<V> prevNode;
        long    pos;

        insNode = new Node(val);

        if (idx < 0 || idx > size)
        {
            throw new IndexOutOfBoundsException(
                "VList.insert(): index " + idx);
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

    public void remove(long idx) throws IndexOutOfBoundsException
    {
        Node<V> prevNode;
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
                head = null;
                tail = null;
            }
            else
            {
                head = head.next;
            }
        }
        else
        {
            for (pos = 1, prevNode = head;
                 pos < idx;
                 ++pos, prevNode = prevNode.next)
            {
                /* intentional no-op block */
            }
            if (prevNode.next != tail)
            {
                prevNode.next = prevNode.next.next;
            }
            else
            {
                tail          = prevNode;
                prevNode.next = null;
            }
        }

        --size;
    }

    public int getSize()
    {
        return size;
    }

    @SuppressWarnings("unchecked")
    public V[] toArray()
    {
        Node<V> node;
        V[]     values;
        int     idx;

        values = (V[]) new Object[size];
        for (node = head, idx = 0; idx < size; node = node.next, ++idx)
        {
            values[idx] = node.val;
        }

        return values;
    }

    public java.util.Iterator<V> iterator()
    {
        return new VListIterator();
    }

    final private class VListIterator implements java.util.Iterator<V>
    {
        private Node<V> nextNode;

        protected VListIterator()
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
            Node<V> retNode;

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

    final private static class Node<V>
    {
        protected V       val;
        protected Node<V> next;

        Node(V val)
        {
            this.val = val;
            next     = null;
        }
    }
}
