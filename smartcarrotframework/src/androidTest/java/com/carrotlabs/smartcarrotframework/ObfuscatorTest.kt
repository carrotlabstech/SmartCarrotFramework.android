package com.carrotlabs.smartcarrotframework

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carrotlabs.smartcarrotframework.utils.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.StringBuilder

@RunWith(AndroidJUnit4::class)
internal class ObfuscatorTest {
    private val PUBLIC_KEY = """
    MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA3d1GxBEthCSAE5BTF9/U
    jFm+Zq8+X03rO/G2TaVEZbtkilR6pTORXGFXNcJxLKEOivn/zMPwUEK4ox857TBv
    zFRZnoI1o8gEb4b6AK5r4I8c9evEyyDq/TH3d6J/qQwv0kX09nEmjJsR26JU0L4N
    hVMRmA20yV6epYod9uGWV1yVyWbraYMB7Vo59SphW8WcQUxc43wMqGbpEzUoPPyL
    PWX8C7NOOnolPdGd1IrCMDgl1bb/LRIxKvVC00ZXjla8tNrd2n5fTAwo5hRidgZf
    9fHoz1P32wzdgN7WckEmriDi02U2XRDEm6CCb3p3aEUv4QCLZGrKjm+EHL4Aihsg
    fJ3JMgp2VXzz3W1ewvsAiys0KMa6d7fWJuc3wNW4V/CRrNPr8p/vXSEvOMdzrksT
    f/EimjbxLSygo8A+VKBHt78Xoz6yL2Iy0nWOZq17VxPFzVetN10QF4nPFz8JQr0U
    o54sCPjLOz4+v9X0xKO+A7/hJGlnpwt1kHJAsblZpB/uMZF987C3Qs803Eujqp+x
    1iLR/AxSTJEq5/YubR5B3jz+YAqJg6wouHYrKF+2oIZH1i00N+4NdQzOrx2H+g0d
    rmuZJIjF3QW+tQaKsE6oVbQbihcWnam/v75yHqxVnDTOXx7cCNVrnUzKrChCQwLS
    RxR/FBbIIGonq88hPy6GE6ECAwEAAQ==
    """

    @Test
    fun testProduceString() {
        var salt = "s".a.l.t.f.o.r.h.i.d.i.g.paren_left.o.b.f.u.s.c.a.t.i.n.g.paren_right.space.s.t.r.t.n.g.s.dot
        val o = Obfuscator(salt)

        var res = o.bytesByObfuscatingString(PUBLIC_KEY)
        var sb = StringBuilder("arrayOf(")
        for (by in res) {
            sb.append(",$by")
        }

        var r = sb.append(")").toString()

        var deObfuscated = o.reveal(res)
        Assert.assertEquals(PUBLIC_KEY, deObfuscated)
    }
}
