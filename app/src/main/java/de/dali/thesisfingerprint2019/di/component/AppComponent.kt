package de.dali.thesisfingerprint2019.di.component


import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import de.dali.thesisfingerprint2019.AppController
import de.dali.thesisfingerprint2019.di.module.ActivityModule
import de.dali.thesisfingerprint2019.di.module.DbModule
import de.dali.thesisfingerprint2019.di.module.FragmentModule
import de.dali.thesisfingerprint2019.di.module.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DbModule::class,
        ViewModelModule::class,
        AndroidSupportInjectionModule::class,
        ActivityModule::class,
        FragmentModule::class]
)
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun dbModule(dbModule: DbModule): Builder

        fun build(): AppComponent

    }

    fun inject(appController: AppController)
}
