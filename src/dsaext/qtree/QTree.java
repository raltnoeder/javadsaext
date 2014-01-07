package dsaext.qtree;

import dsaext.QIterator;

/**
 * Quick balanced binary search tree
 *
 * @version 2011-10-22_001
 * @author  Robert Altnoeder (r.altnoeder@gmx.net)
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
        protected Node<K, V> p;
        protected Node<K, V> l;
        protected Node<K, V> r;

        /* balance number */
        protected int blnc;

        protected Node(K key, V val)
        {
            this.key = key;
            this.val = val;

            blnc = 0;

            p = null;
            l = null;
            r = null;
        }
    }

    final private static class QTreeEnumeration<T>
        implements java.util.Enumeration<T>
    {
        private QTreeEnumerationNode<T> enumRoot;
        private QTreeEnumerationNode<T> crt;

        QTreeEnumeration(QTreeEnumerationNode<T> root)
        {
            enumRoot = root;
        }

        public boolean hasMoreElements()
        {
            return (enumRoot != null);
        }

        public T nextElement()
        {
            crt = enumRoot;
            enumRoot = enumRoot.next;
            return crt.enumVal;
        }
    }

    final private class QTreeValuesIterator implements QIterator<V>
    {
        /* current node */
        private Node<K, V> c;
        /* previous node */
        private Node<K, V> p;
        /* result node */
        private Node<K, V> r;

        private int state;

        final private static int STATE_ENTER_L = 0;
        final private static int STATE_ENTER_H = 1;
        final private static int STATE_LEAVE   = 2;

        protected QTreeValuesIterator()
        {
            if (root != null)
            {
                for (c = root; c.l != null; c = c.l) { }
            } else {
                c = null;
            }
            p = null;
            state = STATE_ENTER_H;
        }

        final public long getSize()
        {
            return size;
        }

        final public boolean hasNext()
        {
            return (c != null);
        }

        final public V next()
        {
            if (c != null)
            {
                /* save current */
                r = c;

                /* update current */
                for (; c != null;)
                {
                    if (state == STATE_ENTER_L)
                    {
                        /* traversing down */
                        /* find lowest-value node */
                        for (; c.l != null; c = c.l) { }
                        state = STATE_ENTER_H;
                        /* select current */
                        break;
                    } else
                    if (state == STATE_ENTER_H)
                    {
                        /* traversing down */
                        if (c.r != null)
                        {
                            c = c.r;
                            state = STATE_ENTER_L;
                        } else {
                            /* leaf node */
                            state = STATE_LEAVE;
                        }
                    } else {
                        /* traversing up (STATE_LEAVE) */
                        p = c;
                        c = c.p;
                        if (c == null)
                        {
                            break;
                        }
                        if (c.l == p)
                        {
                            /* traversing up from lower-value node */
                            state = STATE_ENTER_H;
                            /* select current */
                            break;
                        } else {
                            /* traversing up from higher-value node */
                            continue;
                        }
                    }
                }

                return r.val;
            }

            return null;
        }

        final public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    final private class QTreeKeysIterator implements QIterator<K>
    {
        /* current node */
        private Node<K, V> c;
        /* previous node */
        private Node<K, V> p;
        /* result node */
        private Node<K, V> r;

        private int state;

        final private static int STATE_ENTER_L = 0;
        final private static int STATE_ENTER_H = 1;
        final private static int STATE_LEAVE   = 2;

        protected QTreeKeysIterator()
        {
            if (root != null)
            {
                for (c = root; c.l != null; c = c.l) { }
            } else {
                c = null;
            }
            p = null;
            state = STATE_ENTER_H;
        }

        final public long getSize()
        {
            return size;
        }

        final public boolean hasNext()
        {
            return (c != null);
        }

        final public K next()
        {
            if (c != null)
            {
                /* save current */
                r = c;

                /* update current */
                for (; c != null;)
                {
                    if (state == STATE_ENTER_L)
                    {
                        /* traversing down */
                        /* find lowest-value node */
                        for (; c.l != null; c = c.l) { }
                        state = STATE_ENTER_H;
                        /* select current */
                        break;
                    } else
                    if (state == STATE_ENTER_H)
                    {
                        /* traversing down */
                        if (c.r != null)
                        {
                            c = c.r;
                            state = STATE_ENTER_L;
                        } else {
                            /* leaf node */
                            state = STATE_LEAVE;
                        }
                    } else {
                        /* traversing up (STATE_LEAVE) */
                        p = c;
                        c = c.p;
                        if (c == null)
                        {
                            break;
                        }
                        if (c.l == p)
                        {
                            /* traversing up from lower-value node */
                            state = STATE_ENTER_H;
                            /* select current */
                            break;
                        } else {
                            /* traversing up from higher-value node */
                            continue;
                        }
                    }
                }

                return r.key;
            }

            return null;
        }

        final public V getValue()
        {
            if (c != null)
            {
                return c.val;
            }

            return null;
        }

        final public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    /*
    private class QTreeValueIterator implements java.util.Iterator<V>
    {
        
    }
    */

    final private static class QTreeEnumerationNode<T>
    {
        QTreeEnumerationNode<T> next;

        T enumVal;
    }

    final public void insert(K key, V val)
    {
        Node<K, V> nX;
        Node<K, V> nY;
        int  rc;

        nX = new Node<K, V>(key, val);

        if (root == null)
        {
            root = nX;
            ++size;
        } else {
            nY = root;
            for (;;)
            {
               rc = nX.key.compareTo(nY.key);
               if (rc < 0)
               {
                   if (nY.l == null)
                   {
                       nY.l = nX;
                       nX.p = nY;
                       ++size;
                       break;
                   } else {
                       nY = nY.l;
                   }
               } else
               if (rc > 0)
               {
                   if (nY.r == null)
                   {
                       nY.r = nX;
                       nX.p = nY;
                       ++size;
                       break;
                   } else {
                       nY = nY.r;
                   }
               } else {
                   nY.key = nX.key;
                   nY.val = nX.val;
                   return;
               }
            }

            /* update balance and perform rotations */
            do
            {
                if (nY.l == nX)
                {
                    --nY.blnc;
                } else {
                    ++nY.blnc;
                }

                if (nY.blnc == 0)
                {
                    break;
                } else
                if (nY.blnc == -2)
                {
                    if (nX.blnc == -1)
                    {
                        /* rotate R */
                        nY.blnc = 0;
                        nX.blnc = 0;

                        nX.p = nY.p;
                        if (nY.p != null)
                        {
                            if (nY.p.l == nY)
                            {
                                nY.p.l = nX;
                            } else {
                                nY.p.r = nX;
                            }
                        } else {
                            root = nX;
                        }

                        nY.l = nX.r;
                        if (nX.r != null)
                        {
                            nX.r.p = nY;
                        }

                        nX.r = nY;
                        nY.p = nX;
                    } else {
                        /* rotate LR */
                        if (nX.r.blnc == -1)
                        {
                            nX.blnc = 0;
                            nY.blnc = 1;
                        } else
                        if (nX.r.blnc == 1)
                        {
                            nX.blnc = -1;
                            nY.blnc = 0;
                        } else {
                            nX.blnc = 0;
                            nY.blnc = 0;
                        }
                        nX.r.blnc = 0;

                        nX.p = nX.r;
                        nX.r = nX.r.l;
                        nX.p.l = nX;
                        nY.l = nX.p.r;
                        nX.p.p = nY.p;
                        if (nX.r != null)
                        {
                            nX.r.p = nX;
                        }
                        if (nY.l != null)
                        {
                            nY.l.p = nY;
                        }

                        if (nY.p != null)
                        {
                            if (nY.p.l == nY)
                            {
                                nY.p.l = nX.p;
                            } else {
                                nY.p.r = nX.p;
                            }
                        } else {
                            root = nX.p;
                        }

                        nY.p = nX.p;
                        nX.p.r = nY;
                    }
                    break;
                } else
                if (nY.blnc == 2)
                {
                    if (nX.blnc == 1)
                    {
                        /* rotate L */
                        nY.blnc = 0;
                        nX.blnc = 0;

                        nX.p = nY.p;
                        if (nY.p != null)
                        {
                            if (nY.p.l == nY)
                            {
                                nY.p.l = nX;
                            } else {
                                nY.p.r = nX;
                            }
                        } else {
                            root = nX;
                        }

                        nY.r = nX.l;
                        if (nX.l != null)
                        {
                            nX.l.p = nY;
                        }

                        nX.l = nY;
                        nY.p = nX;
                    } else {
                        /* rotate RL */
                        if (nX.l.blnc == -1)
                        {
                            nX.blnc = 1;
                            nY.blnc = 0;
                        } else
                        if (nX.l.blnc == 1)
                        {
                            nX.blnc = 0;
                            nY.blnc = -1;
                        } else {
                            nX.blnc = 0;
                            nY.blnc = 0;
                        }
                        nX.l.blnc = 0;

                        nX.p = nX.l;
                        nX.l = nX.l.r;
                        nX.p.r = nX;
                        nY.r = nX.p.l;
                        nX.p.p = nY.p;
                        if (nX.l != null)
                        {
                            nX.l.p = nX;
                        }
                        if (nY.r != null)
                        {
                            nY.r.p = nY;
                        }

                        if (nY.p != null)
                        {
                            if (nY.p.l == nY)
                            {
                                nY.p.l = nX.p;
                            } else {
                                nY.p.r = nX.p;
                            }
                        } else {
                            root = nX.p;
                        }

                        nY.p = nX.p;
                        nX.p.l = nY;
                    }
                    break;
                }
                
                nX   = nY;
                nY = nY.p;
            } while (nY != null);
        }
    }

    final public V get(K key)
    {
        Node<K, V> nX;
        int rc;

        if (root == null)
        {
            return null;
        } else {
            nX = root;
            while (nX != null)
            {
               rc = key.compareTo(nX.key);
               if (rc < 0)
               {
                   nX = nX.l;
               } else
               if (rc > 0)
               {
                   nX = nX.r;
               } else {
                   return nX.val;
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
            } else
            if (rc < 0)
            {
                crt = crt.l;
            } else {
                crt = crt.r;
            }
        }

        return false;
    }

    final public void remove(K key)
    {
        Node<K, V> crt;
        Node<K, V> n;
        Node<K, V> r;
        int  dir;
        int  rc;

        if (root != null)
        {
            /* find the node to be deleted */
            crt = root;
            for (;;)
            {
                rc = key.compareTo(crt.key);
                if (rc < 0)
                {
                    crt = crt.l;
                } else
                if (rc > 0)
                {
                    crt = crt.r;
                } else {
                    break;
                }

                if (crt == null)
                {
                    return;
                }
            }
            --size;

            if (crt.l == null && crt.r == null)
            {
                /* leaf node */
                n = crt;
            } else {
                /* one child or two subtrees *
                 * find replacement node     */
                if (crt.blnc == -1)
                {
                    for (n = crt.l; n.r != null; n = n.r) { }
                } else {
                    for (n = crt.r; n.l != null; n = n.l) { }
                }
            }
            
            /* remove the node */
            if (root == n)
            {
                if (n.l != null)
                {
                    /* replace root node by its left child */
                    root = n.l;
                    n.l.p = null;
                } else
                if (n.r != null)
                {
                    /* replace root node by its right child */
                    root = n.r;
                    n.r.p = null;
                } else {
                    /* root node leaf */
                    root = null;
                }
                return;
            } else {
                /* non-root node */
                r = n.p;
                if (r.l == n)
                {
                    /* node to remove is in the left subtree *
                     * of its parent                         */

                    /* save direction */
                    dir = -1;

                    if (n.l != null)
                    {
                        /* replace node by its left child */
                        r.l = n.l;
                        n.l.p = r;
                    } else
                    if (n.r != null)
                    {
                        /* replace node by its right child */
                        r.l = n.r;
                        n.r.p = r;
                    } else {
                        /* non-root leaf node */
                        r.l = null;
                    }
                } else {
                    /* node to remove is in the right subtree *
                     * of its parent                          */

                    /* save direction */
                    dir = 1;

                    if (n.l != null)
                    {
                        /* replace node by its left child */
                        r.r = n.l;
                        n.l.p = r;
                    } else
                    if (n.r != null)
                    {
                        /* replace node by its right child */
                        r.r = n.r;
                        n.r.p = r;
                    } else {
                        /* non-root leaf node */
                        r.r = null;
                    }
                }
            }

            /* if n was removed as a replacement for crt,
             * replace crt by n */
            if (crt != n)
            {
                crt.key = n.key;
                crt.val = n.val;
            }

            /* update balance and perform rotations */
            for (; r != null; r = r.p)
            {
                if (dir < 0)
                {
                    /* node was removed from left subtree */
                    if (++r.blnc == 1)
                    {
                        break;
                    }
                } else {
                    /* node was removed from right subtree */
                    if (--r.blnc == -1)
                    {
                        break;
                    }
                }

                if (r.p != null)
                {
                    if (r.p.l == r)
                    {
                        dir = -1;
                    } else {
                        dir = 1;
                    }
                }

                /* update balance and perform rotations */
                if (r.blnc == -2)
                {
                    n = r.l;
                    /* 0 or -1 */
                    if (n.blnc <= 0)
                    {
                        /* rotate R */
                        n.p = r.p;
                        if (r.p != null)
                        {
                            if (r.p.l == r)
                            {
                                r.p.l = n;
                            } else {
                                r.p.r = n;
                            }
                        } else {
                            root = n;
                        }

                        r.l = n.r;
                        if (n.r != null)
                        {
                            n.r.p = r;
                        }

                        n.r = r;
                        r.p = n;

                        if (n.blnc == 0)
                        {
                            r.blnc = -1;
                            n.blnc = 1;
                            break;
                        } else {
                            r.blnc = 0;
                            n.blnc = 0;
                        }
                    } else {
                        /* rotate LR */
                        if (n.r.blnc == -1)
                        {
                            n.blnc = 0;
                            r.blnc = 1;
                        } else
                        if (n.r.blnc == 1)
                        {
                            n.blnc = -1;
                            r.blnc = 0;
                        } else {
                            n.blnc = 0;
                            r.blnc = 0;
                        }
                        n.r.blnc = 0;

                        n.p = n.r;
                        n.r = n.r.l;
                        n.p.l = n;
                        r.l = n.p.r;
                        n.p.p = r.p;
                        if (n.r != null)
                        {
                            n.r.p = n;
                        }
                        if (r.l != null)
                        {
                            r.l.p = r;
                        }

                        if (r.p != null)
                        {
                            if (r.p.l == r)
                            {
                                r.p.l = n.p;
                            } else {
                                r.p.r = n.p;
                            }
                        } else {
                            root = n.p;
                        }

                        r.p = n.p;
                        n.p.r = r;
                    }
                    r = r.p;
                    /* end of R / LR rotations */
                } else
                if (r.blnc == 2)
                {
                    n = r.r;
                    /* 0 or 1 */
                    if (n.blnc >= 0)
                    {
                        /* rotate L */
                        n.p = r.p;
                        if (r.p != null)
                        {
                            if (r.p.l == r)
                            {
                                r.p.l = n;
                            } else {
                                r.p.r = n;
                            }
                        } else {
                            root = n;
                        }

                        r.r = n.l;
                        if (n.l != null)
                        {
                            n.l.p = r;
                        }

                        n.l = r;
                        r.p = n;
                        if (n.blnc == 0)
                        {
                            r.blnc = 1;
                            n.blnc = -1;
                            break;
                        } else {
                            r.blnc = 0;
                            n.blnc = 0;
                        }
                    } else {
                        /* rotate RL */
                        if (n.l.blnc == -1)
                        {
                            n.blnc = 1;
                            r.blnc = 0;
                        } else
                        if (n.l.blnc == 1)
                        {
                            n.blnc = 0;
                            r.blnc = -1;
                        } else {
                            n.blnc = 0;
                            r.blnc = 0;
                        }
                        n.l.blnc = 0;

                        n.p = n.l;
                        n.l = n.l.r;
                        n.p.r = n;
                        r.r = n.p.l;
                        n.p.p = r.p;
                        if (n.l != null)
                        {
                            n.l.p = n;
                        }
                        if (r.r != null)
                        {
                            r.r.p = r;
                        }

                        if (r.p != null)
                        {
                            if (r.p.l == r)
                            {
                                r.p.l = n.p;
                            } else {
                                r.p.r = n.p;
                            }
                        } else {
                            root = n.p;
                        }

                        r.p = n.p;
                        n.p.l = r;
                    }
                    r = r.p;
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
        QTreeEnumerationNode<V>  en;

        int state;
        Node<K, V> c;
        Node<K, V> p;

        if (root != null)
        {
            for (c = root; c.l != null; c = c.l) { }
            p = null;
            state = STATE_ENTER_H;

            en = valsEnumRoot = new QTreeEnumerationNode<V>();
            valsEnumRoot.enumVal = c.val;

            for (; c != null;)
            {
                if (state == STATE_ENTER_L)
                {
                    /* traversing down */
                    /* find lowest-value node */
                    for (; c.l != null; c = c.l) { }
                    state = STATE_ENTER_H;
                    /* select current */
                    en.next = new QTreeEnumerationNode<V>();
                    en = en.next;
                    en.enumVal = c.val;
                } else
                if (state == STATE_ENTER_H)
                {
                    /* traversing down */
                    if (c.r != null)
                    {
                        c = c.r;
                        state = STATE_ENTER_L;
                    } else {
                        /* leaf node */
                        state = STATE_LEAVE;
                    }
                } else {
                    /* traversing up (STATE_LEAVE) */
                    p = c;
                    c = c.p;
                    if (c == null)
                    {
                        break;
                    }
                    if (c.l == p)
                    {
                        /* traversing up from lower-value node */
                        state = STATE_ENTER_H;
                        /* select current */
                        en.next = new QTreeEnumerationNode<V>();
                        en = en.next;
                        en.enumVal = c.val;
                    } else {
                        /* traversing up from higher-value node */
                        continue;
                    }
                }
            }
            en.next = null;
        } else {
            valsEnumRoot = null;
        }

        valsEnum = new QTreeEnumeration<V>(valsEnumRoot);

        return valsEnum;
    }

    final public java.util.Enumeration<K> keysEn()
    {
        java.util.Enumeration<K> keysEnum;
        QTreeEnumerationNode<K>  keysEnumRoot;
        QTreeEnumerationNode<K>  en;

        int state;
        Node<K, V> c;
        Node<K, V> p;

        if (root != null)
        {
            for (c = root; c.l != null; c = c.l) { }
            p = null;
            state = STATE_ENTER_H;

            en = keysEnumRoot = new QTreeEnumerationNode<K>();
            keysEnumRoot.enumVal = c.key;

            for (; c != null;)
            {
                if (state == STATE_ENTER_L)
                {
                    /* traversing down */
                    /* find lowest-value node */
                    for (; c.l != null; c = c.l) { }
                    state = STATE_ENTER_H;
                    /* select current */
                    en.next = new QTreeEnumerationNode<K>();
                    en = en.next;
                    en.enumVal = c.key;
                } else
                if (state == STATE_ENTER_H)
                {
                    /* traversing down */
                    if (c.r != null)
                    {
                        c = c.r;
                        state = STATE_ENTER_L;
                    } else {
                        /* leaf node */
                        state = STATE_LEAVE;
                    }
                } else {
                    /* traversing up (STATE_LEAVE) */
                    p = c;
                    c = c.p;
                    if (c == null)
                    {
                        break;
                    }
                    if (c.l == p)
                    {
                        /* traversing up from lower-value node */
                        state = STATE_ENTER_H;
                        /* select current */
                        en.next = new QTreeEnumerationNode<K>();
                        en = en.next;
                        en.enumVal = c.key;
                    } else {
                        /* traversing up from higher-value node */
                        continue;
                    }
                }
            }
            en.next = null;
        } else {
            keysEnumRoot = null;
        }

        keysEnum = new QTreeEnumeration<K>(keysEnumRoot);
        
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

        int state;
        Node<K, V> c;
        Node<K, V> p;

        if (size <= (long) Integer.MAX_VALUE)
        {
            keys = (K[]) new Comparable[(int) size];

            if (root != null)
            {
                for (c = root, idx = 0; c.l != null; c = c.l) { }
                p = null;
                state = STATE_ENTER_H;

                keys[idx++] = c.key;

                for (; c != null;)
                {
                    if (state == STATE_ENTER_L)
                    {
                        /* traversing down */
                        /* find lowest-value node */
                        for (; c.l != null; c = c.l) { }
                        state = STATE_ENTER_H;
                        /* select current */
                        keys[idx++] = c.key;
                    } else
                    if (state == STATE_ENTER_H)
                    {
                        /* traversing down */
                        if (c.r != null)
                        {
                            c = c.r;
                            state = STATE_ENTER_L;
                        } else {
                            /* leaf node */
                            state = STATE_LEAVE;
                        }
                    } else {
                        /* traversing up (STATE_LEAVE) */
                        p = c;
                        c = c.p;
                        if (c == null)
                        {
                            break;
                        }
                        if (c.l == p)
                        {
                            /* traversing up from lower-value node */
                            state = STATE_ENTER_H;
                            /* select current */
                            keys[idx++] = c.key;
                        } else {
                            /* traversing up from higher-value node */
                            continue;
                        }
                    }
                }
            }
        } else {
            keys = null;
        }

        return keys;
    }

    @SuppressWarnings("unchecked")
    final public V[] valuesArray()
    {
        V[] vals;
        int idx;

        int state;
        Node<K, V> c;
        Node<K, V> p;

        if (size <= (long) Integer.MAX_VALUE)
        {
            vals = (V[]) new Object[(int) size];

            if (root != null)
            {
                for (c = root, idx = 0; c.l != null; c = c.l) { }
                p = null;
                state = STATE_ENTER_H;

                vals[idx++] = c.val;

                for (; c != null;)
                {
                    if (state == STATE_ENTER_L)
                    {
                        /* traversing down */
                        /* find lowest-value node */
                        for (; c.l != null; c = c.l) { }
                        state = STATE_ENTER_H;
                        /* select current */
                        vals[idx++] = c.val;
                    } else
                    if (state == STATE_ENTER_H)
                    {
                        /* traversing down */
                        if (c.r != null)
                        {
                            c = c.r;
                            state = STATE_ENTER_L;
                        } else {
                            /* leaf node */
                            state = STATE_LEAVE;
                        }
                    } else {
                        /* traversing up (STATE_LEAVE) */
                        p = c;
                        c = c.p;
                        if (c == null)
                        {
                            break;
                        }
                        if (c.l == p)
                        {
                            /* traversing up from lower-value node */
                            state = STATE_ENTER_H;
                            /* select current */
                            vals[idx++] = c.val;
                        } else {
                            /* traversing up from higher-value node */
                            continue;
                        }
                    }
                }
            }
        } else {
            vals = null;
        }

        return vals;
    }



    /* ========== DEBUG CODE ========== */

    void dump(Node<K, V> d)
    {
        String txt;

        System.out.println("DUMP Node " + d.key.toString());
        if (d.p != null)
        {
            txt = d.p.key.toString();
        } else {
            txt = "null";
        }
        System.out.println("  p: " + txt);

        if (d.l != null)
        {
            txt = d.l.key.toString();
        } else {
            txt = "null";
        }
        System.out.println("  l: " + txt);

        if (d.r != null)
        {
            txt = d.r.key.toString();
        } else {
            txt = "null";
        }
        System.out.println("  r: " + txt);

        System.out.println("  blnc: " + d.blnc);
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
                if (c.l != null)
                {
                    /* enter left subtree */
                    c = c.l;
                    ++ctr;
                } else
                if (c.r != null)
                {
                    /* if there is no left subtree, enter right subtree */
                    c = c.r;
                    ++ctr;
                } else {
                    /* leaf, go back up */
                    p = c;
                    c = c.p;
                    if (ctr < level)
                    {
                        level = ctr;
                    }
                    --ctr;
                }
            } else {
                if (c.l == p)
                {
                    if (c.r != null)
                    {
                        /* enter right subtree */
                        p = null;
                        c = c.r;
                        ++ctr;
                    } else {
                        /* if there is no right subtree, go further up */
                        p = c;
                        c = c.p;
                        --ctr;
                    }
                } else {
                    /* go further up */
                    p = c;
                    c = c.p;
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
                if (c.l != null)
                {
                    /* enter left subtree */
                    c = c.l;
                    ++ctr;
                } else
                if (c.r != null)
                {
                    /* if there is no left subtree, enter right subtree */
                    c = c.r;
                    ++ctr;
                } else {
                    /* leaf, go back up */
                    p = c;
                    c = c.p;
                    if (ctr > level)
                    {
                        level = ctr;
                    }
                    --ctr;
                }
            } else {
                if (c.l == p)
                {
                    if (c.r != null)
                    {
                        /* enter right subtree */
                        p = null;
                        c = c.r;
                        ++ctr;
                    } else {
                        /* if there is no right subtree, go further up */
                        p = c;
                        c = c.p;
                        --ctr;
                    }
                } else {
                    /* go further up */
                    p = c;
                    c = c.p;
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
                    System.out.print("(" + crt.blnc + ") ");
                } else {
                    System.out.print(" ");
                }

                if (crt.l != null)
                {
                    /* enter left subtree */
                    System.out.print("<");
                    crt = crt.l;
                } else
                if (crt.r != null)
                {
                    /* if there is no left subtree, enter right subtree */
                    System.out.print(">");
                    crt = crt.r;
                } else {
                    /* leaf, go back up */
                    System.out.print("} ");
                    p = crt;
                    crt = crt.p;
                }
            } else {
                if (crt.l == p)
                {
                    if (crt.r != null)
                    {
                        /* enter right subtree */
                        System.out.print(">");
                        p = null;
                        crt = crt.r;
                    } else {
                        /* if there is no right subtree, go further up */
                        System.out.print("} ");
                        p = crt;
                        crt = crt.p;
                    }
                } else {
                    /* go further up */
                    System.out.print("} ");
                    p = crt;
                    crt = crt.p;
                }
            }
        }
        System.out.println();
        System.out.flush();
    }

}
