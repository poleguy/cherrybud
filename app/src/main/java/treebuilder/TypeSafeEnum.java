/*
 * Created on Jul 12, 2004
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

/**
 * TypeSafeEnum
 *
 * @author Ian Kaplan, iank@bearcave.com, Jul 12, 2004
 *
 */

import java.util.ArrayList;
import java.util.Iterator;


/**
   Yet another type safe enumeration base class.  This class supports the 
   creation of multiple enumeration subclasses.  In each subclass the
   enumeration values start at 0.

<p> 
   Documentation for this class can be found <a
   href="http://www.bearcave.com/software/java/misl/enum/type_safe_enum.html">
   here</a>.  Apparently TypeSafeEnums are supported in the new version
   of Java.  So at some point the classes that use this class should
   be replaced with something more generic.
</p>
 */
public abstract class TypeSafeEnum
{
    private static class enumInfo
    {
        public int hashCode;
        public int count;
        public ArrayList values;

        enumInfo( int hash )
        {
            hashCode = hash;
            count = 0;
            values = new ArrayList();
        }
    } // class enumInfo


    private static ArrayList infoVec = new ArrayList();

    private String mName;
    private int mValue;

    public TypeSafeEnum( String name, Class cls )
    {
        mName = name;
        enumInfo elem = findInfo( cls, true );
        mValue = elem.count;
        elem.count++;
        elem.values.add( this );
    } // TypeSafeEnum constructor


    public static Iterator enumValues( Class cls )
    {
        Iterator iter = null;
        enumInfo elem = findInfo( cls, false );
        if (elem != null) {
            iter = elem.values.iterator();
        }
        return iter;
    } // enumValues


    public String getName() { return mName; }
    public int getValue() { return mValue; }

    public String toString() { return getName(); }

    /**
       Find the entry for the enumeration, if it exists.  If not,
       add it to the end of the enumInfo.  Note that this
       function has linear time, but the assumption is that there
       will not a large number of enumeration classes.
    */
    private static enumInfo findInfo(Class cls, boolean add)
    {
        enumInfo foundElem = null;
        int hashCode = cls.hashCode();
        for (Iterator iter = infoVec.iterator(); iter.hasNext(); ) {
            enumInfo elem = (enumInfo)iter.next();
            if (elem.hashCode == hashCode) {
                foundElem = elem;
                break;
            }
        }
        if (foundElem == null && add) {
            foundElem = new enumInfo(hashCode);
            infoVec.add( foundElem  );
        }
        return foundElem;
    }  // findInfo
    
}

