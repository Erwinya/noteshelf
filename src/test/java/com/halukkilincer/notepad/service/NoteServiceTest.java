package com.halukkilincer.notepad.service;

import com.halukkilincer.notepad.model.Note;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoteServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void addAndPersistNotes() {
        Path saveFile = tempDir.resolve("notes.ser");
        NoteService service = new NoteService(saveFile);
        service.addNote(new Note("Ideas", "Write clean code"));
        service.addNote(new Note("Todo", "Ship the app"));
        service.saveAllNotes();

        NoteService reloaded = new NoteService(saveFile);
        reloaded.loadAllNotes();

        assertEquals(2, reloaded.getNoteCount());
        assertEquals("Ideas", reloaded.getNote(0).getTitle());
        assertEquals("Write clean code", reloaded.getNote(0).getContent());
        assertTrue(Files.exists(saveFile));
    }

    @Test
    void rejectsNullNote() {
        NoteService service = new NoteService(tempDir.resolve("notes.ser"));
        assertThrows(NullPointerException.class, () -> service.addNote(null));
    }

    @Test
    void updateNoteRequiresValidIndex() {
        NoteService service = new NoteService(tempDir.resolve("notes.ser"));
        service.addNote(Note.of("One", "body"));
        assertThrows(IndexOutOfBoundsException.class,
                () -> service.updateNote(3, Note.of("X", "y")));
    }

    @Test
    void nextUntitledTitleAvoidsCollisions() {
        NoteService service = new NoteService(tempDir.resolve("notes.ser"));
        assertEquals("New Note", service.nextUntitledTitle());

        service.addNote(Note.of("New Note", ""));
        assertEquals("New Note (2)", service.nextUntitledTitle());

        service.addNote(Note.of("New Note (2)", ""));
        assertEquals("New Note (3)", service.nextUntitledTitle());
    }

    @Test
    void updateAndRemovePersist() {
        Path saveFile = tempDir.resolve("notes.ser");
        NoteService service = new NoteService(saveFile);
        service.addNote(Note.of("Draft", "v1"));
        service.updateNote(0, Note.of("Draft", "v2"));
        service.addNote(Note.of("Scratch", "tmp"));
        service.removeNote(service.getNote(1));
        service.saveAllNotes();

        NoteService reloaded = new NoteService(saveFile);
        reloaded.loadAllNotes();
        assertEquals(1, reloaded.getNoteCount());
        assertEquals("v2", reloaded.getNote(0).getContent());
    }
}
