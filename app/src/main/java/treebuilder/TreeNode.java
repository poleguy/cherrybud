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
 * treeNode
 * 
   A light weight XML tree node
 */
public abstract class TreeNode {
    /** Node type: tag, attribute, text */
    private TreeNodeType mType = null;
    /** if (this) is a child, mParent points to the parent node */
	private TreeNode mParent = null;
    /** reference to a child node */
	private TreeNode mChild = null;
    /** reference to the next sibling node */
	private TreeNode mSibling = null;
    /** reference to the predecessor to (this) sibling */
    private TreeNode mSibPred = null;

    public TreeNode( TreeNodeType ty )
    {
        mType = ty;
    }

    public TreeNodeType getType()
    {
        return mType;
    }

    public boolean isLeaf()
    {
        return (getChild() == null);
    }

	public void setParent(TreeNode parent) {
		mParent = parent;
	}

	public TreeNode getParent() {
		return mParent;
	}

	public void setChild(TreeNode child) {
		mChild = child;
        if (child != null) {
            child.setParent( this );
        }
	}

	public TreeNode getChild() {
		return mChild;
	}

    public TreeNode getSecondChild() {
        TreeNode child = null;
        if (getChild() != null) {
            child = getChild().getSibling();
        }
        return child;
    }

    public TreeNode getThirdChild() {
        TreeNode child = null;
        if (getChild() != null) {
            TreeNode tmp = getChild().getSibling();
            if (tmp != null) {
                child = tmp.getSibling();
            }
        }
        return child;
    }


    /**
       Get child N, where N = {1, 2, ... n}
     */
    public TreeNode getChildN( int n )
    {
        TreeNode nthChild = getChild();
        for (int i = 1; i < n && nthChild != null; i++) {
            nthChild = nthChild.getSibling();
        }
        return nthChild;
    }

	public void setSibling(TreeNode sibling) {
		mSibling = sibling;
        if (sibling != null) {
            sibling.setSibPred( this );
        }
	}

	public TreeNode getSibling() {
		return mSibling;
	}

    public void setSibPred(TreeNode pred )
    {
        mSibPred = pred;
    }

    public TreeNode getSibPred()
    {
        return mSibPred;
    }

    public abstract String toString();
}
