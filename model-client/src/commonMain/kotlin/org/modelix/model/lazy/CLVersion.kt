/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.modelix.model.lazy

import org.modelix.model.api.INodeReference
import org.modelix.model.api.LocalPNodeReference
import org.modelix.model.api.PNodeReference
import org.modelix.model.operations.*
import org.modelix.model.persistent.CPOperationsList
import org.modelix.model.persistent.CPVersion

class CLVersion {
    var store: NonWrittenEntriesStore
    var data: CPVersion? = null
        private set
    val treeHash: String?

    private constructor(
        id: Long,
        time: String?,
        author: String?,
        tree: CLTree,
        previousVersion: CLVersion?,
        originalVersion: CLVersion?,
        baseVersion: CLVersion?,
        mergedVersion1: CLVersion?,
        mergedVersion2: CLVersion?,
        operations: Array<IOperation>
    ) {
        this.store = tree.store
        this.treeHash = tree.hash
        val localizedOps = localizeOps(operations.asList()).toTypedArray()
        val nonWrittenChildren = listOf(
            tree.store,
            previousVersion?.store,
            originalVersion?.store,
            baseVersion?.store,
            mergedVersion1?.store,
            mergedVersion2?.store
        ).mapNotNull { it?.entry }
        if (localizedOps.size <= 10) {
            data = CPVersion(
                id = id,
                time = time,
                author = author,
                treeHash = tree.hash,
                previousVersion = previousVersion?.hash,
                originalVersion = originalVersion?.hash,
                baseVersion = baseVersion?.hash,
                mergedVersion1 = mergedVersion1?.hash,
                mergedVersion2 = mergedVersion2?.hash,
                operations = localizedOps,
                operationsHash = null,
                numberOfOperations = localizedOps.size
            )
            this.store = tree.store.with(data!!, data!!.serialize(), nonWrittenChildren)
        } else {
            val opsList = CPOperationsList(localizedOps)
            val opsListEntry = NonWrittenEntry(opsList.serialize(), opsList, listOf())
            data = CPVersion(
                id = id,
                time = time,
                author = author,
                treeHash = tree?.hash,
                previousVersion = previousVersion?.hash,
                originalVersion = originalVersion?.hash,
                baseVersion = baseVersion?.hash,
                mergedVersion1 = mergedVersion1?.hash,
                mergedVersion2 = mergedVersion2?.hash,
                operations = null,
                operationsHash = opsList.hash,
                numberOfOperations = localizedOps.size
            )
            this.store = tree.store.with(data!!, data!!.serialize(), nonWrittenChildren + opsListEntry)
        }
        write()
    }

    constructor(hash: String, store: IDeserializingKeyValueStore) : this(store.get<CPVersion>(hash, { CPVersion.deserialize(it) }), NonWrittenEntriesStore.create(store))
    constructor(data: CPVersion?, store: NonWrittenEntriesStore) {
        if (data == null) {
            throw NullPointerException("data is null")
        }
        this.data = data
        this.treeHash = data.treeHash
        this.store = store
    }

    val author: String?
        get() = data!!.author

    val id: Long
        get() = data!!.id

    val time: String?
        get() = data!!.time

    val hash: String
        get() = data!!.hash

    val previousHash: String?
        get() = data!!.previousVersion

    val tree: CLTree
        get() = CLTree(treeHash, treeHash?.let { store.findStore(it) } ?: store)

    val baseVersion: CLVersion?
        get() {
            val previousVersionHash = data!!.baseVersion ?: data!!.previousVersion ?: return null
            val previousVersion = store[previousVersionHash, { input: String -> CPVersion.deserialize(input) }]
                ?: return null
            return CLVersion(previousVersion, store)
        }

    val operations: Iterable<IOperation>
        get() {
            val operationsHash = data!!.operationsHash
            val ops = if (operationsHash == null) data!!.operations else
            (
                store[operationsHash, { CPOperationsList.deserialize(it) }]
                    ?: throw RuntimeException("Missing entry for key $operationsHash")
                )
                .operations
            return globalizeOps((ops ?: arrayOf()).toList())
        }

    val numberOfOperations: Int
        get() = data!!.numberOfOperations

    fun operationsInlined(): Boolean {
        return data!!.operations != null
    }

    fun isMerge() = this.data!!.mergedVersion1 != null

    fun getMergedVersion1() = this.data!!.mergedVersion1?.let { CLVersion.loadFromHash(it, store) }
    fun getMergedVersion2() = this.data!!.mergedVersion2?.let { CLVersion.loadFromHash(it, store) }

