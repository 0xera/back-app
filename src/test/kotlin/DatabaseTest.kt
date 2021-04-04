
import com.example.database.git.BackupDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test


class DatabaseTest {

    private lateinit var database: BackupDatabase

    @Before
    fun setUp() {
//        val driver = JdbcSqliteDriver(IN_MEMORY)
//        BackupDatabase.Schema.create(driver)
//        database = BackupDatabase(driver, Fix.Adapter(DateAdapter()))
    }

    @Test
    fun `insert in author table`() {

        database.authorQueries.insert("test_nickname", "test-email")
        val lastId = database.authorQueries.lastInsertRowId().executeAsOne()
        val author = database.authorQueries.get(lastId).executeAsOne()

        assertThat(author.nickname).isEqualTo("test_nickname")
        assertThat(author.email).isEqualTo("test-email")
    }


    @Test
    fun `delete fix triggers`() {

        database.authorQueries.insert("test_nickname", "test-email")
        val authorId = database.authorQueries.lastInsertRowId().executeAsOne()
        database.repoQueries.insert("new_repo")
        val repoId = database.repoQueries.lastInsertRowId().executeAsOne()


//        database.fixQueries.insert("sample", authorId, repoId)
        val fixId = database.fixQueries.lastInsertRowId().executeAsOne()

//        database.fileQueries.insert("name", ByteArray(10), repoId)
        var fileId = database.fileQueries.lastInsertRowId().executeAsOne()
        database.fileToFixQueries.insert(fixId, fileId)

//        database.fileQueries.insert("name", ByteArray(10), repoId)
        fileId = database.fileQueries.lastInsertRowId().executeAsOne()
        database.fileToFixQueries.insert(fixId, fileId)

        assertThat(database.fixQueries.getAll(repoId).executeAsList()).hasSize(1)
        assertThat(database.fileQueries.getAll(repoId).executeAsList()).hasSize(2)
        assertThat(database.fileToFixQueries.getAll().executeAsList()).hasSize(2)

        val lastFixId = database.fixQueries.lastInsertRowId().executeAsOne()
        assertThat(lastFixId).isEqualTo(fixId)
        database.fixQueries.delete(fixId)

        assertThat(database.fixQueries.getAll(repoId).executeAsList()).isEmpty()
        assertThat(database.fileQueries.getAll(repoId).executeAsList()).isEmpty()
        assertThat(database.fileToFixQueries.getAll().executeAsList()).isEmpty()

    }


    @Test
    fun `stateflow emit`() = runBlocking {
        val mutableStateFlow = MutableStateFlow(0)
        val list = mutableListOf<Int>()
        val job = launch {
            mutableStateFlow.toList(list)
        }
        mutableStateFlow.value = 1
        mutableStateFlow.value = 2
        mutableStateFlow.value = 1
        mutableStateFlow.value = 3
        job.cancel()
        assertThat(list).isEqualTo(listOf(0, 1, 2, 1, 3))
    }

}