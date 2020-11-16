/** \file
 * 
 * Sep 12, 2004
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
 * NamedEntity
 * Sep 12, 2004
 * 
<p>
    An object that has an XML name.  That is, an optional name space,
    a optional prefix and an name.
</p>
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
public class NamedEntity extends TreeNode {
    /** The namespace, if one is specified (e.g., like http://www.bearcave.com/expression) */
    private String mNamespace = null;
    /** The prefix associated with this tag, if there is one.  This prefix should
        also be associated with a namespace */
    private String mPrefix = null;
    /** The name of the tag or attribute.  For text, this will be TEXT.
        This is always the local name (in org.w3c.dom terms) */
    private String mName = null;
    /**
     * @param ty
     */
    public NamedEntity(TreeNodeType ty) {
        super(ty);
    }

    public NamedEntity(TreeNodeType ty, String name ) {
        super(ty);
        mName = name;
    }

    public NamedEntity(TreeNodeType ty,  String name, String prefix, String namespace )
    {
        super(ty);
        mName = name;
        mPrefix = prefix;
        mNamespace = namespace;
    }

    public String getNamespace() { return mNamespace; }
    public void setNamesapce(String namespace ) { mNamespace = namespace; }

    public String getPrefix() { return mPrefix; }
    public void setPrefix( String prefix ) { mPrefix = prefix; }

    public String getName() { return mName; }
    public void setName( String name ) { mName = name; }

    public String toString() 
    {
        String name = mName;
        if (mPrefix != null) {
            name = mPrefix + ':' + name;
        }
        return name;
    } // toString

}
