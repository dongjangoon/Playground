package msa.common.extension

import org.springframework.data.domain.Sort
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import kotlin.reflect.KProperty

infix fun Criteria.isEqualTo(o: Any?): Criteria = this.`is`(o)

infix fun <T> KProperty<T>.isEqualTo(value: T): Criteria = Criteria(this.toDotPath()).isEqualTo(value)

infix fun Criteria.orderByRandom(sampleSize: Int): Criteria = this.apply { "\$sample" to sampleSize }

fun Query.withRandomOrder(): Query = this.with(Sort.by(Sort.Order.by("\$sample")))

// Nickname 관련 확장 함수
fun Query.whereNotUsed(): Query = this.addCriteria(Criteria.where("isUsed").isEqualTo(false))

fun Query.whereCombinedNameIs(name: String): Query = this.addCriteria(Criteria.where("combinedName").isEqualTo(name))
