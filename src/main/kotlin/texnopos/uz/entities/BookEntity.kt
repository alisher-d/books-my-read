package texnopos.uz.entities

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object BookEntity:Table<Nothing>(tableName = "books"){
    val id=int("id").primaryKey()
    val bookName=varchar("bookName")
    val author=varchar("author")
    val posterId=int("posterId")
    val createdAt=long("createdAt")
}