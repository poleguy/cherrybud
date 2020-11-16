/** \file
 * 
 * Sep 19, 2004
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

/**
 * TreeToXML
 * Sep 19, 2004
 * 
<p>
     Traverse the in-memory tree and build an XML representation for
     the tree.  Leaving asside white space, this XML should be the
     same as the original XML that was read to build the tree.
     Or at least the same relative to the supported XML elements.
     For example, the TreeBuilder code does not support the
     "documentation" elements (DOCDECL).
</p>
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
public class TreeToXML {
    private TreeNode mRoot;
    private StringBuffer mBuf = null;
    private final int INDENT = 4;
    private int mIndentLevel;


    private void indent()
    {
        for (int i = 0; i < mIndentLevel; i++) {
            mBuf.append(' ');
        }
    } // indent

    private void openTag()
    {
        if (mIndentLevel > 0) {
            mBuf.append('\n');
        }
        indent();
        mIndentLevel += INDENT;
    } // openTag


    private void closeTag( boolean first )
    {
        assert mIndentLevel > 0;

        mIndentLevel -= INDENT;
        if (! first ) {
            mBuf.append('\n');
            indent();
        }
    } // closeTag



    private void serializeAttribute( Attribute attr )
    {
        String attrName = attr.toString();
        mBuf.append( attrName );
        mBuf.append("=\"");
        String attrVal = attr.getValue();
        mBuf.append( attrVal );
        mBuf.append('"');
    } // serializeAttribute



    private void serializeTag( TreeNode node )
    {
        String tagName = node.toString();
        mBuf.append("<");
        mBuf.append( tagName );
        AttributeList attrList = ((TagNode)node).getAttrList();
        if (attrList != null) {
            Iterator iter = attrList.getIterator();
            while (iter.hasNext()) {
                Attribute attr = (Attribute)iter.next();
                mBuf.append(' ');
                serializeAttribute( attr );
            } // while
        }
        if (node.isLeaf()) {
            mBuf.append("/>");
        }
        else {
            mBuf.append('>');
        }
    } // serializeTag



    private void serializeNode( TreeNode node )
    {
        if (node != null) {
            TreeNodeType ty = node.getType();
            if (ty == TreeNodeType.TAG) {
                serializeTag( node );
            }
            else {
                String nodeStr = node.toString();
                if (ty == TreeNodeType.COMMENT) {
                    nodeStr = "\n<--" + nodeStr + "-->";
                }
                mBuf.append( nodeStr );
            }
        }
    } // serializeNode



    private void endTag( TreeNode root )
    {
        if (root != null && root.getType() == TreeNodeType.TAG) {
            String tagName = root.toString();
            mBuf.append("</");
            mBuf.append( tagName );
            mBuf.append('>');
        }
    } // endTag



    private void leavesToString( TreeNode root )
    {
        if (root != null) {
            for (TreeNode n = root.getChild(); n != null; n = n.getSibling()) {
                if (! n.isLeaf()) {
                    rootToString( n );
                }
                else {
                    serializeNode( n );
                }
            }
        }
    } // leavesToString



    private void rootToString( TreeNode root )
    {
        boolean first = true;

        if (root != null) {
            openTag();
            serializeNode( root );
            if (! root.isLeaf()) {
                // foreach child of root ...
                for (TreeNode n = root.getChild(); n != null; n = n.getSibling()) {
                    if (! n.isLeaf()) {
                        first = false;
                    }
                } // for
                leavesToString( root );
                endTag( root );
            } // if root is not a leaf
            closeTag( first );
        }
    }  // rootToString

    

    public TreeToXML( TreeNode root ) 
    { 
        mRoot = root; 
    }


    public String toString()
    {
        mBuf = new StringBuffer();
        for (TreeNode t = mRoot; t != null; t = t.getSibling()) {
            mIndentLevel = 0;
            rootToString( t );
            mBuf.append('\n');
        }
        return mBuf.toString();
    } // toString

}
