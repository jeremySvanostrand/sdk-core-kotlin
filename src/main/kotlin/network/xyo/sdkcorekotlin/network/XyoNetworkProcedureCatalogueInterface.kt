package network.xyo.sdkcorekotlin.network

import network.xyo.sdkobjectmodelkotlin.buffer.XyoBuff

/**
 * Used for advertising what a device can do and support
 */
interface XyoNetworkProcedureCatalogueInterface {
    fun canDo(byteArray: ByteArray) : Boolean

    fun getEncodedCanDo() : ByteArray

    fun choose (byteArray: ByteArray): ByteArray

    fun getNetworlHuerestics (): Array<XyoBuff>
}