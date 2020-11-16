/** \file
 * 
 * Sep 4, 2004
 *
 * Copyright Ian Kaplan 2004, Bear Products International
 *
 * You may use this code for any purpose, without restriction,
 * including in proprietary code for which you charge a fee.
 * In using this code you acknowledge that you understand its
 * function completely and accept all risk in its use.
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package treebuilder;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * AttributeList
 * Sep 4, 2004
 * 
<p>
    A list of Attribute objects.  The AttributeList is used to
    as a container for a set of XML tag attributes.
</p>
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
public class AttributeList {

    private static class AttrListIter implements Iterator
    {
        private Attribute mCurrent = null;

        public AttrListIter( Attribute attrList )
        {
            mCurrent = attrList;
        }

        public boolean hasNext()
        {
            boolean rslt = mCurrent != null;
            return rslt;
        }

        public Object next()
            throws NoSuchElementException
        {
            if (mCurrent != null) {
                Attribute attr = mCurrent;
                mCurrent = mCurrent.getNext();
                return attr;
            }
            else {
                throw new NoSuchElementException();
            }
        }

        public void remove()
            throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }
    } // static class AttrListIter

    private Attribute mHead = null;
    private Attribute mTail = null;

    public boolean hasAttributes() { return mHead != null; }

    /**
       Add to the end of the attribute list
     */
    public void append( Attribute attr ) 
    {
        if (attr != null) {
            if (mHead == null) {
                mHead = attr;
                mTail = attr;
            }
            else {
                mTail.setNext( attr );
                mTail = attr;
            }
        }
    } // append


    /**
       Insert at the head (front) of the attribute list
     */
    public void insert( Attribute attr )
    {
        if (attr != null) {
            if (mHead == null) {
                mHead = attr;
                mTail = attr;
            }
            else {
                attr.setNext( mHead );
                mHead = attr;
            }
        }
    } // insert


    /**
       Get an Iterator for the attribute list
<p>
       Note that the Iterator returned does not support the remove() method 
       and will throw and UnsupportedOperationException if remove() is called.
</p>
     */
    public Iterator getIterator()
    {
        AttrListIter iter = new AttrListIter( mHead );
        return iter;
    } // getIterator

}
