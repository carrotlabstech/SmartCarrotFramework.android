package com.carrotlabs.smartcarrotframework.utils

// this is not 100% secure, because these props stay in memory, needs to find a way to clean it
// but I am not sure if we just pass them into insert/update db, it could still stay in memory
// however it is used to encode / decode transactions which in turn are not encoded in memory
// when passed into the library, so should be OK
internal object SharedContext {
    internal var key = ""
    internal var iv = ""
}