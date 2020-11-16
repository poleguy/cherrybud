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

/**
 * TagNode
 * 
<p>
    An XML tag node.  This class is derived from a NamedEntity, which
    is an XML object with a name (this include a tag and an attribute).
    It extends this object with an attribute list (something a tag
    node can have but and attribute node cannot).
</p>
 */
public class TagNode extends NamedEntity {
    /** attribute list (or null, if there is no attribute list) */
    private AttributeList mAttrList = null;

    public TagNode() {
        super( TreeNodeType.TAG );
    }

    public TagNode( String tagName )
    {
        super( TreeNodeType.TAG, tagName );
    }

    public TagNode( String tagName, String prefix, String namespace )
    {
        super( TreeNodeType.TAG, tagName, prefix, namespace );
    }

    public AttributeList getAttrList() { return mAttrList; }
    public void setAttrList( AttributeList attrList ) { mAttrList = attrList; }

}
