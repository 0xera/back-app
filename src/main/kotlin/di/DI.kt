package di

import com.example.database.git.BackupDatabase
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import data.RepositoryImpl
import database.FileData
import database.Fix
import domain.Repository
import domain.usecases.*
import org.kodein.di.*
import presentetion.Store
import utils.DateAdapter
import utils.StatusAdapter
import java.io.File

private val useCaseModule = DI.Module(name = "UseCaseModule") {
    bind<AuthorUseCase>() with provider { AuthorUseCase(instance()) }
    bind<CheckCommandUseCase>() with provider { CheckCommandUseCase() }
    bind<ConfigureUseCase>() with provider { ConfigureUseCase(instance()) }
    bind<FixUseCase>() with provider { FixUseCase(instance(), instance()) }
    bind<ResetUseCase>() with provider { ResetUseCase(instance(), instance()) }
    bind<RollbackUseCase>() with provider { RollbackUseCase(instance(), instance()) }
}

private val repoModule = DI.Module(name = "RepoModule") {
    bind<Repository>() with provider { RepositoryImpl(instance()) }
}

private val presentationModule = DI.Module(name = "PresentationModule") {
    bind<Store>() with provider { Store(instance(), instance(), instance(), instance(), instance(), instance()) }
}

private val appModule = DI.Module(name = "AppModule") {
    bind<JdbcSqliteDriver>(tag = "driver") with singleton {
        val databasePath = File("BackupDatabase.db")
        val driver = JdbcSqliteDriver(url = "jdbc:sqlite:${databasePath.absolutePath}")
        BackupDatabase.Schema.create(driver)
        driver
    }
    bind<BackupDatabase>() with singleton {
        BackupDatabase(instance(tag = "driver"),
            FileData.Adapter(StatusAdapter()),
            Fix.Adapter(DateAdapter()))
    }
}

val di = DI {
    import(useCaseModule)
    import(appModule)
    import(repoModule)
    import(presentationModule)
}