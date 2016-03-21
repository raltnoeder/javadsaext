package dsaext.vlist;

/**
 * Vector list
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
final public class VList<V> implements Iterable<V>
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
        Node<V> insNode = new Node(val);

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
        Node<V> insNode = new Node(val);

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

    public void insert(V value, long index) throws IndexOutOfBoundsException
    {
        if (index < 0 || index > size)
        {
            throw new IndexOutOfBoundsException("VList.insert(): index " + index);
        }

        Node<V> insNode = new Node(value);
        if (index == 0)
        {
            if (tail == null)
            {
                tail = insNode;
            }
            insNode.next = head;
            head         = insNode;
        }
        else
        if (index == size)
        {
            tail.next = insNode;
            tail      = insNode;
        }
        else
        {
            Node<V> prevNode = head;
            for (long pos = 1; pos < index; ++pos)
            {
                prevNode = prevNode.next;
            }
            insNode.next  = prevNode.next;
            prevNode.next = insNode;
        }

        ++size;
    }

    public void remove(long index) throws IndexOutOfBoundsException
    {
        if (head == null || index < 0 || index > (size - 1))
        {
            throw new IndexOutOfBoundsException("VList.remove(): invalid index " + index);
        }

        if (index == 0)
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
            Node<V> prevNode = head;
            for (long pos = 1; pos < index; ++pos)
            {
                prevNode = prevNode.next;
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

    public void clear()
    {
        head = null;
        tail = null;
        size = 0;
    }

    @SuppressWarnings("unchecked")
    public V[] toArray()
    {
        V[] values = null;
        if (size <= Integer.MAX_VALUE)
        {
            values = (V[]) new Object[size];
            Node<V> node = head;
            for (int index = 0; index < size; ++index)
            {
                values[index] = node.value;
                node = node.next;
            }
        }

        return values;
    }

    @Override
    public java.util.Iterator<V> iterator()
    {
        return new VListIterator(this);
    }

    final private class VListIterator implements java.util.Iterator<V>
    {
        private VList<V> container;
        private Node<V> current;
        private Node<V> next;

        protected VListIterator(VList<V> containerRef)
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

    final private static class Node<V>
    {
        protected V       value;
        protected Node<V> next;

        Node(V valueRef)
        {
            this.value = valueRef;
            next       = null;
        }
    }
}
