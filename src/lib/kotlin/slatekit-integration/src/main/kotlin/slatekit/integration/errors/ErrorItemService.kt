package slatekit.integration.errors

import slatekit.apis.ApiConstants
import slatekit.apis.ApiContainer
import slatekit.apis.ApiHostAware
import slatekit.apis.core.Requests
import slatekit.common.*
import slatekit.entities.core.Entities
import slatekit.integration.common.AppEntContext

class ErrorItemService(ctx: AppEntContext, entities: Entities, repo: slatekit.entities.core.EntityRepo<ErrorItem>)
    : slatekit.entities.support.EntityServiceWithSupport<ErrorItem>(ctx, entities, repo), ApiHostAware {

    private var container: ApiContainer? = null
    override fun setApiHost(host: ApiContainer) {
        container = host
    }

    fun retryById(id: Long, deleteOnSuccess: Boolean): ResultMsg<String> {
        val item = get(id)
        return item?.let { retry(it, deleteOnSuccess) } ?: Success("No errors present")
    }

    fun retryLast(deleteOnSuccess: Boolean): ResultMsg<String> {
        val item = last()
        return item?.let { retry(it, deleteOnSuccess) } ?: Success("No errors present")
    }

    fun retryRecent(count: Int, deleteOnSuccess: Boolean): ResultMsg<String> {
        val recent = recent(count)
        val results = recent.map { it -> retry(it, deleteOnSuccess) }
        val success = results.all { it.success }
        val message = results.map { if (it.success) "" else it.msg }
                            .filter { it != "" }
                            .joinToString { newline }
        val result = if (success) Success("") else Failure(message)
        return result
    }

    fun retryByWorker(deleteOnSuccess: Boolean): ResultMsg<String> {
        return Success("")
    }

    fun retry(itemRaw: ErrorItem, deleteOnSuccess: Boolean): ResultMsg<String> {

        // Move item to retry state
        val item = itemRaw.copy(status = ErrorItemStatus.Retrying)
        update(item)

        // Now process
        val requestJson = item.request
        val request = Requests.fromJson(requestJson, ApiConstants.SourceQueue, ApiConstants.SourceQueue)
        val result: Result<Any, Exception> = this.container?.let { c ->
            val res = c.callAsResult(request)
            if (res.success) {

                if (deleteOnSuccess) {
                    delete(item)
                } else {
                    val itemUpdated = item.copy(status = ErrorItemStatus.Succeeded)
                    update(itemUpdated)
                }
                res
            } else {
                val itemUpdated = item.copy(status = ErrorItemStatus.Active, retries = item.retries + 1)
                update(itemUpdated)
                res
            }
        } ?: Failure(Exception("ApiContainer not set for ErrorItems"))
        val finalResult = result.transform({ it -> Success(it.toString()) }, { err -> Failure(err.toString()) })
        return finalResult
    }
}