    fun write(): String {
        store.write()
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CLVersion

        if (data?.id != other.data?.id) return false

        return true
    }

    override fun hashCode(): Int {
        return data?.id?.hashCode() ?: 0
    }

    companion object {
        fun createAutoMerge(
            id: Long,
            tree: CLTree,
            baseVersion: CLVersion,
            mergedVersion1: CLVersion,
            mergedVersion2: CLVersion,
            operations: Array<IOperation>,
            store: NonWrittenEntriesStore
        ) = CLVersion(
            id = id,
            time = null,
            author = null,
            tree = tree,
            previousVersion = null,
            originalVersion = null,
            baseVersion = baseVersion,
            mergedVersion1 = mergedVersion1,
            mergedVersion2 = mergedVersion2,
            operations = operations
        )

        fun createRegularVersion(
            id: Long,
            time: String?,
            author: String?,
            tree: CLTree,
            baseVersion: CLVersion?,
            operations: Array<IOperation>
        ): CLVersion = CLVersion(
            id = id,
            time = time,
            author = author,
            tree = tree,
            previousVersion = null,
            originalVersion = null,
            baseVersion = baseVersion,
            mergedVersion1 = null,
            mergedVersion2 = null,
            operations = compressOperations(operations, tree)
        )

        fun loadFromHash(hash: String, store: NonWrittenEntriesStore): CLVersion {
            val data = store[hash, { CPVersion.deserialize(it) }]
                ?: throw RuntimeException("Version with hash $hash not found")
            return CLVersion(data, store)
        }

        /**
         * Optimize for bulk imports
         * If a whole subtree is imported then there are a lot of operations where only the AddNewChildOp for the subtree
         * root has the potential to cause any conflict.
         * In that case we replace all of these operation with one AddNewChildSubtreeOp that references the resulting
         * subtree in the new version. We don't lose any information and can reconstruct the original operations if needed.
         */
        private fun compressOperations(ops: Array<IOperation>, resultTree: CLTree): Array<IOperation> {
            val compressedOps: MutableList<IOperation> = ArrayList()
            val createdNodes: MutableSet<Long> = HashSet()

            for (op in ops) {
                if (op is UndoOp) return ops

                when (op) {
                    is AddNewChildOp -> {
                        createdNodes.add(op.childId)
                        val effectivelyAddedToSubtree =
                            createdNodes.contains(op.childId) &&
                                resultTree.containsNode(op.childId) &&
                                createdNodes.contains(resultTree.getParent(op.childId))
                        if (!effectivelyAddedToSubtree) {
                            compressedOps += AddNewChildSubtreeOp(resultTree.hash, op.position, op.childId, op.concept)
                        }
                    }
                    is DeleteNodeOp -> {
                        createdNodes -= op.childId
                        compressedOps += op
                    }
                    is SetPropertyOp -> {
                        if (!createdNodes.contains(op.nodeId)) {
                            compressedOps += op
                        }
                    }
                    is SetReferenceOp -> {
                        if (!createdNodes.contains(op.sourceId)) {
                            compressedOps += op
                        }
                    }
                    is MoveNodeOp -> {
                        val effectivelyAddedToSubtree =
                            createdNodes.contains(op.childId) &&
                                resultTree.containsNode(op.childId) &&
                                createdNodes.contains(resultTree.getParent(op.childId))
                        if (!effectivelyAddedToSubtree) {
                            compressedOps += op
                        }
                    }
                    else -> compressedOps += op
                }
            }

            // if we save less than 10 operations then it's probably not worth doing the replacement
            return if (ops.size - compressedOps.size >= 10) compressedOps.toTypedArray() else ops
        }
    }

    private fun globalizeOps(ops: List<IOperation>): List<IOperation> {
        return ops.map {
            when (it) {
                is SetReferenceOp -> it.withTarget(globalizeNodeRef(it.target))
                else -> it
            }
        }
    }

    private fun globalizeNodeRef(ref: INodeReference?): INodeReference? {
        return when (ref) {
            null -> null
            is LocalPNodeReference -> ref.toGlobal(tree.getId())
            else -> ref
        }
    }

    private fun localizeNodeRef(ref: INodeReference?): INodeReference? {
        return if (ref is PNodeReference && ref.branchId == tree.getId()) ref.toLocal() else ref
    }

    private fun localizeOps(ops: List<IOperation>): List<IOperation> {
        return ops.map {
            when (it) {
                is SetReferenceOp -> it.withTarget(localizeNodeRef(it.target))
                else -> it
            }
        }
    }
}
