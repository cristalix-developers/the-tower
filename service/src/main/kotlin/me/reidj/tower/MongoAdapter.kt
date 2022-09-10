package me.reidj.tower

import com.mongodb.ClientSessionOptions
import com.mongodb.async.client.MongoClient
import com.mongodb.async.client.MongoClients
import com.mongodb.async.client.MongoCollection
import com.mongodb.client.model.*
import com.mongodb.client.model.Aggregates.*
import com.mongodb.client.model.Indexes.ascending
import com.mongodb.client.model.Indexes.hashed
import com.mongodb.session.ClientSession
import kotlinx.coroutines.future.await
import me.reidj.tower.data.Stat
import me.reidj.tower.top.PlayerTopEntry
import me.reidj.tower.top.TopEntry
import me.reidj.tower.util.UtilCristalix
import org.bson.Document
import ru.cristalix.core.GlobalSerializers
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.network.packages.BulkGroupsPackage
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit


/**
 * @project : forest
 * @author : Рейдж
 **/
open class MongoAdapter(dbUrl: String, dbName: String, collection: String) {

    private var data: MongoCollection<Document>

    private val upsert = UpdateOptions().upsert(true)
    private val mongoClient: MongoClient
    private val session: ClientSession

    init {
        val future = CompletableFuture<ClientSession>()
        mongoClient = MongoClients.create(dbUrl).apply {
            startSession(ClientSessionOptions.builder().causallyConsistent(true).build()) { response, throwable ->
                if (throwable != null) future.completeExceptionally(throwable) else future.complete(response)
            }
        }
        data = mongoClient.getDatabase(dbName).getCollection(collection)
        session = future.get(10, TimeUnit.SECONDS)
    }

    fun find(uuid: UUID) = CompletableFuture<Stat?>().apply {
        data.find(session, Filters.eq("uuid", uuid.toString())).first { result: Document?, _: Throwable? ->
            try {
                complete(readDocument(result))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun findAll(): CompletableFuture<Map<UUID, Stat>> {
        val future = CompletableFuture<Map<UUID, Stat>>()
        val documentFindIterable = data.find()
        val map = ConcurrentHashMap<UUID, Stat>()
        documentFindIterable.forEach({ document: Document ->
            val obj: Stat? = readDocument(document)
            if (obj != null)
                map[obj.uuid] = obj
        }) { _: Void, _: Throwable -> future.complete(map) }
        return future
    }

    private fun readDocument(document: Document?) =
        if (document == null) null else GlobalSerializers.fromJson(document.toJson(), Stat::class.java)

    fun save(stat: Stat) = save(listOf(stat))

    fun save(stats: List<Stat>) {
        mutableListOf<WriteModel<Document>>().apply {
            stats.forEach {
                add(
                    UpdateOneModel(
                        Filters.eq("uuid", it.uuid.toString()),
                        Document("\$set", Document.parse(GlobalSerializers.toJson(it))),
                        upsert
                    )
                )
            }
        }.run {
            if (isNotEmpty())
                data.bulkWrite(session, this) { _, throwable: Throwable? -> handle(throwable) }
        }
    }

    private fun handle(throwable: Throwable?) = throwable?.printStackTrace()

    open fun <V> makeRatingByField(fieldName: String, limit: Int, isSortAscending: Boolean): List<TopEntry<Stat, V>> {
        val entries = ArrayList<TopEntry<Stat, V>>()
        val future: CompletableFuture<List<TopEntry<Stat, V>>> = CompletableFuture<List<TopEntry<Stat, V>>>()

        data.createIndex(hashed("_id")) { _, _ -> }
        data.createIndex(hashed("uuid")) { _, _ -> }
        data.createIndex(ascending(fieldName)) { _, _ -> }

        data.aggregate(listOf(
            project(
                Projections.fields(
                    Projections.include(fieldName),
                    Projections.include("uuid"),
                    Projections.exclude("_id")
                )
            ), sort(if (isSortAscending) Sorts.ascending(fieldName) else Sorts.descending(fieldName)),
            limit(limit)
        )).forEach({ document: Document ->
            if (readDocument(document) == null) {
                throw NullPointerException("Document is null")
            }
            entries.add(TopEntry(readDocument(document)!!, document[fieldName] as V))
        }) { _: Void?, throwable: Throwable? ->
            if (throwable != null) {
                future.completeExceptionally(throwable)
                return@forEach
            }
            future.complete(entries)
        }
        return future.get()
    }

    suspend fun getTop(topType: String, limit: Int, isSortAscending: Boolean): List<PlayerTopEntry<Any>> {
        val entries = makeRatingByField<String>(topType, limit, isSortAscending)
        val playerEntries = mutableListOf<PlayerTopEntry<Any>>()

        entries.forEach { it.key.let { stat -> playerEntries.add(PlayerTopEntry(stat, it.value)) } }

        try {
            val uuids = arrayListOf<UUID>()

            entries.forEach { uuids.add(it.key.uuid) }

            val map = ISocketClient.get()
                .writeAndAwaitResponse<BulkGroupsPackage>(BulkGroupsPackage(uuids))
                .await().groups.associateBy { it.uuid }

            playerEntries.forEach {
                map[it.key.uuid]?.let {data ->
                    it.userName = data.username
                    it.displayName = UtilCristalix.createDisplayName(data)
                }
            }
        } catch (exception: java.lang.Exception) {
            exception.printStackTrace()
            playerEntries.forEach {
                it.userName = "ERROR"
                it.displayName = "ERROR"
            }
        }
        return playerEntries.map {
            PlayerTopEntry(it.key, it.value).also { new ->
                new.displayName = it.displayName
                new.userName = it.userName
            }
        }
    }
}