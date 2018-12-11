package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.crypto.signing.XyoSigner
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.ecc.XyoUncompressedEcPublicKey
import network.xyo.sdkcorekotlin.crypto.signing.algorithms.rsa.XyoRsaWithSha256
import network.xyo.sdkcorekotlin.hashing.basic.XyoBasicHashBase
import network.xyo.sdkcorekotlin.hashing.basic.XyoSha3
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff
import network.xyo.sdkobjectmodelkotlin.objects.XyoIterableObject
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class XyoTestBoundWitnessUtil : XyoTestBase() {

    @Test
    fun testRemoveTypeFromWitnesses () {
        runBlocking {
            val boundWitnessBytes = BigInteger("A0020000023DA01500000118A00100000112800D000000850082202BDE1EB8335AB4064B524D450B34F21AF9D630C57503FDE35C6708A931F923DF30AA9068240838B68C404CDD0FAB55CFAB58EF973EBBD9E73954547940B77BAF3B94432F437387722475B2CBDD819C1E13EFFAEC8906230D2325849EE1891B45DC4B9A2279257712E2BE060BFE517B1651C85810E2185C8E0A54ABAF4073800D00000085009D603D917DAFE6B263E9E68D3C5B3D101E9B2C8A1EE0515F28AA78C146363E369F57A41ADD790A484447DE552E1C0C9D762E378ECF4583974B2767A1B9956ED07DF80CFFEAA6193B04DA261818DAEF94E846782DA51CB231C461378EC44503B9C6E910754CAB299E52ABD4ABD4ACA78D886E744CC46D8FAC909BFEFE55940B7BA0160000011DA00100000110800A000000848F20E2F3CA33F6BE1CDCBC3EEEC3B07768C79B099A8DAFD28BF5C187D67B05994A40F46EC8D4B59A22BA04CB1CCB78D3D50A08103624AFDD7013B43BCEEB41E5C6C160CF494BD694CF0F6A5B41D472DED6B4DBC27F6AAAF43E66FA867F095B7B829D8AF5E97244B1D84287EE647C65B180E4709584BBF1B15E9CDAFC60AB3FA4800A000000848F20E2F3CA33F6BE1CDCBC3EEEC3B07768C79B099A8DAFD28BF5C187D67B05994A40F46EC8D4B59A22BA04CB1CCB78D3D50A08103624AFDD7013B43BCEEB41E5C6C160CF494BD694CF0F6A5B41D472DED6B4DBC27F6AAAF43E66FA867F095B7B829D8AF5E97244B1D84287EE647C65B180E4709584BBF1B15E9CDAFC60AB3FA48013000000052D", 16).toByteArray()
            val createdBoundWitness = XyoBoundWitness.getInstance(boundWitnessBytes.copyOfRange(1, boundWitnessBytes.size))
            val removedBoundWitness = XyoBoundWitnessUtil.removeTypeFromUnsignedPayload(XyoSchemas.RSSI.id, createdBoundWitness)

            for (witness in removedBoundWitness[XyoSchemas.FETTER.id]) {
                if (witness is XyoIterableObject) {
                    for (item in witness.iterator) {
                        if (item.schema.id == XyoSchemas.RSSI.id) {
                            throw Exception("Found an rssi in FETTER!")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun testGetPartyNumFromPublicKey () {
        runBlocking {
            val boundWitnessBytes = "6002012B201547201944000C4192BAF8FBA41F6B5CA997DF7634F1F33176E0DDA8F7B485C6CD2EBC3BA06D4EEC8BB98284DB33761BA8A7668D1A5C140384968A0BE3436067F10A0D6B7F5AAFFF201547201944000C41ED1512DA596726D8E19A592BBA5573D31174C424FDFD7A0D14B3088BD22F0EB520F99E19D78DBBD613B79277FEB2BD0911C4C379E69B8688CC744B5B5ACF928F20174A201A470009442100CAC1C5F12BCCEA80C176FCCEEFEC616E86A9F208F43E45D49E8F32F76278B9F8202ABFC11D935F56D5CFECFDC66D4CA37D67C69AE6CD3C1DB41794C3C7FF41FE90201749201A4600094320656984EF23EAD4304E4A1AB3321F64BF9629FFE0E3A4097B181C2295892578D2205B90DAD8607D3BE600209771E2A19EC9EA3BB7BEE9D44A99395E85577FBCDBB7".hexStringToByteArray()
            val publicKeyBytes = "000C41ED1512DA596726D8E19A592BBA5573D31174C424FDFD7A0D14B3088BD22F0EB520F99E19D78DBBD613B79277FEB2BD0911C4C379E69B8688CC744B5B5ACF928F".hexStringToByteArray()
            val publicKey = object : XyoBuff() {
                override val allowedOffset: Int = 0
                override var item: ByteArray = publicKeyBytes
            }
            val createdBoundWitness = XyoBoundWitness.getInstance(boundWitnessBytes)
            val partyNumOfPublicKey = XyoBoundWitnessUtil.getPartyNumberFromPublicKey(createdBoundWitness, publicKey)

            Assert.assertEquals(1, partyNumOfPublicKey)
        }
    }
}