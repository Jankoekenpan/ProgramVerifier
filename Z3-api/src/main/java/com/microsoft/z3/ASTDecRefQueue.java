/**
Copyright (c) 2012-2014 Microsoft Corporation
   
Module Name:

    ASTDecRefQueue.java

Abstract:

Author:

    @author Christoph Wintersteiger (cwinter) 2012-03-15

Notes:
    
**/

package com.microsoft.z3;

class ASTDecRefQueue extends IDecRefQueue<AST>
{
    public ASTDecRefQueue() 
    {
        super();
    }

    @Override
    protected void decRef(Context ctx, long obj) {
        Native.decRef(ctx.nCtx(), obj);
    }
};
