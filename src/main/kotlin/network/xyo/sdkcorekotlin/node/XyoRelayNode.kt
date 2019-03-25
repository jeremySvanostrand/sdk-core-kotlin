package network.xyo.sdkcorekotlin.node

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import network.xyo.sdkcorekotlin.boundWitness.XyoBoundWitness
import network.xyo.sdkcorekotlin.hashing.XyoHash
import network.xyo.sdkcorekotlin.network.XyoNetworkPipe
import network.xyo.sdkcorekotlin.network.XyoNetworkProcedureCatalogueInterface
import network.xyo.sdkcorekotlin.network.XyoProcedureCatalogue
import network.xyo.sdkcorekotlin.persist.XyoStorageProviderInterface
import network.xyo.sdkcorekotlin.repositories.XyoBridgeQueueRepository
import network.xyo.sdkcorekotlin.repositories.XyoOriginBlockRepository
import network.xyo.sdkcorekotlin.repositories.XyoOriginChainStateRepository
import java.nio.ByteBuffer
import kotlin.concurrent.thread

/**
 * A base class for nodes creating data, then relaying it (e.g.) sentinels and bridges.
 *
 * @param storageProvider A place to store all origin blocks.
 * @property hashingProvider A hashing provider to use hashing utilises.
 */
abstract class XyoRelayNode (storageProvider : XyoStorageProviderInterface,
                             blockRepository: XyoOriginBlockRepository,
                             stateRepository: XyoOriginChainStateRepository,
                             bridgeQueueRepository: XyoBridgeQueueRepository,
                             private val hashingProvider : XyoHash.XyoHashProvider) : XyoOriginChainCreator(blockRepository, stateRepository, hashingProvider) {

    val originBlocksToBridge = XyoBridgeQueue(bridgeQueueRepository)
    private val selfToOtherQueue = XyoBridgingOption(blockRepository, originBlocksToBridge)

    private val mainBoundWitnessListener = object : XyoNodeListener() {
        override fun onBoundWitnessEndFailure(error: Exception?) {}

        override fun onBoundWitnessEndSuccess(boundWitness: XyoBoundWitness) {
            for (hash in originBlocksToBridge.getBlocksToRemove()) {
                runBlocking {
                    blockRepository.removeOriginBlock(hash).await()
                }
            }
        }

        override fun onBoundWitnessDiscovered(boundWitness: XyoBoundWitness) {
            runBlocking {
                originBlocksToBridge.addBlock(boundWitness.getHash(hashingProvider).await())
            }
        }

        override fun onBoundWitnessStart() {}
    }


    /**
     * The nodes procedureCatalogue to advertise when it makes connections.
     */
    abstract val procedureCatalogue : XyoNetworkProcedureCatalogueInterface

    /**
     * Gets a XyoNetworkPipe to have a session with.
     *
     * @return The pipe to have a session with.
     */
    abstract suspend fun findSomeoneToTalkTo() : XyoNetworkPipe


    private fun canDo (catalog: Int, item : Int) : Boolean {
        return catalog and item == item
                && procedureCatalogue.canDo(ByteBuffer.allocate(4).putInt(item).array())
    }

    override fun getChoice(catalog: Int, strict: Boolean): Int {
        if (canDo(XyoProcedureCatalogue.TAKE_ORIGIN_CHAIN, catalog)) {
            return  XyoProcedureCatalogue.GIVE_ORIGIN_CHAIN
        } else if (canDo(XyoProcedureCatalogue.GIVE_ORIGIN_CHAIN, catalog)) {
            return XyoProcedureCatalogue.TAKE_ORIGIN_CHAIN
        }
        return XyoProcedureCatalogue.BOUND_WITNESS
    }

    init {
        addListener(this.toString(), mainBoundWitnessListener)
        addBoundWitnessOption(selfToOtherQueue)
    }
}
