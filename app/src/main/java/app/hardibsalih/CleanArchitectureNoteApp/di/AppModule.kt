package app.hardibsalih.CleanArchitectureNoteApp.di

import android.app.Application
import androidx.room.Room
import app.hardibsalih.CleanArchitectureNoteApp.feature_note.data.data_source.NoteDatabase
import app.hardibsalih.CleanArchitectureNoteApp.feature_note.data.repository.NoteRepositoryImpl
import app.hardibsalih.CleanArchitectureNoteApp.feature_note.domain.repository.NoteRepository
import app.hardibsalih.CleanArchitectureNoteApp.feature_note.domain.use_case.AddNote
import app.hardibsalih.CleanArchitectureNoteApp.feature_note.domain.use_case.DeleteNote
import app.hardibsalih.CleanArchitectureNoteApp.feature_note.domain.use_case.GetNote
import app.hardibsalih.CleanArchitectureNoteApp.feature_note.domain.use_case.GetNotes
import app.hardibsalih.CleanArchitectureNoteApp.feature_note.domain.use_case.NoteUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(db: NoteDatabase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotes(repository),
            deleteNote = DeleteNote(repository),
            addNote = AddNote(repository),
            getNote = GetNote(repository)
        )
    }



}