package li.dic.tourphone.domain

import java.util.*

data class LocalExcursions(
    var date: Date?,
    var day: String,
    var list: List<LocalExcursion>
)