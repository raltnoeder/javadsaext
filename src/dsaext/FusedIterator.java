package dsaext;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * FusedIterator - iterate over the elements of multiple iterators
 *
 * Copyright (C) 2016 - 2018 Robert ALTNOEDER
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
 *
 * @author Robert Altnoeder <r.altnoeder@gmx.net>
 */
public class FusedIterator<T> implements Iterator<T>
{
    Iterator<? extends T> iteratorList[];
    int index = 0;

    public FusedIterator(Iterator<T> iteratorListRef[])
    {
        if (iteratorListRef == null || iteratorListRef.length < 1)
        {
            throw new IllegalArgumentException();
        }
        iteratorList = iteratorListRef;
    }

    @Override
    public boolean hasNext()
    {
        boolean iterHasNext = false;
        if (index < iteratorList.length)
        {
            do
            {
                iterHasNext = iteratorList[index].hasNext();
                if (!iterHasNext)
                {
                    ++index;
                }
            }
            while (!iterHasNext && index < iteratorList.length);
        }
        return iterHasNext;
    }

    @Override
    public T next()
    {
        if (index >= iteratorList.length)
        {
            throw new NoSuchElementException();
        }

        T value = null;
        do
        {
            try
            {
                value = iteratorList[index].next();
                break;
            }
            catch (NoSuchElementException exc)
            {
                ++index;
                if (index >= iteratorList.length)
                {
                    // Generate a new exception from the FusedIterator rather than
                    // rethrowing the nested iterator's exception
                    throw new NoSuchElementException();
                }
            }
        }
        while (true);

        return value;
    }

    @Override
    public void remove()
    {
        if (index >= iteratorList.length)
        {
            throw new IllegalStateException();
        }

        iteratorList[index].remove();
    }
}
