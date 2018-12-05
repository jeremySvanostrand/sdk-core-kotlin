package network.xyo.sdkcorekotlin.boundWitness

import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.XyoTestBase
import network.xyo.sdkcorekotlin.hashing.basic.XyoBasicHashBase
import network.xyo.sdkcorekotlin.schemas.XyoSchemas
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class XyoBoundWitnessInterpretTest : XyoTestBase() {

    @Test
    fun testInterpreterBoundWitness () {
        val boundWitnessBytes = BigInteger("A00200000186A0150000005DA0010000004A800C000000444D43F25283CB7EE48EF3C45052128DDC1C56661E40FC5A03897E9740A3062511E40E79AEE4C25BFAF6E6C963E5F14C2DF847130F81EB725CC933166112AD5850A0010000000B801300000005AAA0150000005DA0010000004A800C0000004425E192D7431ADB169A90AA7EC380E5A65D76710678C0F01B8158003B638DF10A13D2C1AD11DD385DD14BEA2BCC429B0A762D5669A3010877A5CAFA26191954B1A0010000000B801300000005BBA01600000061A0010000004E8009000000482100C5EBCED4774BA74A94617C60C75ADBC366CFE74B2D9695436F32C9E1B6CEFF1A2100F1D48DD03829C88641891F555B3FB9ED90B0E2781E7F9706A2438F111A277E53A0010000000B801300000005BBA0160000005FA0010000004C8009000000462061E890441C3E98DD3AD2E4CBE65837EB0C36F7CB75C9038DF764912E317C69FF20785069BC2F21BEF393788F98B514DB354D3F7DC1B2B15BB1C132E640E0D32AF3A0010000000B801300000005AA", 16).toByteArray()
        val expectedFetterOne = "A0150000005DA0010000004A800C000000444D43F25283CB7EE48EF3C45052128DDC1C56661E40FC5A03897E9740A3062511E40E79AEE4C25BFAF6E6C963E5F14C2DF847130F81EB725CC933166112AD5850A0010000000B801300000005AA".hexStringToByteArray()
        val expectedWitnessOne = "A01600000061A0010000004E8009000000482100C5EBCED4774BA74A94617C60C75ADBC366CFE74B2D9695436F32C9E1B6CEFF1A2100F1D48DD03829C88641891F555B3FB9ED90B0E2781E7F9706A2438F111A277E53A0010000000B801300000005BB".hexStringToByteArray()
        val expectedFetterTwo = "A0150000005DA0010000004A800C0000004425E192D7431ADB169A90AA7EC380E5A65D76710678C0F01B8158003B638DF10A13D2C1AD11DD385DD14BEA2BCC429B0A762D5669A3010877A5CAFA26191954B1A0010000000B801300000005BB".hexStringToByteArray()
        val expectedWitnessTwo = "A0150000005DA0010000004A800C0000004425E192D7431ADB169A90AA7EC380E5A65D76710678C0F01B8158003B638DF10A13D2C1AD11DD385DD14BEA2BCC429B0A762D5669A3010877A5CAFA26191954B1A0010000000B801300000005BB".hexStringToByteArray()
        val createdBoundWitness = XyoBoundWitness.getInstance(boundWitnessBytes.copyOfRange(1, boundWitnessBytes.size))

        Assert.assertEquals(2, createdBoundWitness[XyoSchemas.FETTER.id].size)
        Assert.assertEquals(2, createdBoundWitness[XyoSchemas.WITNESS.id].size)
        Assert.assertEquals(2, createdBoundWitness.numberOfParties)
        Assert.assertArrayEquals(expectedFetterOne, createdBoundWitness.getFetterOfParty(0)?.bytesCopy)
        Assert.assertArrayEquals(expectedWitnessOne, createdBoundWitness.getWitnessOfParty(0)?.bytesCopy)
        Assert.assertArrayEquals(expectedFetterTwo, createdBoundWitness.getFetterOfParty(1)?.bytesCopy)
        Assert.assertArrayEquals(expectedWitnessTwo, createdBoundWitness.getWitnessOfParty(1)?.bytesCopy)
    }

    @Test
    fun testGetSigningData () {
        val boundWitnessBytes = BigInteger("A00200000186A0150000005DA0010000004A800C000000444D43F25283CB7EE48EF3C45052128DDC1C56661E40FC5A03897E9740A3062511E40E79AEE4C25BFAF6E6C963E5F14C2DF847130F81EB725CC933166112AD5850A0010000000B801300000005AAA0150000005DA0010000004A800C0000004425E192D7431ADB169A90AA7EC380E5A65D76710678C0F01B8158003B638DF10A13D2C1AD11DD385DD14BEA2BCC429B0A762D5669A3010877A5CAFA26191954B1A0010000000B801300000005BBA01600000061A0010000004E8009000000482100C5EBCED4774BA74A94617C60C75ADBC366CFE74B2D9695436F32C9E1B6CEFF1A2100F1D48DD03829C88641891F555B3FB9ED90B0E2781E7F9706A2438F111A277E53A0010000000B801300000005BBA0160000005FA0010000004C8009000000462061E890441C3E98DD3AD2E4CBE65837EB0C36F7CB75C9038DF764912E317C69FF20785069BC2F21BEF393788F98B514DB354D3F7DC1B2B15BB1C132E640E0D32AF3A0010000000B801300000005AA", 16).toByteArray()
        val expectedSigningBytes = BigInteger("A0150000005DA0010000004A800C000000444D43F25283CB7EE48EF3C45052128DDC1C56661E40FC5A03897E9740A3062511E40E79AEE4C25BFAF6E6C963E5F14C2DF847130F81EB725CC933166112AD5850A0010000000B801300000005AAA0150000005DA0010000004A800C0000004425E192D7431ADB169A90AA7EC380E5A65D76710678C0F01B8158003B638DF10A13D2C1AD11DD385DD14BEA2BCC429B0A762D5669A3010877A5CAFA26191954B1A0010000000B801300000005BB", 16).toByteArray()
        val createdBoundWitness = XyoBoundWitness.getInstance(boundWitnessBytes.copyOfRange(1, boundWitnessBytes.size))

        Assert.assertArrayEquals(expectedSigningBytes.copyOfRange(1, expectedSigningBytes.size), createdBoundWitness.signingData)
    }


    @Test
    fun testBoundWitnessHash () {
        runBlocking {
            val boundWitnessHash = BigInteger("DA46A02BEF33A07827129FC2C317201ACDABAA49849608B5F596B094A2F207C0", 16).toByteArray()
            val boundWitnessBytes = BigInteger("A00200000186A0150000005DA0010000004A800C000000444D43F25283CB7EE48EF3C45052128DDC1C56661E40FC5A03897E9740A3062511E40E79AEE4C25BFAF6E6C963E5F14C2DF847130F81EB725CC933166112AD5850A0010000000B801300000005AAA0150000005DA0010000004A800C0000004425E192D7431ADB169A90AA7EC380E5A65D76710678C0F01B8158003B638DF10A13D2C1AD11DD385DD14BEA2BCC429B0A762D5669A3010877A5CAFA26191954B1A0010000000B801300000005BBA01600000061A0010000004E8009000000482100C5EBCED4774BA74A94617C60C75ADBC366CFE74B2D9695436F32C9E1B6CEFF1A2100F1D48DD03829C88641891F555B3FB9ED90B0E2781E7F9706A2438F111A277E53A0010000000B801300000005BBA0160000005FA0010000004C8009000000462061E890441C3E98DD3AD2E4CBE65837EB0C36F7CB75C9038DF764912E317C69FF20785069BC2F21BEF393788F98B514DB354D3F7DC1B2B15BB1C132E640E0D32AF3A0010000000B801300000005AA", 16).toByteArray()
            val createdBoundWitness = XyoBoundWitness.getInstance(boundWitnessBytes.copyOfRange(1, boundWitnessBytes.size))
            val hashProvider = XyoBasicHashBase.createHashType(XyoSchemas.SHA_256, "SHA-256")

            val hash = createdBoundWitness.getHash(hashProvider).await().hash
            Assert.assertArrayEquals(boundWitnessHash.copyOfRange(1, boundWitnessHash.size), hash)
        }
    }
}