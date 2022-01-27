package com.carrotlabs.smartcarrotframework.models

import kotlin.math.min

internal fun String.levenshtein(other: String) : Int {
    /*
         https://web.archive.org/web/20120526085419/http://www.merriampark.com/ldjava.htm

         The difference between this impl. and the previous is that, rather
         than creating and retaining a matrix of size s.length()+1 by t.length()+1,
         we maintain two single-dimensional arrays of length s.length()+1.  The first, d,
         is the 'current working' distance array that maintains the newest distance cost
         counts as we iterate through the characters of String s.  Each time we increment
         the index of String t we are comparing, d is copied to p, the second int[].  Doing so
         allows us to retain the previous cost counts as required by the algorithm (taking
         the minimum of the cost count to the left, up one, and diagonally up and to the left
         of the current cost count being calculated).  (Note that the arrays aren't really
         copied anymore, just switched...this is clearly much better than cloning an array
         or doing a System.arraycopy() each time  through the outer loop.)

         Effectively, the difference between the two implementations is this one does not
         cause an out of memory condition when calculating the LD over two very large strings.
         */

    val n = this.count(); // length of s
    val m = other.count(); // length of t

    if (n == 0) {
        return m
    } else if (m == 0) {
        return n
    }

    var p = IntArray(n+1) //'previous' cost array, horizontally
    var d = IntArray(n+1) // cost array, horizontally
    var _d: IntArray; //placeholder to assist in swapping p and d

    var t_j:Char // jth character of t

    var cost:Int // cost

    for (i in 0..n) {
        p[i] = i
    }

    for (j in 1..m) {
        t_j = other[j-1]
        d[0] = j

        for (i in 1..n) {
        cost = if (this[i-1] == t_j) 0 else 1;
        // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
        d[i] = min(min(d[i-1]+1, p[i]+1),  p[i-1]+cost)
    }

        // copy current distance counts to 'previous row' distance counts
        _d = p;
        p = d;
        d = _d;
    }

    // our last action in the above loop was to switch d and p, so p now
    // actually has the most recent cost counts
    return p[n];
}