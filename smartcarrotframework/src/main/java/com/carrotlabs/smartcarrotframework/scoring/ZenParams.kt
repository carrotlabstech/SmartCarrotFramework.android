package com.carrotlabs.smartcarrotframework.scoring

import com.carrotlabs.smartcarrotframework.BuildConfig
import com.carrotlabs.smartcarrotframework.TransactionCategory
import com.carrotlabs.smartcarrotframework.utils.*

/**
 *  [ZenScore] Parameters
 */
public object ZenParams {
    internal val STRIKE_DATA  = listOf(
       Pair(TransactionCategory("c".l.o.t.h.e.s).getIntId() , 0.0351),
       Pair(TransactionCategory("f".e.e.s.underscore.t.a.x.underscore.c.h.a.r.g.e.s).getIntId() , 0.0786),
       Pair(TransactionCategory("g".r.o.c.e.r.i.e.s).getIntId() , 0.1523),
       Pair(TransactionCategory("h".e.a.l.t.h).getIntId() , 0.0222),
       Pair(TransactionCategory("h".e.a.l.t.h.underscore.i.n.s.u.r.a.n.c.e).getIntId() , 0.1536),
       Pair(TransactionCategory("h".o.u.s.e.h.o.l.d.underscore.i.n.s.u.r.a.n.c.e).getIntId() , 0.0071),
       Pair(TransactionCategory("h".o.u.s.e.h.o.l.d.underscore.s.u.p.p.l.i.e.s).getIntId() , 0.0283),
       Pair(TransactionCategory("l".e.i.s.u.r.e.underscore.s.h.o.p.p.i.n.g).getIntId() , 0.0407),
       Pair(TransactionCategory("m".i.s.c).getIntId() , 0.1514),
       Pair(TransactionCategory("m".o.b.i.l.i.t.y).getIntId() , 0.0226),
       Pair(TransactionCategory("h".o.u.s.e.h.o.l.d).getIntId() , 0.025),
       Pair(TransactionCategory("h".o.u.s.i.n.g).getIntId() , 0.2619),
       Pair(TransactionCategory("l".e.i.s.u.r.e.underscore.s.h.o.p.p.i.n.g).getIntId() , 0.0088),
       Pair(TransactionCategory("h".o.u.s.i.n.g).getIntId() , 0.0098),
       Pair(TransactionCategory("h".o.u.s.e.h.o.l.d).getIntId() , 0.0035)
    )

    internal val alpha = 0.99
    internal var beta = 0.15
    internal var gamma = 1.2
    internal var delta = 3
    internal var lambda = getDefaultLambda()
    internal var minSalary = 6000.0
    internal var kickInPoint = 0.4
    internal var finalValue = 0.7

    internal fun getStrike(categoryId: Int) : Double {
        val strike = STRIKE_DATA.filter { it.first == categoryId }
        if (strike.count() > 0) {
            return strike[0].second
        } else {
            return 0.05
        }
    }

    /**
    * Maximum [ZenScore] sequence calculation time interval in days.
    */
    public var MAX_INTERVAL = 3 * 365

    // Required to let the already created tests run on the old value
    internal fun getDefaultLambda() : Double {
        //val isDebuggable = 0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
        if (BuildConfig.DEBUG) {
            return 0.98
        } else {
            return 0.95
        }
    }
}