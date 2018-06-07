/**
Copyright (c) 2012-2014 Microsoft Corporation
   
Module Name:

    StatisticsDecRefQueue.java

Abstract:

Author:

    @author Christoph Wintersteiger (cwinter) 2012-03-15

Notes:
    
**/ 

package com.microsoft.z3;

class StatisticsDecRefQueue extends IDecRefQueue<Statistics> {
    public StatisticsDecRefQueue() 
    {
        super();
    }

    @Override
    protected void decRef(Context ctx, long obj) {
        Native.statsDecRef(ctx.nCtx(), obj);
    }
}
